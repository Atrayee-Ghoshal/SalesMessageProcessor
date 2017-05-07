package com.jpmorgan.test.messageprocessor.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jpmorgan.test.messageprocessor.dao.SalesMessagesDao;
import com.jpmorgan.test.messageprocessor.entity.ProductType;
import com.jpmorgan.test.messageprocessor.entity.Sale;
import com.jpmorgan.test.messageprocessor.entity.SalesRecord;
import com.jpmorgan.test.messageprocessor.model.MessageType;

/**
 * Service class which processes the incoming messages 
 * and generates the Sales Report.
 * @author atrayee ghoshal
 *
 */
@Service
public class MessageProcessingService {
	private static final Logger LOGGER = Logger.getLogger( MessageProcessingService.class.getName() );
	
	@Autowired
	SalesMessagesDao salesMessageDao;
	
	@Value("${messages.processed.max-count}")
	private Integer maxMessagesToBeProcessed;
	
	@Value("${messages.processed.iteration-count}")
	private Integer iterationCount;
	
	/**
	 * Method to process the sales messages.
	 */
    public void processSalesMessages() {
    	LOGGER.log( Level.INFO, "==> MessageProcessingService Started <==");
		
		Map<Integer, SalesRecord> salesRecordMap = new HashMap<Integer, SalesRecord>();
		
		// Fetching messages from DAO layer
		List<MessageType> messageList = salesMessageDao.getSalesMessages();
		int totalMessages = messageList.size();
		
		if (totalMessages == 0) {
			createAndDisplayReportForEmptyMessageList();
		} else {
			createAndDisplaySalesReport(salesRecordMap, messageList, totalMessages);
		}
		
		LOGGER.log( Level.INFO, "==> MessageProcessingService Completed <==");
    }

	private void createAndDisplayReportForEmptyMessageList() {
		createReportHeader();
		System.out.println("No messages in the queue! Stopping message processing.");
		createReportFooter();
	}

	private void createAndDisplaySalesReport(Map<Integer, SalesRecord> salesRecordMap,
								List<MessageType> messageList, int totalMessages) {
		
		createReportHeader();
		int iteration = 1;
		int messageListSize = messageList.size();
		Integer totalMessagesToBeProcessed = messageListSize < maxMessagesToBeProcessed 
												? messageListSize : maxMessagesToBeProcessed;
		
		// Only #{totalMessagesToBeProcessed} messages to be processed before the application is stopped.
		for(int messageCount = 1; messageCount <= totalMessagesToBeProcessed ; messageCount++) {
			MessageType message = messageList.get(messageCount-1);
			message.processMessage(salesRecordMap);
			
			// Enter logs in the report after every 10th message 
			// or if this is the last message of the last iteration
			if (messageCount % iterationCount == 0 || messageCount == messageListSize) {
				iteration = generateIterativeReport(iteration, salesRecordMap);
			} 
		}

		System.out.println("****************************************");
		System.out.println("Pausing message processing as the message count has reached 50 or all the messages have been processed.");
		
		createAdjustmentsReport(salesRecordMap);
		createReportFooter();
	}

	private int generateIterativeReport(int iteration, Map<Integer, SalesRecord> salesRecordMap) {
		System.out.println("*** Logging sales in Report - Iteration "+ iteration++ +" ***");
		for(SalesRecord salesRecord : salesRecordMap.values()) {
			Sale sale = salesRecord.getSale();
			ProductType productType = sale.getProductType();
			
			System.out.println("Product Type - " + productType.getProductType() + " recorded " + salesRecord.getNumOfSales() 
						+ " sale(s) with a total value of " + new BigDecimal(salesRecord.getTotalSalesValue()).toPlainString());
		}
		return iteration;
	}

	private void createReportFooter() {
		System.out.println("****************************************");
		System.out.println("***##*** End Of Sales Report ***##***");
	}

	private void createReportHeader() {
		System.out.println("***##*** Start Of Sales Report ***##***");
		System.out.println("****************************************");
	}
	
	private void createAdjustmentsReport(Map<Integer, SalesRecord> salesRecordMap) {
		System.out.println("*** Adjustments Report ***");
		
		for(SalesRecord salesRecord : salesRecordMap.values()) {
			System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			System.out.println("Product Type - " + salesRecord.getSale().getProductType().getProductType());
			if (salesRecord.getAdjustments() != null && salesRecord.getAdjustments().size() > 0) {
				System.out.println("Adjustments made :");
				for(String adjusment : salesRecord.getAdjustments()){
					System.out.println(adjusment);
				}
			} else {
				System.out.println("No adjustments made for this product!");
			}
			
			System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		}
	}
}
