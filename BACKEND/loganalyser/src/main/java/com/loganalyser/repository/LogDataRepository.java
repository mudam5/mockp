package com.loganalyser.repository;

import com.loganalyser.model.LogData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogDataRepository extends JpaRepository<LogData, Long> {

    // Derived query method (Spring Data JPA will generate the query)
    long countByLogtypeAndTimestampBetween(String logtype, LocalDateTime start, LocalDateTime end);

    // Example of custom query with parameters
    /*
    @Query("SELECT COUNT(l) FROM LogData l WHERE l.timestamp BETWEEN :start AND :end AND l.logtype = :logtype")
    Long countByLogtypeAndTimeBetween(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("logtype") String logtype);
    */

    @Query("SELECT l.logtype, COUNT(l) FROM LogData l GROUP BY l.logtype")
    List<Object[]> countLogsByLevel();

    @Query("SELECT l FROM LogData l")
    List<LogData> getAllLogs();
}
