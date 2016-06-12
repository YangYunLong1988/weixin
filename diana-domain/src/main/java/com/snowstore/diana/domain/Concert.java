package com.snowstore.diana.domain;

import java.util.Date;

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
@Table(name = "diana_concert")
@EntityListeners(AuditingEntityListener.class)
public class Concert extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4473135500989464813L;
	private String name;
	private Date performTime;//演出时间
	private String performAddress;//演出地点
	@Transient
	private Long areaCount; // 区间数量
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getPerformTime() {
		return performTime;
	}
	public void setPerformTime(Date performTime) {
		this.performTime = performTime;
	}
	public String getPerformAddress() {
		return performAddress;
	}
	public void setPerformAddress(String performAddress) {
		this.performAddress = performAddress;
	}
	public Long getAreaCount() {
		return areaCount;
	}
	public void setAreaCount(Long areaCount) {
		this.areaCount = areaCount;
	}
}
