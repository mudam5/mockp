package com.loganalyser.service;

import com.loganalyser.model.LogData;
import com.loganalyser.repository.LogDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogDataService {

    private final LogDataRepository repository;

    public LogDataService(LogDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Count logs by type in a time range.
     */
    public long getLogCount(String logtype, LocalDateTime start, LocalDateTime end) {
        return repository.countByLogtypeAndTimestampBetween(logtype, start, end);
    }

    /**
     * Count logs grouped by log level/type.
     */
    public List<Object[]> getLogsGroupedByLevel() {
        return repository.countLogsByLevel();
    }

    /**
     * Get all logs.
     */
    public List<LogData> getAllLogs() {
        return repository.getAllLogs();
    }
}
