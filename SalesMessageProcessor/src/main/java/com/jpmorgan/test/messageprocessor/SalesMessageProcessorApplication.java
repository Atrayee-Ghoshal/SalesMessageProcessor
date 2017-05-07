package com.jpmorgan.test.messageprocessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.jpmorgan.test.messageprocessor.service.MessageProcessingService;

/**
 * Main entry point of the Sales Message processing application.
 * @author atrayee ghoshal
 *
 */
@SpringBootApplication
public class SalesMessageProcessorApplication implements CommandLineRunner {

	@Autowired
	MessageProcessingService messageProcessingService;
	
	public static void main(String[] args) {
		SpringApplication.run(SalesMessageProcessorApplication.class, args);
	}
	
	@Override
    public void run(String... strings) throws Exception {
		startMessageProcessingService();
    }
	
	public void startMessageProcessingService() {
		messageProcessingService.processSalesMessages();
	}
}
