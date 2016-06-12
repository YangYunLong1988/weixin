package com.snowstore.diana.result;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.snowstore.diana.utils.DateUtils;

/**
 * 这只是个连表查询结果对象，并没有相对应的表
 * 
 * @author XieZG
 * @Date:2016年1月7日下午4:54:01
 */
@Entity
public class GiftResult implements Serializable {

	/**
	 * @author XieZG
	 * @Date:2016年1月7日下午3:26:04
	 */
	private static final long serialVersionUID = -4389873831512123207L;
	private String mobile; // 用户手机号码
	@Id
	private Long id; // 礼品编号

	private String orderId; // 订单号

	private String productName;// 购买产品

	private Date giftCreatedDate;// 礼品提交时间

	private String referenceOrder;// 易联订单号

	private Date createdDate;// 订单提交时间

	@Transient
	private Integer receiveGiftNum = 1;// 领取礼品数量

	private String giftName;// 礼品类型

	@Transient
	private Integer giftNum = 1; // 礼品数量

	private String recipients; // 取件人

	private String recipientsMobile;// 取件人手机

	private String province;// 省

	private String city;// 市

	private String area;// 区

	private String address;// 配送地址

	@JsonIgnore
	@Transient
	private Date beginDate;
	@JsonIgnore
	@Transient
	private Date endDate;

	public GiftResult() {

	}

	public GiftResult(String mobile, String id, String beginDate, String endDate) {
		this.mobile = mobile;
		if (StringUtils.isNotEmpty(id)) {
			this.id = Long.valueOf(id);
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Date getGiftCreatedDate() {
		return giftCreatedDate;
	}

	public void setGiftCreatedDate(Date giftCreatedDate) {
		this.giftCreatedDate = giftCreatedDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getGiftName() {
		return giftName;
	}

	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}

	public Integer getReceiveGiftNum() {
		return receiveGiftNum;
	}

	public void setReceiveGiftNum(Integer receiveGiftNum) {
		this.receiveGiftNum = receiveGiftNum;
	}

	public Integer getGiftNum() {
		return giftNum;
	}

	public void setGiftNum(Integer giftNum) {
		this.giftNum = giftNum;
	}

	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getRecipientsMobile() {
		return recipientsMobile;
	}

	public void setRecipientsMobile(String recipientsMobile) {
		this.recipientsMobile = recipientsMobile;
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

	public String getAddress() {
		StringBuilder result = new StringBuilder();
		if (!StringUtils.isEmpty(this.province)) {
			result.append(this.province).append(" ");
		}
		if (!StringUtils.isEmpty(this.city)) {
			result.append(this.city).append(" ");
		}
		if (!StringUtils.isEmpty(this.area)) {
			result.append(this.area).append(" ");
		}
		if (!StringUtils.isEmpty(this.address)) {
			result.append(this.address);
		}
		return result.toString();
	}

	public String getReferenceOrder() {
		return referenceOrder;
	}

	public void setReferenceOrder(String referenceOrder) {
		this.referenceOrder = referenceOrder;
	}

	public void setAddress(String address) {
		this.address = address;
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

	/**
	 * 用于判断giftCreatedDate是否是当天的时间，是返回true
	 * @author YYL
	 * @return action
	 */
	public boolean getGiftCreateDateIsToday() {
		boolean action = false;
		if (null != this.giftCreatedDate) {
			int timeLong =this.giftCreatedDate.compareTo(DateUtils.getCurrenDate());
			if (timeLong >= 0) {
				action = true;
			}
		}
		return action;
	}

	/**
	 * 用于获取未与省市区拼接的详细地址
	 * @author YYL
	 * @return 详细地址
	 */
	public String getDetailAddress() {
		return this.address;
	}
}
