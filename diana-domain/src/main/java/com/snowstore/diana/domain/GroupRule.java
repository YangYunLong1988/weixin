package com.snowstore.diana.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "diana_group_rule")
@EntityListeners(AuditingEntityListener.class)
public class GroupRule extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	// Fileds
	private String ruleName;// 规则名称
	private BigDecimal price;// 产品金额
	private Integer ticketNumber;// 票数
	private BigDecimal minMoney;// 最小抽奖金额
	private BigDecimal maxMoney;// 最大抽奖金额

	// Cascade

	// Constructors
	/*** default contructor ***/
	public GroupRule() {

	}

	// property accessors
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(Integer ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	public BigDecimal getMinMoney() {
		return minMoney;
	}

	public void setMinMoney(BigDecimal minMoney) {
		this.minMoney = minMoney;
	}

	public BigDecimal getMaxMoney() {
		return maxMoney;
	}

	public void setMaxMoney(BigDecimal maxMoney) {
		this.maxMoney = maxMoney;
	}

}
