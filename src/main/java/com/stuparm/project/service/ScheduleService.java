package com.stuparm.project.service;

import com.stuparm.project.model.*;

import java.time.LocalDateTime;

public interface ScheduleService {

    public Schedule create(Schedule schedule, String user);

    public Status checkStatus(Gate gate, LocalDateTime utc);

    public ScheduleSlot findSlotForGate(Flight flight, String user);

    public Schedule makeGateFree(Gate gate, LocalDateTime utc, String user);


}
