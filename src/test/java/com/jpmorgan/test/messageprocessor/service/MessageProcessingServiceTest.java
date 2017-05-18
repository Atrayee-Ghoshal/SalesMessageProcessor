package com.jpmorgan.test.messageprocessor.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.jpmorgan.test.messageprocessor.dao.SalesMessagesDao;
import com.jpmorgan.test.messageprocessor.entity.ProductType;
import com.jpmorgan.test.messageprocessor.entity.Sale;
import com.jpmorgan.test.messageprocessor.entity.SalesRecord;
import com.jpmorgan.test.messageprocessor.model.MessageOperation;
import com.jpmorgan.test.messageprocessor.model.MessageType;
import com.jpmorgan.test.messageprocessor.model.MessageType1;
import com.jpmorgan.test.messageprocessor.model.MessageType2;
import com.jpmorgan.test.messageprocessor.model.MessageType3;


@RunWith(MockitoJUnitRunner.class)
public class MessageProcessingServiceTest {
	MessageProcessingService messageProcessingService;
	private static final Integer MAX_MESSAGES = 10;
	
	@Mock
	SalesMessagesDao salesMessageDao;

	@Before
	public void setup(){
		messageProcessingService = new MessageProcessingService(salesMessageDao);
		messageProcessingService.setIterationCount(1);
		messageProcessingService.setMaxMessagesToBeProcessed(MAX_MESSAGES);
	}

	@Test
	public void testProcessSalesMessageWithType1() {
		ProductType productType1 = new ProductType(1, "Apple");
		ProductType productType2 = new ProductType(2, "Orange");
		List<MessageType> messageList = new ArrayList<MessageType>();
		
		// Message Type 1 for Apple with Sale value 10
		Sale sale = getSales(productType1, 10.0);
		MessageType m = new MessageType1(sale);
		messageList.add(m);

		// Message Type 1 for Orange with Sale value 20
		sale = getSales(productType2, 20.0);
		m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 1 for Orange with Sale value 10
		sale = getSales(productType2, 10.0);
		m = new MessageType1(sale);
		messageList.add(m);

		Mockito.when(salesMessageDao.getSalesMessages())
			.thenReturn(messageList);
		
		Map<Integer, SalesRecord> salesRecord = messageProcessingService.processSalesMessages();
		assertEquals("Only two products added", salesRecord.size(), 2);
		SalesRecord appleRecord = salesRecord.get(1);
		SalesRecord orangeRecord = salesRecord.get(2);
		assertEquals("Number of sales for apple is 1", appleRecord.getNumOfSales(), Integer.valueOf(1));
		assertEquals("Number of sales for orange is 2", orangeRecord.getNumOfSales(), Integer.valueOf(2));
		assertEquals("Total sales for orange is 30.0", orangeRecord.getTotalSalesValue(), Double.valueOf(30.0));
	}
	
	@Test
	public void testProcessSalesMessageWithType1AndType2() {
		ProductType productType1 = new ProductType(1, "Apple");
		ProductType productType2 = new ProductType(2, "Orange");
		List<MessageType> messageList = new ArrayList<MessageType>();
		
		// Message Type 1 for Apple with Sale value 10
		Sale sale = getSales(productType1, 10.0);
		MessageType m = new MessageType1(sale);
		messageList.add(m);

		// Message Type 1 for Orange with Sale value 20
		sale = getSales(productType2, 20.0);
		m = new MessageType1(sale);
		messageList.add(m);

		// Message Type 2 for Orange with Sale value 10 and 10 occurences
		sale = getSales(productType2, 10.0);
		m = new MessageType2(sale, 10);
		messageList.add(m);
		
		Mockito.when(salesMessageDao.getSalesMessages())
			.thenReturn(messageList);
		
		Map<Integer, SalesRecord> salesRecord = messageProcessingService.processSalesMessages();
		assertEquals("Only two products added", salesRecord.size(), 2);
		SalesRecord appleRecord = salesRecord.get(1);
		SalesRecord orangeRecord = salesRecord.get(2);
		assertEquals("Number of sales for apple is 1", appleRecord.getNumOfSales(), Integer.valueOf(1));
		assertEquals("Number of sales for orange is 11", orangeRecord.getNumOfSales(), Integer.valueOf(11));
		assertEquals("Total sales for orange is 120.0", orangeRecord.getTotalSalesValue(), Double.valueOf(120.0));
	}
	
