package com.stuparm.project.service.impl;


import com.stuparm.project.model.Gate;
import com.stuparm.project.repository.GateEntityRepository;
import com.stuparm.project.repository.entity.GateEntity;
import com.stuparm.project.service.GateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GateServiceImpl implements GateService {

    @Autowired
    private GateEntityRepository gateEntityRepository;

    @Override
    public List<Gate> findAll(Pageable pageable) {

        List<Gate> gateList = new ArrayList<>();

        Page<GateEntity> gateEntityPage = gateEntityRepository.findAll(pageable);
        if (gateEntityPage.hasContent()) {
            gateEntityPage.get().forEach(gateEntity -> {

                Gate gate = new Gate();
                gate.setGateNumber(gateEntity.getGateNumber());

                gateList.add(gate);
            });
        }

        return gateList;
    }






}
