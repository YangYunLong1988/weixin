package com.snowstore.diana.result;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.snowstore.diana.domain.Order;

/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
@Entity
public class OrderExportVo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8765808207894767289L;
	/**
	 * 
	 */
	
	@Id
	private Long id;
	private String mobile;
	private BigDecimal amount;
	private String status;
	private String productName;
	private Date createdDate;
	private int ticketNum;
	private String type;
	private BigDecimal payAmount;
	private String tradeNo;
	private String platform;
	private BigDecimal cardStoneAmount;
	private String payPlatform;
	private String memo;
	private int giftNum;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public BigDecimal getAmount() {
		return amount;
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
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public int getTicketNum() {
		return ticketNum;
	}
	public void setTicketNum(int ticketNum) {
		this.ticketNum = ticketNum;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public BigDecimal getCardStoneAmount() {
		return cardStoneAmount;
	}
	public void setCardStoneAmount(BigDecimal cardStoneAmount) {
		this.cardStoneAmount = cardStoneAmount;
	}
	public String getPayPlatform() {
		if(Order.Status.已撤单.name().equals(this.status)){
			return null;
		}
		return payPlatform;
	}
	public void setPayPlatform(String payPlatform) {
		this.payPlatform = payPlatform;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public int getGiftNum() {
		return giftNum;
	}
	public void setGiftNum(int giftNum) {
		this.giftNum = giftNum;
	}
	
}
