package com.snowstore.diana.vo;

import java.io.Serializable;

/**
 * 礼物
 * 
 * @author stone
 * 
 */
public class GiftVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private boolean isSelected;// 是否已选择
	private String person;// 收货人
	private String phone;// 收货人手机号
	private String address;// 收货人地址
	private String gift;// 礼物
	private String giftName;//
	private String logisticsSn;// 物流订单号
	private String logisticsCompany;// 物流承运公司
	private String logisticsStatus;// 物流状态
	private String province;// 省
	private String city;// 市
	private String area;// 区
	private String giftNo = "";// 礼券码
	private String giftNo1 = "";// 礼券码1
	private String giftNo2 = "";// 礼券码2
	private String giftNo3 = "";// 礼券码3
	private String giftNo4 = "";// 礼券码4
	private Long orderId;// 订单id

	public String getGiftNo1() {
		return giftNo1;
	}

	public void setGiftNo1(String giftNo1) {
		this.giftNo1 = giftNo1;
	}

	public String getGiftNo2() {
		return giftNo2;
	}

	public void setGiftNo2(String giftNo2) {
		this.giftNo2 = giftNo2;
	}

	public String getGiftNo3() {
		return giftNo3;
	}

	public void setGiftNo3(String giftNo3) {
		this.giftNo3 = giftNo3;
	}

	public String getGiftNo4() {
		return giftNo4;
	}

	public void setGiftNo4(String giftNo4) {
		this.giftNo4 = giftNo4;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGift() {
		return gift;
	}

	public void setGift(String gift) {
		this.gift = gift;
	}

	public String getLogisticsSn() {
		return logisticsSn;
	}

	public void setLogisticsSn(String logisticsSn) {
		this.logisticsSn = logisticsSn;
	}

	public String getLogisticsCompany() {
		return logisticsCompany;
	}

	public void setLogisticsCompany(String logisticsCompany) {
		this.logisticsCompany = logisticsCompany;
	}

	public String getLogisticsStatus() {
		return logisticsStatus;
	}

	public void setLogisticsStatus(String logisticsStatus) {
		this.logisticsStatus = logisticsStatus;
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

	public String getGiftName() {
		return giftName;
	}

	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}

	public String getGiftNo() {
		return giftNo;
	}

	public void setGiftNo(String giftNo) {
		this.giftNo = giftNo;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

}
