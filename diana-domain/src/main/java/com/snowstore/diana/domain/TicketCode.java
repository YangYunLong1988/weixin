package com.snowstore.diana.domain;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "diana_ticket_code")
@EntityListeners(AuditingEntityListener.class)
public class TicketCode extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	// Fields
	@Version
	private Long version;
	private String ticketCode;// 取票码
	private String channelType;// 取票码渠道
	private String status;// 取票码状态

	// Cascade
	@OneToOne
	@JoinColumn(name = "ticket_id")
	private Tickets tickets;

	// Constructors
	/**** defalust constructor ***/
	public TicketCode() {

	}

	// Property accessors
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getTicketCode() {
		return ticketCode;
	}

	public void setTicketCode(String ticketCode) {
		this.ticketCode = ticketCode;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Tickets getTickets() {
		return tickets;
	}

	public void setTickets(Tickets tickets) {
		this.tickets = tickets;
	}

	public enum ChannelType {
		爱奇艺, 猫眼, 大众点评
	}

	public enum Status {
		未分配, 已分配, 已取票
	}

}
