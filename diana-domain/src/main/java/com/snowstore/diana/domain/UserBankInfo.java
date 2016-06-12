package com.snowstore.diana.domain;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "diana_user_bankinfo")
@EntityListeners(AuditingEntityListener.class)
public class UserBankInfo extends AbstractEntity {

	/**
	 * @author XieZG
	 * @Date:2016年2月19日下午3:43:46
	 */
	private static final long serialVersionUID = 7084216234738799734L;
	private Long authSerialNumber;// 认证流水号（经纪会员所属平台唯一）
	private String payChannel; // 支付通道
	private String accountType; // 账户类型
	private String accountName; // 账户名称
	private String accountNumber; // 账户号码
	private String bankCode; // 银行编码
	private String province; // 分支行省份
	private String city; // 分支行城市
	private String chName; // 分支行
	private String identificationType; // 证件类型
	private String identificationNumber; // 证件号码
	private String phoneNumber; // 预留手机号码
	private String cardType; // 卡类型
	private String status; // 认证状态
	private BigDecimal singleLimit;// 单笔限额
	private BigDecimal dayLimit;// 单天限额
	private String bankName;// 银行名称
	/**
	 * 用户唯一ID
	 */
	private Long unionId;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "userId")
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public enum Status {
		快捷认证失败, 快捷认证中, 快捷认证已解除, 快捷认证成功
	}

	/**
	 * 认证流水号（经纪会员所属平台唯一）
	 */
	public Long getAuthSerialNumber() {
		return authSerialNumber;
	}

	public void setAuthSerialNumber(Long authSerialNumber) {
		this.authSerialNumber = authSerialNumber;
	}

	/**
	 * 支付通道
	 */
	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	/**
	 * 账户类型
	 */
	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	/**
	 * 账户名称
	 */
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * 账户号码
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * 银行编码
	 */
	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	/**
	 * 分支行省份
	 */
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * 分支行城市
	 */
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * 分支行
	 */
	public String getChName() {
		return chName;
	}

	public void setChName(String chName) {
		this.chName = chName;
	}

	/**
	 * 证件类型
	 */
	public String getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	/**
	 * 证件号码
	 */
	public String getIdentificationNumber() {
		return identificationNumber;
	}

	public void setIdentificationNumber(String identificationNumber) {
		this.identificationNumber = identificationNumber;
	}

	/**
	 * 预留手机号码
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * 卡类型
	 */
	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getSingleLimit() {
		return singleLimit;
	}

	public void setSingleLimit(BigDecimal singleLimit) {
		this.singleLimit = singleLimit;
	}

	public BigDecimal getDayLimit() {
		return dayLimit;
	}

	public void setDayLimit(BigDecimal dayLimit) {
		this.dayLimit = dayLimit;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Long getUnionId() {
		return unionId;
	}

	public void setUnionId(Long unionId) {
		this.unionId = unionId;
	}

	@Override
	public String toString() {
		return "QuickAuthenticationReq [authSerialNumber=" + authSerialNumber + ", payChannel=" + payChannel + ", accountType=" + accountType + ", accountName=" + accountName + ", accountNumber="
				+ accountNumber + ", bankCode=" + bankCode + ", province=" + province + ", city=" + city + ", chName=" + chName + ", identificationType=" + identificationType
				+ ", identificationNumber=" + identificationNumber + ", phoneNumber=" + phoneNumber + ", cardType=" + cardType + ", toString()=" + super.toString() + "]";
	}
}
