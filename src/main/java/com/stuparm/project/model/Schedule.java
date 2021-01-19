package com.stuparm.project.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class Schedule {

    private LocalDateTime from;

    private LocalDateTime to;

    @NotNull(message = "Field status is mandatory")
    private Status status;

    @NotNull(message = "Field gateNumber is mandatory")
    private String gateNumber;

}
