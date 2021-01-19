package com.stuparm.project.controller;


import com.stuparm.project.model.Flight;
import com.stuparm.project.model.Gate;
import com.stuparm.project.model.ScheduleSlot;
import com.stuparm.project.model.Status;
import com.stuparm.project.openapi.OpenApiPageable;
import com.stuparm.project.service.GateService;
import com.stuparm.project.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author stuparm
 */
@RestController
public class GatesController {

    @Autowired
    private GateService gateService;

    @Autowired
    private ScheduleService scheduleService;


    @OpenApiPageable
    @GetMapping("/gates")
    @Operation(summary = "List all gates", security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<List<Gate>> get(
            @Parameter(hidden = true, in = ParameterIn.QUERY) Pageable pageable
    ) {

        List<Gate> gateList = gateService.findAll(pageable);
        return new ResponseEntity<>(gateList, HttpStatus.OK);

    }

    @Operation(summary = "Check the availability for the gate", security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/gates/{gateNumber}/status")
    public ResponseEntity status(@PathVariable(name = "gateNumber") String gateNumber) {

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Z"));
        Gate gate = new Gate();
        gate.setGateNumber(gateNumber);

        Status status = scheduleService.checkStatus(gate, now);
        // make the object as response / UI would be able to say body.status
        Map<String,Status> map = new HashMap<>();
        map.put("status", status);

        return new ResponseEntity<>(map, HttpStatus.OK);

    }


    @Operation(summary = "Assign free gate to specified flight", security = @SecurityRequirement(name = "basicAuth"))
    @PostMapping("/gates/assign")
    public ResponseEntity<ScheduleSlot> assign(@Valid @RequestBody Flight flight, Authentication authentication) {

        String user = authentication.getName();
        ScheduleSlot scheduleSlot = scheduleService.findSlotForGate(flight, user);

        return new ResponseEntity<>(scheduleSlot, HttpStatus.OK);

    }







}
