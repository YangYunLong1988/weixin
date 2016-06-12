package com.snowstore.diana.vo;

import java.io.Serializable;

/**
 * 代金券
 * 
 * @author stone
 *
 */
public class TicketBuyCodeVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String code;// 编码
	private String type;// 类型A,B
	private int money;// 金额
	private String desc;// 代金券描述

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public String getDesc() {
		return (desc == null || desc.length() == 0) ? "代金券" : desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
