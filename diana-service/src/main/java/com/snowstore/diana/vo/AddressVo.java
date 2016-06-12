package com.snowstore.diana.vo;

import java.io.Serializable;

/**
 * 收货地址
 * 
 * @author stone
 *
 */
public class AddressVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String person;// 收货人
	private String phone;// 收货人手机号
	private String address;// 收货地址

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
}
