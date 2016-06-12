package com.snowstore.diana.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author Administratior
 *
 */
@Entity
@Table(name = "diana_statistics")
@EntityListeners(AuditingEntityListener.class)
public class Statistics extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7353390201020605791L;
	private Date dateStatistics;// 日期
	private Long accessStatistics = 0L;// 访问统计
	private Long orderStatistics = 0L;// 日订单统计
	private Long ticketStatistics = 0L;// 日出票统计
	private BigDecimal transactionsStatistics = new BigDecimal("0");// 日成交金额
	private String platform;	//用户来源
	private String memo;

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

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
