package com.snowstore.diana.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.snowstore.diana.utils.DateUtils;

/**
 * 用户VO对象
 * 
 * @author: fuhongxing
 * @date: 2015年10月20日
 * @version 1.0.0
 */
public class UserVo {

	private Long id;// 用户ID

	private String mobile; // 绑定手机

	private Date createdDate;// 接入时间

	private Integer orderCount; // 购买次数

	private BigDecimal consumerTotal;// 消费总额

	private String platform;// 用户来源

	private Long platformId;	//渠道ID

	private Date beginDate;

	private Date endDate;

	private String role; // 用户角色

	private String isMng;// 是否是管理员

	public UserVo() {
		super();
	}

	public UserVo(String id, String mobile, String beginDate, String endDate) {
		super();
		if (StringUtils.isNotEmpty(id)) {
			this.id = Long.valueOf(id);
		}
		if (StringUtils.isNotEmpty(mobile)) {
			this.mobile = mobile;
		}
		if (StringUtils.isNotEmpty(beginDate)) {
			this.beginDate = DateUtils.StringToDate(beginDate, "yyyy-MM-dd");
		}
		if (StringUtils.isNotEmpty(endDate)) {
			this.endDate = DateUtils.StringToDate(endDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}

	public BigDecimal getConsumerTotal() {
		return consumerTotal;
	}

	public void setConsumerTotal(BigDecimal consumerTotal) {
		this.consumerTotal = consumerTotal;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getIsMng() {
		return isMng;
	}

	public void setIsMng(String isMng) {
		this.isMng = isMng;
	}

	public Long getPlatformId() {
		return platformId;
	}

	public void setPlatformId(Long platformId) {
		this.platformId = platformId;
	}
}
