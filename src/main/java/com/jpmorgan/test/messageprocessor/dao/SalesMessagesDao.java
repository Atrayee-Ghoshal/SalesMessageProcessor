package com.jpmorgan.test.messageprocessor.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import com.jpmorgan.test.messageprocessor.entity.ProductType;
import com.jpmorgan.test.messageprocessor.entity.Sale;
import com.jpmorgan.test.messageprocessor.model.MessageOperation;
import com.jpmorgan.test.messageprocessor.model.MessageType;
import com.jpmorgan.test.messageprocessor.model.MessageType1;
import com.jpmorgan.test.messageprocessor.model.MessageType2;
import com.jpmorgan.test.messageprocessor.model.MessageType3;

/**
 * Data Layer class which will create the sales messages.
 * Currently this class picks the data from a csv file placed in the classpath
 * and creates the final list of Messages after necessary validations.
 * @author atrayee ghoshal
 *
 */
@Repository
public class SalesMessagesDao {
	
	@Autowired
	SalesDao salesDao;
	
	@Autowired
	ProductTypeDao productTypeDao;
	
	public MessageType getMessageType1(Sale sale) {
		return new MessageType1(sale);
	}
	
	public MessageType getMessageType2(Sale sale, Integer numOfOccurences) {
		return new MessageType2(sale, numOfOccurences);
	}
	
	public MessageType getMessageType3(Sale sale, MessageOperation operation) {
		return new MessageType3(sale, operation);
	}

	public List<MessageType> getSalesMessages() {
		return createMessageList();
	}

	private List<MessageType> createMessageList() {
		List<MessageType> messageList = new ArrayList<MessageType>();
        String line = "";
        String cvsSeparator = ",";
        int lineCount = 1;
        Map<String, ProductType> productMap = new HashMap<String, ProductType>();

        try {
        	File csvFile = ResourceUtils.getFile("classpath:testdata.csv");
        	BufferedReader br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSeparator);
                if(data != null && data.length >= 3) {
                    String messageTypeCategory = data[0];
                    String productType = data[1];
                    String value = data[2];
                    
                    if (!checkNullOrBlank(messageTypeCategory)) {
                    	if (!checkNullOrBlank(productType)) {
                    		if (!checkNullOrBlank(value)) {
                    			try {
                    				
                    				ProductType product = createProduct(productMap, productType);
                    				Sale sale = createSale(value, product);
                            		
                        			if(messageTypeCategory.equalsIgnoreCase("Type 1")) {
                                		addMessageType1ToMessageList(messageList, sale);
                                	} else if(messageTypeCategory.equalsIgnoreCase("Type 2")) {
                                		addMessageType2ToMessageList(messageList, lineCount, data, sale);
                                	} else if(messageTypeCategory.equalsIgnoreCase("Type 3")) {
                                		addMessageType3ToMessageList(messageList, lineCount, data, sale);
                                	} else {
                                		System.out.println("Invalid Message Type! Ignoring the record at row " + lineCount);
                                	}
                        			
                    			} catch (NumberFormatException e) {
                    	        	System.out.println("Either Sales value or Number of occurence is not a number! Ignoring the record at row " + lineCount);
                    	        } catch (ArrayIndexOutOfBoundsException e) {
                    	        	System.out.println("Message Type does not have required parameters! Ignoring the record at row " + lineCount);
                    	        }
                    		} else {
                    			System.out.println("Blank value for Sales value is not allowed! Ignoring the record at row " + lineCount);
                    		}
                    	} else {
                    		System.out.println("Blank value for Product Type is not allowed! Ignoring the record at row " + lineCount);
                    	}
                    } else {
                    	System.out.println("Blank value for Message Type is not allowed! Ignoring the record at row " + lineCount);
                    }
                    
                } else {
                	System.out.println("Insufficient parameters! Ignoring the record at row " + lineCount);
                }

                lineCount++;
            }

            br.close();
        }  catch (IOException e) {
            System.out.println(e.getMessage());
        }
		
        return messageList;
	}

	private void addMessageType3ToMessageList(List<MessageType> messageList, int lineCount, String[] data, Sale sale) {
		String operation = data[3];
		MessageType messageType = null;
		
		if (!checkNullOrBlank(operation)) {
			if (operation.equalsIgnoreCase("ADD")) {
				messageType = getMessageType3(sale, MessageOperation.ADD);
			} else if (operation.equalsIgnoreCase("SUBTRACT")) {
				messageType = getMessageType3(sale, MessageOperation.SUBTRACT);
			} else if (operation.equalsIgnoreCase("MULTIPLY")) {
				messageType = getMessageType3(sale, MessageOperation.MULTIPLY);
			}
		} else {
			System.out.println("Operation cannot be blank for Type 3 message! Ignoring the record at row " + lineCount);
		}
		
		if(messageType != null) {
			messageList.add(messageType);
		} else {
			System.out.println("Invalid Message Operation! Ignoring the record at row " + lineCount);
		}
	}

	private void addMessageType2ToMessageList(List<MessageType> messageList, int lineCount, String[] data, Sale sale) {
		String numberOfOccurence = data[3];
		if (!checkNullOrBlank(numberOfOccurence)) {
			MessageType messageType = getMessageType2(sale, Integer.parseInt(numberOfOccurence));
			messageList.add(messageType);
		} else {
			System.out.println("Number of occurence cannot be blank for Type 2 message! Ignoring the record at row " + lineCount);
		}
	}

	private void addMessageType1ToMessageList(List<MessageType> messageList, Sale sale) {
		MessageType messageType = getMessageType1(sale);
		messageList.add(messageType);
	}

	private Sale createSale(String value, ProductType product) {
		Double saleValue= Double.parseDouble(value);
		Sale sale = salesDao.getSales(product, saleValue);
		return sale;
	}

	private ProductType createProduct(Map<String, ProductType> productMap, String productType) {
		ProductType product = null;
		if(productMap.containsKey(productType.toLowerCase())) {
			product = productMap.get(productType.toLowerCase());
		} else {
			product = productTypeDao.getProduct(productType);
			productMap.put(productType.toLowerCase(), product);
		}
		return product;
	}
	
	private boolean checkNullOrBlank(String value) {
		return (value != null && !value.equals("")) ? false : true;
	}
}