	@Test
	public void testProcessSalesMessageType3AddAdjustment() {
		ProductType productType1 = new ProductType(1, "Apple");
		ProductType productType2 = new ProductType(2, "Orange");
		ProductType productType3 = new ProductType(3, "Banana");
		List<MessageType> messageList = new ArrayList<MessageType>();
		
		// Message Type 1 for Apple with Sale value 10
		Sale sale = getSales(productType1, 10.0);
		MessageType m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 1 for Orange with Sale value 20
		sale = getSales(productType2, 20.0);
		m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 2 for Orange with Sale value 10 and 10 occurences
		sale = getSales(productType2, 10.0);
		m = new MessageType2(sale, 10);
		messageList.add(m);
		
		// Message Type 1 for Banana with Sale value 10
		sale = getSales(productType3, 10.0);
		m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 3 for Banana with adjustment ADD and value 5
		sale = getSales(productType3, 5.0);
		m = new MessageType3(sale, MessageOperation.ADD);
		messageList.add(m);
		
		Mockito.when(salesMessageDao.getSalesMessages())
			.thenReturn(messageList);
		
		Map<Integer, SalesRecord> salesRecord = messageProcessingService.processSalesMessages();
		assertEquals("Only three products added", salesRecord.size(), 3);
		SalesRecord appleRecord = salesRecord.get(1);
		SalesRecord orangeRecord = salesRecord.get(2);
		SalesRecord bananaRecord = salesRecord.get(3);
		List<String> bananaAdjustments = bananaRecord.getAdjustments();
		String adjustmentMessage = "Adjustment Operation ADD applied for a value of 5.0";
		assertEquals("Number of sales for apple is 1", appleRecord.getNumOfSales(), Integer.valueOf(1));
		assertEquals("Number of sales for orange is 11", orangeRecord.getNumOfSales(), Integer.valueOf(11));
		assertEquals("Number of sales for banana is 1", bananaRecord.getNumOfSales(), Integer.valueOf(1));
		assertEquals("Total sales for orange is 120.0", orangeRecord.getTotalSalesValue(), Double.valueOf(120.0));
		assertEquals("Total sales for banana is 15.0", bananaRecord.getTotalSalesValue(), Double.valueOf(15.0));
		assertEquals("Adjustment for banana", bananaAdjustments.get(0), adjustmentMessage);
		assertNull("No adjustments for orange", orangeRecord.getAdjustments());
		assertNull("No adjustments for apple", appleRecord.getAdjustments());
	}
	
