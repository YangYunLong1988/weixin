package com.snowstore.diana.domain;

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
@Table(name = "diana_package")
@EntityListeners(AuditingEntityListener.class)
public class Package extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8696791498920585969L;
	private Long orderId;
	private String recipients;// 收件人
	private String address;// 收件地址
	private String mobile;// 联系方式
	private Date receiveTime;// 收件时间
	private Date deadline;// 取件截止日期
	private Date status;
	// 添加省份城市区域信息 @updateDate 2016年1月19日
	private String province;// 省
	private String city;// 市
	private String area;// 区

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Date getStatus() {
		return status;
	}

	public void setStatus(Date status) {
		this.status = status;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
}
