package com.snowstore.diana.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "diana_gift_code")
@EntityListeners(AuditingEntityListener.class)
public class GiftCode extends AbstractEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -963985126992842355L;
	// Fields
	private String redeemCode;// 兑换码-1代金卷;2、3小杯暴风雪;4,5咖啡卷
	private String redeemCodePassword;// 兑换码密码-1代金卷;2、3小杯暴风雪;4,5咖啡卷
	private String redeemCodeType;// 兑换码类型
	private String status;// 兑换码状态
	private Date validityPeriod; // 兑换码有效日期
	// Cascade
	private Long orderId;// 订单id

	// Constructors
	/*** default constructor ***/
	public GiftCode() {

	}

	public String getRedeemCode() {
		return redeemCode;
	}

	public void setRedeemCode(String redeemCode) {
		this.redeemCode = redeemCode;
	}

	public String getRedeemCodePassword() {
		return redeemCodePassword;
	}

	public void setRedeemCodePassword(String redeemCodePassword) {
		this.redeemCodePassword = redeemCodePassword;
	}

	public String getRedeemCodeType() {
		return redeemCodeType;
	}

	public void setRedeemCodeType(String redeemCodeType) {
		this.redeemCodeType = redeemCodeType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(Date validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public enum Status {
		未分配, 已分配, 已兑换
	}

	public enum RedeemCodeType {
		DQ
	}
}
