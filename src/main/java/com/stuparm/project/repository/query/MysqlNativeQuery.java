package com.stuparm.project.repository.query;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "database.type", havingValue = "mysql")
public class MysqlNativeQuery implements NativeQuery{


    @Override
    public String findFreeScheduleSQL() {
        return "SELECT se.* FROM project.schedule se INNER JOIN " +
               "    ( SELECT gate_id, max(utc_change) as latest_change " +
               "      FROM project.schedule " +
               "      WHERE utc_from < :utc AND utc_to > :utc  GROUP BY gate_id ) jse " +
               "ON se.gate_id = jse.gate_id AND se.utc_change = jse.latest_change " +
               "WHERE status = 'free' ORDER BY TIMEDIFF(se.utc_to, :utc) DESC LIMIT 1; ";
    }

}
