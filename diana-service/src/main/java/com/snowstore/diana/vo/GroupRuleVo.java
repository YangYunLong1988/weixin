package com.snowstore.diana.vo;

import java.math.BigDecimal;

public class GroupRuleVo {
	// Fileds
	private long id;
	private String ruleName;// 规则名称
	private BigDecimal price;// 产品金额
	private Integer ticketNumber;// 票数
	private BigDecimal minMoney;// 最小抽奖金额
	private BigDecimal maxMoney;// 最大抽奖金额

	// Constructors
	/*** default contructor ***/
	public GroupRuleVo() {

	}

	// property accessors
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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
