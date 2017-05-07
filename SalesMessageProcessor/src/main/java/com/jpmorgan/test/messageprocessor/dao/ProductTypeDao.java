package com.jpmorgan.test.messageprocessor.dao;

import java.util.Random;

import org.springframework.stereotype.Repository;

import com.jpmorgan.test.messageprocessor.entity.ProductType;

/**
 * Data Layer class which will create a Product Type.
 * @author atrayee ghoshal
 *
 */
@Repository
public class ProductTypeDao {
	public ProductType getProduct(String productType) {
		Random rand = new Random();
		Integer productTypeId = rand.nextInt(200) + 1;
		return new ProductType(productTypeId, productType);
	}
	
}