	@Test
	public void testProcessSalesMessageType3AddAndSubtractAdjustment() {
		ProductType productType1 = new ProductType(1, "Apple");
		ProductType productType2 = new ProductType(2, "Orange");
		ProductType productType3 = new ProductType(3, "Banana");
		List<MessageType> messageList = new ArrayList<MessageType>();
		
		// Message Type 1 for Apple with Sale value 10
		Sale sale = getSales(productType1, 10.0);
		MessageType m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 1 for Orange with Sale value 20
		sale = getSales(productType2, 20.0);
		m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 2 for Orange with Sale value 10 and 10 occurences
		sale = getSales(productType2, 10.0);
		m = new MessageType2(sale, 10);
		messageList.add(m);
		
		// Message Type 2 for Apple with Sale value 5 and 10 occurences
		sale = getSales(productType1, 5.0);
		m = new MessageType2(sale, 10);
		messageList.add(m);
		
		// Message Type 1 for Banana with Sale value 10
		sale = getSales(productType3, 10.0);
		m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 3 for Banana with adjustment ADD and value 5
		sale = getSales(productType3, 5.0);
		m = new MessageType3(sale, MessageOperation.ADD);
		messageList.add(m);
		
		// Message Type 3 for Apple with adjustment SUBTRACT and value 5
		sale = getSales(productType1, 5.0);
		m = new MessageType3(sale, MessageOperation.SUBTRACT);
		messageList.add(m);
		
		Mockito.when(salesMessageDao.getSalesMessages())
			.thenReturn(messageList);
		
		Map<Integer, SalesRecord> salesRecord = messageProcessingService.processSalesMessages();
		assertEquals("Only three products added", salesRecord.size(), 3);
		SalesRecord appleRecord = salesRecord.get(1);
		SalesRecord orangeRecord = salesRecord.get(2);
		SalesRecord bananaRecord = salesRecord.get(3);
		List<String> bananaAdjustments = bananaRecord.getAdjustments();
		String adjustmentMessage = "Adjustment Operation ADD applied for a value of 5.0";
		assertEquals("Number of sales for apple is 11", appleRecord.getNumOfSales(), Integer.valueOf(11));
		assertEquals("Number of sales for orange is 11", orangeRecord.getNumOfSales(), Integer.valueOf(11));
		assertEquals("Number of sales for banana is 1", bananaRecord.getNumOfSales(), Integer.valueOf(1));
		assertEquals("Total sales for apple is 5.0", appleRecord.getTotalSalesValue(), Double.valueOf(5.0));
		assertEquals("Total sales for orange is 120.0", orangeRecord.getTotalSalesValue(), Double.valueOf(120.0));
		assertEquals("Total sales for banana is 15.0", bananaRecord.getTotalSalesValue(), Double.valueOf(15.0));
		assertEquals("Adjustment for banana", bananaAdjustments.get(0), adjustmentMessage);
		assertNull("No adjustments for orange", orangeRecord.getAdjustments());
		assertNotNull("Adjustments for apple", appleRecord.getAdjustments());
	}
	
	@Test
	public void testProcessSalesMessageType3AddAndMultiplyAdjustment() {
		ProductType productType1 = new ProductType(1, "Apple");
		ProductType productType2 = new ProductType(2, "Orange");
		ProductType productType3 = new ProductType(3, "Banana");
		List<MessageType> messageList = new ArrayList<MessageType>();
		
		// Message Type 1 for Apple with Sale value 10
		Sale sale = getSales(productType1, 10.0);
		MessageType m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 1 for Orange with Sale value 20
		sale = getSales(productType2, 20.0);
		m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 2 for Orange with Sale value 10 and 10 occurences
		sale = getSales(productType2, 10.0);
		m = new MessageType2(sale, 10);
		messageList.add(m);
		
		// Message Type 1 for Banana with Sale value 10
		sale = getSales(productType3, 10.0);
		m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 3 for Banana with adjustment ADD and value 5
		sale = getSales(productType3, 5.0);
		m = new MessageType3(sale, MessageOperation.ADD);
		messageList.add(m);
		
		// Message Type 3 for Orange with adjustment MULTIPLY and value 10
		sale = getSales(productType2, 10.0);
		m = new MessageType3(sale, MessageOperation.MULTIPLY);
		messageList.add(m);
		
		// Message Type 3 for Banana with adjustment MULTIPLY and value 5
		sale = getSales(productType3, 5.0);
		m = new MessageType3(sale, MessageOperation.MULTIPLY);
		messageList.add(m);
		
		Mockito.when(salesMessageDao.getSalesMessages())
			.thenReturn(messageList);
		
		Map<Integer, SalesRecord> salesRecord = messageProcessingService.processSalesMessages();
		assertEquals("Only three products added", salesRecord.size(), 3);
		SalesRecord appleRecord = salesRecord.get(1);
		SalesRecord orangeRecord = salesRecord.get(2);
		SalesRecord bananaRecord = salesRecord.get(3);
		List<String> bananaAdjustments = bananaRecord.getAdjustments();
		String adjustmentMessage = "Adjustment Operation ADD applied for a value of 5.0";
		assertEquals("Number of sales for apple is 1", appleRecord.getNumOfSales(), Integer.valueOf(1));
		assertEquals("Number of sales for orange is 11", orangeRecord.getNumOfSales(), Integer.valueOf(11));
		assertEquals("Number of sales for banana is 1", bananaRecord.getNumOfSales(), Integer.valueOf(1));
		assertEquals("Total sales for orange is 1200.0", orangeRecord.getTotalSalesValue(), Double.valueOf(1200.0));
		assertEquals("Total sales for banana is 75.0", bananaRecord.getTotalSalesValue(), Double.valueOf(75.0));
		assertEquals("Adjustment for banana", bananaAdjustments.get(0), adjustmentMessage);
		assertNotNull("Adjustments made for orange", orangeRecord.getAdjustments());
		assertNull("No adjustments for apple", appleRecord.getAdjustments());
	}
	
