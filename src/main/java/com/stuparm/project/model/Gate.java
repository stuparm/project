package com.stuparm.project.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Gate {

    @NotNull(message = "Field gate number is mandatory")
    private String gateNumber;


}
