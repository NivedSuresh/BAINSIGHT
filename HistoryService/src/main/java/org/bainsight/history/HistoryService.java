package org.bainsight.history;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * Depends on ScyllaDB and Kafka.
 * */

@SpringBootApplication
public class HistoryService {

	public static void main(String[] args) {
		SpringApplication.run(HistoryService.class, args);
	}

}