	@Test
	public void testProcessSalesMessageForMaxMessageProcessed() {
		ProductType productType1 = new ProductType(1, "Apple");
		ProductType productType2 = new ProductType(2, "Orange");
		ProductType productType3 = new ProductType(3, "Banana");
		ProductType productType4 = new ProductType(4, "Melon");
		List<MessageType> messageList = new ArrayList<MessageType>();
		
		// Message Type 1 for Apple with Sale value 10
		Sale sale = getSales(productType1, 10.0);
		MessageType m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 1 for Orange with Sale value 20
		sale = getSales(productType2, 20.0);
		m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 2 for Orange with Sale value 10 and 10 occurences
		sale = getSales(productType2, 10.0);
		m = new MessageType2(sale, 10);
		messageList.add(m);
		
		// Message Type 1 for Banana with Sale value 10
		sale = getSales(productType3, 10.0);
		m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 3 for Banana with adjustment ADD and value 5
		sale = getSales(productType3, 5.0);
		m = new MessageType3(sale, MessageOperation.ADD);
		messageList.add(m);
		
		// Message Type 3 for Orange with adjustment MULTIPLY and value 10
		sale = getSales(productType2, 10.0);
		m = new MessageType3(sale, MessageOperation.MULTIPLY);
		messageList.add(m);
		
		// Message Type 3 for Banana with adjustment MULTIPLY and value 5
		sale = getSales(productType3, 5.0);
		m = new MessageType3(sale, MessageOperation.MULTIPLY);
		messageList.add(m);
		
		// Message Type 2 for Orange with Sale value 10 and 10 occurences
		sale = getSales(productType2, 10.0);
		m = new MessageType2(sale, 10);
		messageList.add(m);

		// Message Type 1 for Banana with Sale value 10
		sale = getSales(productType3, 10.0);
		m = new MessageType1(sale);
		messageList.add(m);

		// Message Type 2 for Orange with Sale value 10 and 10 occurences
		sale = getSales(productType2, 10.0);
		m = new MessageType2(sale, 10);
		messageList.add(m);

		// Message Type 1 for Melon with Sale value 10
		// 11th message should not be processed
		sale = getSales(productType4, 10.0);
		m = new MessageType1(sale);
		messageList.add(m);
		
		// Message Type 1 for Apple with Sale value 10
		// 12th message should not be processed
		sale = getSales(productType4, 10.0);
		m = new MessageType1(sale);
		messageList.add(m);
		
		Mockito.when(salesMessageDao.getSalesMessages())
			.thenReturn(messageList);
		
		Map<Integer, SalesRecord> salesRecord = messageProcessingService.processSalesMessages();
		assertEquals("Only three products added", salesRecord.size(), 3);
		SalesRecord appleRecord = salesRecord.get(1);
		//Since, 11th record is not processed, Sales for Melon should not be recorded.
		assertEquals("11th record not processed", salesRecord.size(), 3);
		//Since, 12th record is not processed, Sales for Apple will remain 1 for a value of 10.0.
		assertEquals("12th record not processed", appleRecord.getNumOfSales(), Integer.valueOf(1));
		assertEquals("12th record not processed", appleRecord.getTotalSalesValue(), Double.valueOf(10.0));
	}
	
	public Sale getSales(ProductType productType, Double value) {
		return new Sale(value, productType);
	}
	
}
