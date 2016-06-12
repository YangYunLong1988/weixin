package com.snowstore.diana.vo;

/**
 * 用户礼品VO
 * 
 * @author: fuhongxing
 * @date: 2015年10月22日
 * @version 1.0.0
 */
public class UserGiftVo {
	private String giftName;// 礼品名称
	private Integer num = 1; // 礼品数量
	private String recipients;// 取件人
	private String mobile;// 手机号码
	private String province;// 省
	private String city;// 市
	private String area;// 区
	private String address;// 配送地址
	private String logisticsSn;// 物流订单号
	private String logisticsCompany;// 物流承运公司
	private String logisticsStatus;// 物流状态

	public UserGiftVo() {
		super();
	}

	public UserGiftVo(String giftName, Integer num, String recipients, String mobile, String address) {
		super();
		this.giftName = giftName;
		this.num = num;
		this.recipients = recipients;
		this.mobile = mobile;
		this.address = address;
	}

	public String getGiftName() {
		return giftName;
	}

	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getCity() {
		return city;
	}

	public String getArea() {
		return area;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setArea(String area) {
		this.area = area;
	}
}
