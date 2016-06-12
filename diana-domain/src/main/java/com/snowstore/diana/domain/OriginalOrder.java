package com.snowstore.diana.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "diana_original_order")
@EntityListeners(AuditingEntityListener.class)
public class OriginalOrder extends AbstractEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3331421174266299695L;

	private String name; // 姓名

	private String financeProductId; // 理财产品编号

	private String productName;// 经济会员产品名称

	private BigDecimal orderAmt; // 订单金额 ##.00

	private String customerName;// 客户名称

	private String payTime;// 交易时间

	private String orderCreateDate; // 订单创建日期 yyyy-MM-dd

	private String orderStatus;// 订单状态 见8.2

	private String startDate;// 起息日期

	private String buybackDate;// 回购日期

	private BigDecimal buybackAmt;// 回购本息

	private String orderCode;// 订单编号

	private String payStatus;// 支付状态

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFinanceProductId() {
		return financeProductId;
	}

	public void setFinanceProductId(String financeProductId) {
		this.financeProductId = financeProductId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getOrderAmt() {
		return orderAmt;
	}

	public void setOrderAmt(BigDecimal orderAmt) {
		this.orderAmt = orderAmt;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getOrderCreateDate() {
		return orderCreateDate;
	}

	public void setOrderCreateDate(String orderCreateDate) {
		this.orderCreateDate = orderCreateDate;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getBuybackDate() {
		return buybackDate;
	}

	public void setBuybackDate(String buybackDate) {
		this.buybackDate = buybackDate;
	}

	public BigDecimal getBuybackAmt() {
		return buybackAmt;
	}

	public void setBuybackAmt(BigDecimal buybackAmt) {
		this.buybackAmt = buybackAmt;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

}
