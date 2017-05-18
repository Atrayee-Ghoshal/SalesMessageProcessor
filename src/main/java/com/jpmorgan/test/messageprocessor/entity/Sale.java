package com.jpmorgan.test.messageprocessor.entity;

/**
 * Entitiy class for Sale.
 * @author atrayee ghoshal
 *
 */
public class Sale {

	private Double saleValue;
	private ProductType productType;

	
	public Sale(Double saleValue, ProductType productType) {
		super();
		this.saleValue = saleValue;
		this.productType = productType;
	}

	public Double getSaleValue() {
		return saleValue;
	}

	public void setSaleValue(Double saleValue) {
		this.saleValue = saleValue;
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}
}
