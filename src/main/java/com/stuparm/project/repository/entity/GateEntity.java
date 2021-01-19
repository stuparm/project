package com.stuparm.project.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "gate")
@Getter
@Setter
public class GateEntity {

    @Id
    @Column(name = "gate_id")
    private Integer gateId;

    @Column(name = "gate_number")
    private String gateNumber;


}
