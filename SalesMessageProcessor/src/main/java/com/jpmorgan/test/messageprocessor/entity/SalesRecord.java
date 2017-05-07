package com.jpmorgan.test.messageprocessor.entity;

import java.util.List;

/**
 * Entity class which will contain the details of Sale for each product.
 * @author atrayee ghoshal
 *
 */
public class SalesRecord {

	private Integer productTypeId;
	private Double totalSalesValue;
	private Integer numOfSales;
	private Sale sale;
	private List<String> adjustments;
	
	public SalesRecord(Integer productTypeId, Double totalSaleValue, Integer numOfSales, Sale sale) {
		this.productTypeId = productTypeId;
		this.totalSalesValue = totalSaleValue;
		this.numOfSales = numOfSales;
		this.sale = sale;
	}
	
	public Integer getProductTypeId() {
		return productTypeId;
	}
	
	public void setProductTypeId(Integer productTypeId) {
		this.productTypeId = productTypeId;
	}
	
	public Double getTotalSalesValue() {
		return totalSalesValue;
	}
	
	public void setTotalSalesValue(Double totalSaleValue) {
		this.totalSalesValue = totalSaleValue;
	}
	
	public Integer getNumOfSales() {
		return numOfSales;
	}
	
	public void setNumOfSales(Integer numOfSales) {
		this.numOfSales = numOfSales;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public List<String> getAdjustments() {
		return adjustments;
	}

	public void setAdjustments(List<String> adjustments) {
		this.adjustments = adjustments;
	}
	
}
