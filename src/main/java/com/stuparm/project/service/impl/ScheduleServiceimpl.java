package com.stuparm.project.service.impl;

import com.stuparm.project.exception.DatabaseStateException;
import com.stuparm.project.exception.NotFoundException;
import com.stuparm.project.exception.PlaneNotAssignedException;
import com.stuparm.project.model.*;
import com.stuparm.project.repository.GateEntityRepository;
import com.stuparm.project.repository.NativeQueryRepository;
import com.stuparm.project.repository.ScheduleEntityRepository;
import com.stuparm.project.repository.entity.GateEntity;
import com.stuparm.project.repository.entity.ScheduleEntity;
import com.stuparm.project.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScheduleServiceimpl implements ScheduleService {

    @Autowired
    private ScheduleEntityRepository scheduleEntityRepository;

    @Autowired
    private GateEntityRepository gateEntityRepository;

    @Autowired
    private NativeQueryRepository nativeQueryRepository;

    @Override
    public Schedule create(Schedule schedule, String user) {

        String gateNumber = schedule.getGateNumber();
        Optional<GateEntity> gateEntityOptional = gateEntityRepository.findByGateNumber(gateNumber);
        if (gateEntityOptional.isEmpty()) {
            throw new NotFoundException(String.format(NotFoundException.GATE_NOT_FOUND_FORMAT, gateNumber));
        }

        GateEntity gateEntity = gateEntityOptional.get();


        ScheduleEntity scheduleEntity = new ScheduleEntity();
        scheduleEntity.setUtcFrom(schedule.getFrom());
        scheduleEntity.setUtcTo(schedule.getTo());
        scheduleEntity.setStatus(schedule.getStatus().name().toLowerCase());
        scheduleEntity.setChangedBy(user);
        scheduleEntity.setUtcChange(LocalDateTime.now(ZoneId.of("Z")));
        scheduleEntity.setGateEntity(gateEntity);

        ScheduleEntity savedScheduleEntity = scheduleEntityRepository.save(scheduleEntity);

        Schedule savedSchedule = new Schedule();
        savedSchedule.setFrom(savedScheduleEntity.getUtcFrom());
        savedSchedule.setTo(savedScheduleEntity.getUtcTo());
        savedSchedule.setGateNumber(savedScheduleEntity.getGateEntity().getGateNumber());
        savedSchedule.setStatus(Status.valueOf(savedScheduleEntity.getStatus()));

        return savedSchedule;

    }

    @Override
    public Status checkStatus(Gate gate, LocalDateTime utc) {
        String gateNumber = gate.getGateNumber();
        Optional<GateEntity> gateEntityOptional = gateEntityRepository.findByGateNumber(gateNumber);
        if (gateEntityOptional.isEmpty()) {
            throw new NotFoundException(String.format(NotFoundException.GATE_NOT_FOUND_FORMAT, gate.getGateNumber()));
        }

        GateEntity gateEntity = gateEntityOptional.get();

        // instead of limit keyword (not supported in JPQL), use Pageable
        PageRequest limitOne = PageRequest.of(0,1);
        List<ScheduleEntity> scheduleEntityList = scheduleEntityRepository.findSchedulesForGateAtTime(gateEntity, utc, limitOne);

        if (scheduleEntityList.isEmpty())
            throw new DatabaseStateException(
                    String.format(DatabaseStateException.STATUS_DOESNT_EXIST_FORMAT, gateNumber, utc));

        ScheduleEntity scheduleEntity = scheduleEntityList.get(0); // get first because result has only one element
        String scheduleStatus = scheduleEntity.getStatus();


        Status status;
        Pattern pattern = Pattern.compile(Flight.FLIGHT_REGEX);
        Matcher matcher = pattern.matcher(scheduleStatus);

        if (matcher.matches()) {
            status = Status.plane;
        } else {
            status = Status.valueOf(scheduleStatus);
        }

        return status;

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public ScheduleSlot findSlotForGate(Flight flight, String user) {
        ScheduleSlot scheduleSlot = new ScheduleSlot();

        String flightNumber = flight.getFlightNumber();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Z"));

        Optional<ScheduleEntity> freeScheduleOptional = nativeQueryRepository.findFreeSchedule(now);
        if (freeScheduleOptional.isEmpty()) {
            scheduleSlot.setAvailable(false);
            return scheduleSlot;
        }

        ScheduleEntity freeScheduleEntity = freeScheduleOptional.get();

        // how long this gate will be available
        GateEntity gateEntity = freeScheduleEntity.getGateEntity();
        PageRequest limitOne = PageRequest.of(0,1);
        List<ScheduleEntity> busyScheduleEntityList = scheduleEntityRepository.
                findSchedulesForGateAfterTime(gateEntity, now, Status.busy.name(), limitOne);


        // save schedule entity, done by plane
        ScheduleEntity planeScheduleEntity = new ScheduleEntity();
        planeScheduleEntity.setGateEntity(freeScheduleEntity.getGateEntity());
        planeScheduleEntity.setStatus(flightNumber);
        planeScheduleEntity.setUtcChange(now);
        planeScheduleEntity.setUtcFrom(now);
        planeScheduleEntity.setChangedBy(user);

        // set duration how long this gate will be free
        if (busyScheduleEntityList.isEmpty())
            planeScheduleEntity.setUtcTo(freeScheduleEntity.getUtcTo());
        else {
            ScheduleEntity nextBusySchedule = busyScheduleEntityList.get(0);
            planeScheduleEntity.setUtcTo(nextBusySchedule.getUtcFrom());
        }

        ScheduleEntity savedScheduleEntity = scheduleEntityRepository.save(planeScheduleEntity);

        scheduleSlot.setAvailable(true);
        Gate gate = new Gate();
        gate.setGateNumber(freeScheduleEntity.getGateEntity().getGateNumber());
        scheduleSlot.setGate(gate);
        Duration duration = Duration.between(now, planeScheduleEntity.getUtcTo());
        scheduleSlot.setDuration(duration);

        return scheduleSlot;
    }


    @Override
    public Schedule makeGateFree(Gate gate, LocalDateTime utc, String user) {


        Optional<GateEntity> gateEntityOptional = gateEntityRepository.findByGateNumber(gate.getGateNumber());
        if (gateEntityOptional.isEmpty()) {
            throw new NotFoundException(String.format(NotFoundException.GATE_NOT_FOUND_FORMAT, gate.getGateNumber()));
        }

        GateEntity gateEntity = gateEntityOptional.get();

        PageRequest limitOne = PageRequest.of(0,1);
        List<ScheduleEntity> scheduleEntityList = scheduleEntityRepository.findSchedulesForGateAtTime(gateEntity, utc, limitOne);

        if (scheduleEntityList.isEmpty())
            throw new DatabaseStateException(
                    String.format(DatabaseStateException.STATUS_DOESNT_EXIST_FORMAT, gate.getGateNumber(), utc));


        // get first / actually last (since it is limitOne)
        ScheduleEntity scheduleEntity = scheduleEntityList.get(0);

        String statusString = scheduleEntity.getStatus();
        Pattern pattern = Pattern.compile(Flight.FLIGHT_REGEX);
        Matcher matcher = pattern.matcher(statusString);
        if (!matcher.matches()) {
            throw new PlaneNotAssignedException(gate.getGateNumber());
        }


        ScheduleEntity freeScheduleEntity = new ScheduleEntity();
        freeScheduleEntity.setChangedBy(user);
        freeScheduleEntity.setUtcFrom(utc);
        freeScheduleEntity.setUtcTo(scheduleEntity.getUtcTo());
        freeScheduleEntity.setGateEntity(scheduleEntity.getGateEntity());
        freeScheduleEntity.setStatus(Status.free.name());
        freeScheduleEntity.setUtcChange(LocalDateTime.now(ZoneId.of("Z")));

        scheduleEntityRepository.save(freeScheduleEntity);

        Schedule freeSchedule = new Schedule();
        freeSchedule.setStatus(Status.free);
        freeSchedule.setFrom(freeScheduleEntity.getUtcFrom());
        freeSchedule.setTo(freeScheduleEntity.getUtcTo());
        freeSchedule.setGateNumber(freeScheduleEntity.getGateEntity().getGateNumber());

        return freeSchedule;



    }
}
