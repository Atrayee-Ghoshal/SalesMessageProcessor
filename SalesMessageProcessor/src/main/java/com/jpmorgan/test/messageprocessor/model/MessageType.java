package com.jpmorgan.test.messageprocessor.model;

import java.util.Map;

import com.jpmorgan.test.messageprocessor.entity.Sale;
import com.jpmorgan.test.messageprocessor.entity.SalesRecord;

/**
 * Skeleton class for all Message Type objects.
 * @author atrayee ghoshal
 *
 */
public abstract class MessageType {
	
	private Sale sale;
	
	public MessageType(Sale sale) {
		super();
		this.sale = sale;
	}

	public abstract void processMessage(Map<Integer, SalesRecord> salesRecordMap);
	
	public Sale getSale() {
		return sale;
	}
}
