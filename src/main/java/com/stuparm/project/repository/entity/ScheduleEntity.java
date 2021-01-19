package com.stuparm.project.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedule")
@Getter
@Setter
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;

    @Column(name = "utc_from")
    private LocalDateTime utcFrom;

    @Column(name = "utc_to")
    private LocalDateTime utcTo;

    @Column(name = "status")
    private String status;

    @Column(name = "utc_change")
    private LocalDateTime utcChange;

    @Column(name = "changed_by")
    private String changedBy;

    @ManyToOne
    @JoinColumn(name = "gate_id")
    private GateEntity gateEntity;

}




