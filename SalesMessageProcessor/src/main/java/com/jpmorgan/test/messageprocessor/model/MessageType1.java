package com.jpmorgan.test.messageprocessor.model;

import java.util.Map;

import com.jpmorgan.test.messageprocessor.entity.ProductType;
import com.jpmorgan.test.messageprocessor.entity.Sale;
import com.jpmorgan.test.messageprocessor.entity.SalesRecord;

/**
 * Class for Message Type 1 object.
 * @author atrayee ghoshal
 *
 */
public class MessageType1 extends MessageType {
	
	public MessageType1(Sale sale) {
		super(sale);
	}

	@Override
	public void processMessage(Map<Integer, SalesRecord> salesRecordMap) {
		Sale sale = getSale();
		ProductType product= sale.getProductType();
		Integer productTypeId = product.getProductTypeId();

		if (salesRecordMap.containsKey(productTypeId)) {
			SalesRecord salesRecord = salesRecordMap.get(productTypeId);
			salesRecord.setNumOfSales(salesRecord.getNumOfSales() + 1);
			salesRecord.setTotalSalesValue(salesRecord.getTotalSalesValue() + sale.getSaleValue());
		} else {
			SalesRecord salesRecord = new SalesRecord(productTypeId, sale.getSaleValue(), 1, sale);
			salesRecordMap.put(productTypeId, salesRecord);
		}
		
	}

}
