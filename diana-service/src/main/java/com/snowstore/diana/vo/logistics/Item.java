package com.snowstore.diana.vo.logistics;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Item implements Serializable {
	private static final long serialVersionUID = 1L;
	private String SendType = "SEND";
	private String SpuCode;
	private String ItemName;
	private String ItemCount;
	private String ItemValue;

	public String getSendType() {
		return SendType;
	}

	public void setSendType(String sendType) {
		SendType = sendType;
	}

	public String getSpuCode() {
		return SpuCode;
	}

	public void setSpuCode(String spuCode) {
		SpuCode = spuCode;
	}

	public String getItemName() {
		return ItemName;
	}

	public void setItemName(String itemName) {
		ItemName = itemName;
	}

	public String getItemCount() {
		return ItemCount;
	}

	public void setItemCount(String itemCount) {
		ItemCount = itemCount;
	}

	public String getItemValue() {
		return ItemValue;
	}

	public void setItemValue(String itemValue) {
		ItemValue = itemValue;
	}

}
