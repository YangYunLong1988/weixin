package com.snowstore.diana.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Administratior
 *
 */
public class ProductVo {
	
	private Long productId; // 产品ID
	private String name; // 产品名称
	private BigDecimal availableAmount;	//剩余可投

	private String status; // 状态
	
	private Date createdDateStart;
	private Date createdDateEnd;
	
	private String img;
	private String type; // 产品类型
	
	
	private BigDecimal price; // 每份价格
	private Long exchangeAmount; // 兑换数量
	private Integer giftNum; // 礼品数量
	private Long refConcert; // 演唱会
	private String url; // 链接地址
	private Boolean isOff;//是否售罄

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedDateStart() {
		return createdDateStart;
	}

	public void setCreatedDateStart(Date createdDateStart) {
		this.createdDateStart = createdDateStart;
	}

	public Date getCreatedDateEnd() {
		return createdDateEnd;
	}

	public void setCreatedDateEnd(Date createdDateEnd) {
		this.createdDateEnd = createdDateEnd;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Long getExchangeAmount() {
		return exchangeAmount;
	}

	public void setExchangeAmount(Long exchangeAmount) {
		this.exchangeAmount = exchangeAmount;
	}

	public Integer getGiftNum() {
		return giftNum;
	}

	public void setGiftNum(Integer giftNum) {
		this.giftNum = giftNum;
	}

	public Long getRefConcert() {
		return refConcert;
	}

	public void setRefConcert(Long refConcert) {
		this.refConcert = refConcert;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getIsOff() {
		return isOff;
	}

	public void setIsOff(Boolean isOff) {
		this.isOff = isOff;
	}
}
