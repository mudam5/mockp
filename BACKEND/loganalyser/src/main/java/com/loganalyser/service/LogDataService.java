package com.loganalyser.service;

import org.springframework.stereotype.Service;

import com.loganalyser.model.LogData;
import com.loganalyser.repository.LogDataRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.sql.Timestamp;

@Service
public class LogDataService {

	private final LogDataRepository repository;

	public LogDataService(LogDataRepository repository) {
		this.repository = repository;
	}

	public Long getLogCountBetween(LocalDateTime start, LocalDateTime end, String logtype) {
		if (start == null || end == null || logtype == null || logtype.isBlank()) {
			throw new IllegalArgumentException("Start, end, and logtype must not be null/empty");
		}

		// Pass LocalDateTime directly
		// return repository.countByLogtypeAndTimeBetween(start, end, logtype);
		repository.countByLogtypeAndTimestampBetween(logtype, start, end);
	}
	
	
	public List<LogData> getAllLogs() {
        return repository.findAll();
    }

	public Map<String, Long> getLogCountsByLevel() {
        List<Object[]> results = repository.countLogsByLevel();
        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : results) {
            counts.put((String) row[0], (Long) row[1]);
        }
        return counts;
    }
	
	public List<Map<String, Object>> getLogCountsPer5Minutes() {
	    List<LogData> logs = repository.getAllLogs();

	    Map<LocalDateTime, Map<String, Long>> grouped = new TreeMap<LocalDateTime, Map<String,Long>>();

	    for (LogData log : logs) {
	        LocalDateTime ts = log.getTimestamp();

	        int minute = (ts.getMinute() / 5) * 5;
	        LocalDateTime bucket = ts.withMinute(minute).withSecond(0).withNano(0);

	        grouped.putIfAbsent(bucket, new HashMap<>());
	        Map<String, Long> levelCounts = grouped.get(bucket);

	        levelCounts.put(log.getLogtype(),
	            levelCounts.getOrDefault(log.getLogtype(), 0L) + 1);
	    }

	    List<Map<String, Object>> output = new ArrayList<>();
	    for (var entry : grouped.entrySet()) {
	        Map<String, Object> map = new LinkedHashMap<>();
	        map.put("timestamp", entry.getKey().toString());
	        map.put("ERROR", entry.getValue().getOrDefault("ERROR", 0L));
	        map.put("WARN", entry.getValue().getOrDefault("WARN", 0L));
	        map.put("INFO", entry.getValue().getOrDefault("INFO", 0L));
	        map.put("DEBUG", entry.getValue().getOrDefault("DEBUG", 0L));
	        output.add(map);
	    }
	    return output;
	}


}
