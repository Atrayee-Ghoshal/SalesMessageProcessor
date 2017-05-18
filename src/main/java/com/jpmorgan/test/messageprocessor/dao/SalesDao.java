package com.jpmorgan.test.messageprocessor.dao;

import org.springframework.stereotype.Repository;

import com.jpmorgan.test.messageprocessor.entity.ProductType;
import com.jpmorgan.test.messageprocessor.entity.Sale;

/**
 * Data Layer class which will create a Sale.
 * @author atrayee ghoshal
 *
 */
@Repository
public class SalesDao {
	
	public Sale getSales(ProductType productType, Double value) {
		return new Sale(value, productType);
	}
}
