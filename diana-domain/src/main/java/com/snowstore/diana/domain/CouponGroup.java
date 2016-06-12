package com.snowstore.diana.domain;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wulinjie on 2015/11/10.
 */
@Entity
@Table(name = "diana_coupon_group")
@EntityListeners(AuditingEntityListener.class)
public class CouponGroup implements Serializable {
	
	private static final long serialVersionUID = 1l;
	
	private Long id;			//卡券组批次号
	private String groupName;	//卡券组名称
	private String typeCode;	//卡劝组编号
	private long ruleUseId;		//使用规则ID
	private BigDecimal amout;	//价值
	private long total;			//卡券总量
	private Date startDate;		//生效日期
	private Date endDate;		//截止日期
	private int status;			//状态(0-无效,1-有效)
	private String secretKey;	//加密密钥（活动，规则）
	private int isGroupMovie;	//是否集团投资(0-否,1-是)
	private int enablePwd;		//是否加密(0-否,1-是)
	private String note;		//备注

	public BigDecimal getAmout() {
		return amout;
	}

	public void setAmout(BigDecimal amout) {
		this.amout = amout;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public long getRuleUseId() {
		return ruleUseId;
	}

	public void setRuleUseId(long ruleUseId) {
		this.ruleUseId = ruleUseId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public int getEnablePwd() {
		return enablePwd;
	}

	public void setEnablePwd(int enablePwd) {
		this.enablePwd = enablePwd;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public int getIsGroupMovie() {
		return isGroupMovie;
	}

	public void setIsGroupMovie(int isGroupMovie) {
		this.isGroupMovie = isGroupMovie;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
