package com.stuparm.project.service;

import com.stuparm.project.model.Gate;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GateService {

    public List<Gate> findAll(Pageable pageable);



}
