package com.jpmorgan.test.messageprocessor.entity;

/**
 * Entity Class for Product Type.
 * @author atrayee ghoshal
 *
 */
public class ProductType {

	private Integer productTypeId;
	private String productType;
	
	public ProductType(Integer productTypeId, String productType) {
		super();
		this.productTypeId = productTypeId;
		this.productType = productType;
	}

	
	public Integer getProductTypeId() {
		return productTypeId;
	}

	public void setProductTypeId(Integer productTypeId) {
		this.productTypeId = productTypeId;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

}