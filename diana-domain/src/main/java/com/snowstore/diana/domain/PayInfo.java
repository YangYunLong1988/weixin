package com.snowstore.diana.domain;

import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author Administratior
 * 
 */
@Entity
@Table(name = "diana_pay_info")
@EntityListeners(AuditingEntityListener.class)
public class PayInfo extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7417513269115045531L;

	private String platform;// 接入平台

	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String req;// 请求参数

	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String res;// 响应参数

	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String payResultNotify;

	private String status;// 支付状态

	private String payPlatform;// 支付平台

	private String referenceOrder;// 对应易联-->订单编号
	
	private Long orderId;

	private BigDecimal payAmount;
	
	private String tradeNo;

	private String memo;// 备注

	public enum Platform {
		WEIXIN_PAY_JS_API, WEIXIN_PAY_APP, CARD_STOCK_PAY,QUICK_PAY,OMIT_PAY
	}

	/**
	 * 支付类型0,1,2
	 * 
	 * @author XieZG
	 * @Date:2015年11月16日下午3:38:46
	 */
	public enum PayType {
		微信, 支付宝, 卡券,快捷支付
	}

	public enum Status {
		未支付, 支付失败, 支付成功
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getReq() {
		return req;
	}

	public void setReq(String req) {
		this.req = req;
	}

	public String getRes() {
		return res;
	}

	public void setRes(String res) {
		this.res = res;
	}

	public String getPayResultNotify() {
		return payResultNotify;
	}

	public void setPayResultNotify(String payResultNotify) {
		this.payResultNotify = payResultNotify;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPayPlatform() {
		return payPlatform;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public void setPayPlatform(String payPlatform) {
		this.payPlatform = payPlatform;
	}

	public String getReferenceOrder() {
		return referenceOrder;
	}

	public void setReferenceOrder(String referenceOrder) {
		this.referenceOrder = referenceOrder;
	}
	

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
}
