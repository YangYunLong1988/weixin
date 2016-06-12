package com.snowstore.diana.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.snowstore.diana.utils.DateUtils;

/**
 * 订单VO对象
 * 
 * @author: fuhongxing
 * @date: 2015年10月20日
 * @version 1.0.0
 */
public class OrderVo {

	private Long id; // 订单编号

	private String tradeNo; // 订单标号

	private String mobile; // 绑定手机

	private String platform;// 所属渠道
	
	private String productName;// 理财产品名称

	private BigDecimal amount;// 订单金额

	private BigDecimal payAmount;// 支付金额

	private BigDecimal cardStockAmount;// 卡券金额

	private String status;// 订单状态

	private Date createdDate;// 提交时间

	private Integer alreadyConversion;// 已经兑票

	private Integer gift; // 领取礼品数量

	private Integer availableConversion;// 剩余可兑
	
	private String cardStockMemo;//卡券使用类型

	private String type; // 类型
	
	private String payType;//支付类型
	
	private Long platformId;	//
	@JsonIgnore
	private Date beginDate;
	@JsonIgnore
	private Date endDate;

	public Long getId() {
		return id;
	}

	public OrderVo() {
		super();
	}

	public OrderVo(String mobile, String productName, String status, String beginDate, String endDate, String type) {
		super();
		this.mobile = mobile;
		this.productName = productName;
		this.status = status;

		this.type = type;
		
		if (StringUtils.isNotEmpty(beginDate)) {
			this.beginDate = DateUtils.StringToDate(beginDate, "yyyy-MM-dd");
		}
		if (StringUtils.isNotEmpty(endDate)) {
			this.endDate = DateUtils.StringToDate(endDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
		}
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public BigDecimal getCardStockAmount() {
		return cardStockAmount;
	}

	public void setCardStockAmount(BigDecimal cardStockAmount) {
		this.cardStockAmount = cardStockAmount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getAlreadyConversion() {
		return alreadyConversion;
	}

	public void setAlreadyConversion(Integer alreadyConversion) {
		this.alreadyConversion = alreadyConversion;
	}

	public Integer getGift() {
		return gift;
	}

	public void setGift(Integer gift) {
		this.gift = gift;
	}

	public Integer getAvailableConversion() {
		return availableConversion;
	}

	public void setAvailableConversion(Integer availableConversion) {
		this.availableConversion = availableConversion;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Long getPlatformId() {
		return platformId;
	}

	public String getCardStockMemo() {
		return cardStockMemo;
	}

	public void setCardStockMemo(String cardStockMemo) {
		this.cardStockMemo = cardStockMemo;
	}

	public void setPlatformId(Long platformId) {
		this.platformId = platformId;
	}
}
