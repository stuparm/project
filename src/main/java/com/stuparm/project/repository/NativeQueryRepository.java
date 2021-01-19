package com.stuparm.project.repository;

import com.stuparm.project.repository.entity.ScheduleEntity;
import com.stuparm.project.repository.query.NativeQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@Slf4j
public class NativeQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private NativeQuery nativeQuery;

    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<ScheduleEntity> findFreeSchedule(LocalDateTime utc) {

        String sql = nativeQuery.findFreeScheduleSQL();

        List<ScheduleEntity> resultList = entityManager.createNativeQuery(sql, ScheduleEntity.class)
                .setParameter("utc", utc)
                .getResultList();

        if (resultList.isEmpty()) {
            log.debug("Empty gate/schedule is not found at time: {}", utc);
            return Optional.empty();
        }

        ScheduleEntity freeScheduleEntity = resultList.get(0);
        Optional<ScheduleEntity> optional = Optional.of(freeScheduleEntity);

        return optional;

    }



}
