package com.snowstore.diana.domain;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 贝思客蛋糕券
 * 
 * @author XieZG
 * @Date:2015年11月13日上午10:51:42
 */
@Entity
@Table(name = "diana_best_cake_card")
@EntityListeners(AuditingEntityListener.class)
public class BestcakeCard extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2044581822540380914L;
	private String code;
	private Boolean isBind = false;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Boolean getIsBind() {
		return isBind;
	}
	public void setIsBind(Boolean isBind) {
		this.isBind = isBind;
	}
	
}
