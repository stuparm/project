package com.stuparm.project.controller;

import com.stuparm.project.model.Gate;
import com.stuparm.project.model.Schedule;
import com.stuparm.project.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@Validated
public class AdminController {

    @Autowired
    private ScheduleService scheduleService;


    @PostMapping("/admin/gates")
    @Operation(summary = "set gate status as admin", security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<Schedule> schedule(
            Authentication authentication,
            @Valid @RequestBody Schedule schedule
    ) {

        String user = authentication.getName();

        if (schedule.getFrom() == null) { schedule.setFrom(LocalDateTime.now(ZoneId.of("Z"))); }
        Schedule savedSchedule = scheduleService.create(schedule, user);

        return new ResponseEntity<>(savedSchedule, HttpStatus.OK);

    }


    @PostMapping("/admin/gates/free")
    @Operation(summary = "Make the gate free/unpark the plane", security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<Schedule> free (
            Authentication authentication,
            @Valid @RequestBody Gate gate) {

        String user = authentication.getName();


        LocalDateTime now = LocalDateTime.now(ZoneId.of("Z"));
        Schedule freeSchedule = scheduleService.makeGateFree(gate, now, user);

        return new ResponseEntity<>(freeSchedule, HttpStatus.OK);

    }


}
