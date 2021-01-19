package com.stuparm.project.exception;

public class PlaneNotAssignedException extends RuntimeException{

    public PlaneNotAssignedException(String gateNumber) {
        super(String.format("Gate %s doesnt have plane assigned", gateNumber));
    }


}
