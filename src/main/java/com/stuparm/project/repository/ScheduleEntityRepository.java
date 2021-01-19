package com.stuparm.project.repository;

import com.stuparm.project.repository.entity.GateEntity;
import com.stuparm.project.repository.entity.ScheduleEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleEntityRepository extends PagingAndSortingRepository<ScheduleEntity, Integer> {

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ScheduleEntity save(ScheduleEntity scheduleEntity);

    @Query("SELECT se FROM ScheduleEntity se WHERE " +
            "se.gateEntity = :gate_entity AND " +
            "se.utcFrom < :utc AND " +
            "se.utcTo > :utc " +
            "ORDER BY se.utcChange DESC")
    public List<ScheduleEntity> findSchedulesForGateAtTime(@Param("gate_entity")GateEntity gateEntity,
                                                           @Param("utc")LocalDateTime utc,
                                                           Pageable pageable);

    @Transactional(propagation = Propagation.REQUIRED)
    @Query("SELECT se FROM ScheduleEntity se WHERE " +
            "se.gateEntity = :gate_entity AND " +
            "se.utcFrom > :utc AND " +
            "se.status = :status " +
            "ORDER BY se.utcChange DESC")
    public List<ScheduleEntity> findSchedulesForGateAfterTime(@Param("gate_entity")GateEntity gateEntity,
                                                              @Param("utc")LocalDateTime utc,
                                                              @Param("status")String status,
                                                              Pageable pageable);






}
