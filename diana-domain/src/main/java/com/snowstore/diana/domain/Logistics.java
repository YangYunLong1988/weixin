package com.snowstore.diana.domain;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "diana_logistics")
@EntityListeners(AuditingEntityListener.class)
public class Logistics extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	@OneToOne
	private Order order;
	@Enumerated(EnumType.STRING)
	private LogisticsStatus status = LogisticsStatus.已提交;
	private String sn;
	private String company;
	private String memo;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public LogisticsStatus getStatus() {
		return status;
	}

	public void setStatus(LogisticsStatus status) {
		this.status = status;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public enum LogisticsStatus {
		已提交, 拣货中, 已发货
	}
}
