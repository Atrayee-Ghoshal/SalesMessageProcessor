package com.jpmorgan.test.messageprocessor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jpmorgan.test.messageprocessor.entity.ProductType;
import com.jpmorgan.test.messageprocessor.entity.Sale;
import com.jpmorgan.test.messageprocessor.entity.SalesRecord;

/**
 * Class for Message Type 3 object.
 * @author atrayee ghoshal
 *
 */
public class MessageType3 extends MessageType {
	
	private MessageOperation messageOperation;

	public MessageType3(Sale sale, MessageOperation messageOperation) {
		super(sale);
		this.messageOperation = messageOperation;
	}

	
	@Override
	public void processMessage(Map<Integer, SalesRecord> salesRecordMap) {
		Sale sale = getSale();
		ProductType product= sale.getProductType();
		Integer productTypeId = product.getProductTypeId();

		// Adjustments cannot be made if this is the first Message for the given Product Type
		// because there is no such product in the sales record
		// Hence there is no else condition
		
		if (salesRecordMap.containsKey(productTypeId)) {
			SalesRecord salesRecord = salesRecordMap.get(productTypeId);
			salesRecord.setTotalSalesValue(executeMessageOperation(salesRecord, sale));
			addAjustmentRecordToSale(salesRecord, sale.getSaleValue());
		} 
		
	}
	
	private Double executeMessageOperation(SalesRecord salesRecord, Sale sale){
		Double totalSalesValue = salesRecord.getTotalSalesValue();
		switch(messageOperation) {
			case ADD:
				totalSalesValue = Double.sum(totalSalesValue, (sale.getSaleValue() * salesRecord.getNumOfSales()));
				break;
			case SUBTRACT:
				totalSalesValue = Double.sum(totalSalesValue, -(sale.getSaleValue() * salesRecord.getNumOfSales()));
				break;
			case MULTIPLY:
				totalSalesValue = totalSalesValue * sale.getSaleValue();
				break;
		}
		
		return totalSalesValue;
	}

	private void addAjustmentRecordToSale(SalesRecord salesRecord, Double saleValue) {
		List<String> adjustmentsList = salesRecord.getAdjustments() !=null ? salesRecord.getAdjustments() : new ArrayList<String>();
		adjustmentsList.add("Adjustment Operation "+ messageOperation.name() + " applied for a value of " + saleValue);
		salesRecord.setAdjustments(adjustmentsList);
	}


	public MessageOperation getMessageOperation() {
		return messageOperation;
	}
}
