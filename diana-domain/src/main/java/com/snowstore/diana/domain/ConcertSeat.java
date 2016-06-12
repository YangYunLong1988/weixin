package com.snowstore.diana.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author Administratior
 *
 */
@Entity
@Table(name = "diana_concert_seat")
@EntityListeners(AuditingEntityListener.class)
public class ConcertSeat extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6901553251687595012L;
	private Long refConcert;
	private String type;
	private BigDecimal price;
	private Integer num;
	/**
	 * 和数据库中id值一一对应。解决前端的id值无法映射到对象
	 */
	@Transient
	private Long idd;
	
	public Long getRefConcert() {
		return refConcert;
	}
	public void setRefConcert(Long refConcert) {
		this.refConcert = refConcert;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Long getIdd() {
		return idd;
	}
	public void setIdd(Long idd) {
		this.idd = idd;
	}
}
