package com.snowstore.diana.vo;

import java.io.Serializable;

/**
 * 门票场次
 * 
 * @author stone
 *
 */
public class TicketTimeVo implements Serializable  {
	private static final long serialVersionUID = 1L;
	private String key;
	private String value;
	private boolean isValid;// 是否有效

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
}
