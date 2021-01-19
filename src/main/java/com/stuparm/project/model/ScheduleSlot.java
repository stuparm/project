package com.stuparm.project.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class ScheduleSlot {

    private Boolean available;
    private Gate gate;

    private Duration duration;

}
