package com.snowstore.diana.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 卡劵实体类
 * 
 * @author XieZG
 * @Date:2015年11月13日上午10:51:42
 */
@Entity
@Table(name = "diana_card_stock")
@EntityListeners(AuditingEntityListener.class)
public class CardStock extends AbstractEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1598019339753902179L;
	/**
	 * 卡劵类型
	 */
	private String type;
	/**
	 * 金额
	 */
	private BigDecimal amount;
	/**
	 * 礼品
	 */
	private Integer gift;
	/**
	 * 用户
	 */
	private Long userId;
	/**
	 * 用户唯一ID
	 */
	private Long unionId;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 关联订单
	 */
	private Long orderId;
	/**
	 * 兑换码
	 */
	private String exchangeCode;

	private String memo;

	public enum Status {
		未使用, 已使用, 已过期
	}

	public enum Type {
		影视, 门票, 裸票抽奖券, 乐天玛特抵扣券,大轰炸
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getGift() {
		return gift;
	}

	public void setGift(Integer gift) {
		this.gift = gift;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExchangeCode() {
		return exchangeCode;
	}

	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUnionId() {
		return unionId;
	}

	public void setUnionId(Long unionId) {
		this.unionId = unionId;
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
}
