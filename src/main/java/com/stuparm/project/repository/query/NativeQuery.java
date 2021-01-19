package com.stuparm.project.repository.query;

/**
 * This interface is used to abstract native queries in cases where JPQL cannot be used.
 * Each implementation of this interface will return queries for specific database, and this implementation
 * will be injected with @ConditionalOn annotation
 *
 * Parameters are added in the format :param1 or :param2 (with column in from of param name)
 */
public interface NativeQuery {

    /**
     * SQL that finds all gates/schedules that are available at specific time.
     * SQL should return sorted records where the first record has the longest availability<br>
     * Parameter: utc <br>
     * @return SQL that contain parameters
     *
     */
    public String findFreeScheduleSQL();






}
