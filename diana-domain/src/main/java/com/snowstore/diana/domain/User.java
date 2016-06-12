package com.snowstore.diana.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// JPA实体类的标识
@Entity
@Table(name = "diana_user")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "order" })
public class User extends AbstractEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3376947198374677360L;

	private String username;// 昵称

	private String password;

	private String email;

	private String mobile;

	private String status;

	private String platform;// 用户来源
	private String channel;// 渠道

	private Date lastLoginTime;

	private String role;

	private String mobileVerifyCode;// 短信验证码

	private Date mobileVerifyCodeTime; // 验证码生成时间

	private String isMng = User.MngState.ISNOTMNG.name(); // 是否是管理员

	private Long platformNo;		//平台编号

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<Order> order = new HashSet<Order>(); // 用户与订单一对多关联

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public enum Role {
		SYSTEM, CUSTOMER,ADMIN,CHANNEL
	}

	public Set<Order> getOrder() {
		return order;
	}

	public void setOrder(Set<Order> order) {
		this.order = order;
	}

	public String getIsMng() {
		return isMng;
	}

	public void setIsMng(String isMng) {
		this.isMng = isMng;
	}

	public String getMobileVerifyCode() {
		return mobileVerifyCode;
	}

	public void setMobileVerifyCode(String mobileVerifyCode) {
		this.mobileVerifyCode = mobileVerifyCode;
	}

	public Date getMobileVerifyCodeTime() {
		return mobileVerifyCodeTime;
	}

	public void setMobileVerifyCodeTime(Date mobileVerifyCodeTime) {
		this.mobileVerifyCodeTime = mobileVerifyCodeTime;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Long getPlatformNo() {
		return platformNo;
	}

	public void setPlatformNo(Long platformNo) {
		this.platformNo = platformNo;
	}

	@JsonIgnore
	@Transient
	public Long getPlatformNoStart(){
		return (this.platformNo / 1000000) * 1000000;
	}

	@JsonIgnore
	@Transient
	public Long getPlatformNoEnd(){
		return (this.platformNo/1000000 + 1) * 1000000;
	}

	/**
	 * 是否是管理员
	 * 
	 * @author wujinsong
	 *
	 */
	public enum MngState {
		ISNOTMNG, ISMNG
	}
}