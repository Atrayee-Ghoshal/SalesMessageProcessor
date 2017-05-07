package com.jpmorgan.test.messageprocessor.model;

import java.util.Map;

import com.jpmorgan.test.messageprocessor.entity.ProductType;
import com.jpmorgan.test.messageprocessor.entity.Sale;
import com.jpmorgan.test.messageprocessor.entity.SalesRecord;

/**
 * Class for Message Type 2 object.
 * @author atrayee ghoshal
 *
 */
public class MessageType2 extends MessageType {

	private Integer numOfOccurences;

	public Integer getNumOfOccurences() {
		return numOfOccurences;
	}

	public MessageType2(Sale sale, Integer numOfOccurences) {
		super(sale);
		this.numOfOccurences = numOfOccurences;
	}
	
	@Override
	public void processMessage(Map<Integer, SalesRecord> salesRecordMap) {
		Sale sale = getSale();
		ProductType product= sale.getProductType();
		Integer productTypeId = product.getProductTypeId();

		if (salesRecordMap.containsKey(productTypeId)) {
			SalesRecord salesRecord = salesRecordMap.get(productTypeId);
			salesRecord.setNumOfSales(salesRecord.getNumOfSales() + this.getNumOfOccurences());
			salesRecord.setTotalSalesValue(
					salesRecord.getTotalSalesValue() 
					+ (sale.getSaleValue() * this.getNumOfOccurences()));
		} else {
			SalesRecord salesRecord = new SalesRecord(productTypeId, sale.getSaleValue() * this.getNumOfOccurences(), 
														this.getNumOfOccurences(), sale);
			salesRecordMap.put(productTypeId, salesRecord);
		}
		
	}
}
