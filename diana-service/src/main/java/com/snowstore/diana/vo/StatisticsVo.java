package com.snowstore.diana.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class StatisticsVo implements Serializable {

	private String platform;
	private Date dateStatistics;// 日期
	private Long accessStatistics;// 访问统计
	private Long orderStatistics;// 日订单统计
	private Long ticketStatistics;// 日出票统计
	private BigDecimal transactionsStatistics;// 日成交金额
	private Long id;
	private String memo;
	private static final long serialVersionUID = -2375004999947244772L;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDateStatistics() {
		return dateStatistics;
	}

	public void setDateStatistics(Date dateStatistics) {
		this.dateStatistics = dateStatistics;
	}

	public Long getAccessStatistics() {
		return accessStatistics;
	}

	public void setAccessStatistics(Long accessStatistics) {
		this.accessStatistics = accessStatistics;
	}

	public Long getOrderStatistics() {
		return orderStatistics;
	}

	public void setOrderStatistics(Long orderStatistics) {
		this.orderStatistics = orderStatistics;
	}

	public Long getTicketStatistics() {
		return ticketStatistics;
	}

	public void setTicketStatistics(Long ticketStatistics) {
		this.ticketStatistics = ticketStatistics;
	}

	public BigDecimal getTransactionsStatistics() {
		return transactionsStatistics;
	}

	public void setTransactionsStatistics(BigDecimal transactionsStatistics) {
		this.transactionsStatistics = transactionsStatistics;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
}
