package com.stuparm.project.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 *
 * @author stuparm
 */
@Getter
@Setter
public class Flight {

    public static final String FLIGHT_REGEX = "^([A-Z]{2}|[A-Z]\\d|\\d[A-Z])[1-9](\\d{1,3})?$";


    @NotBlank(message = "Field flightNumber is mandatory")
    @Pattern(regexp = FLIGHT_REGEX, message = "Flight number is not in proper format")
    private String flightNumber;




}
