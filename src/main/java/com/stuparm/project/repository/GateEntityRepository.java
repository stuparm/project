package com.stuparm.project.repository;

import com.stuparm.project.repository.entity.GateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GateEntityRepository extends PagingAndSortingRepository<GateEntity, Integer> {



    @Override
    public Page<GateEntity> findAll(Pageable pageable);

    public Optional<GateEntity> findByGateNumber(String gateNumber);




}
