package com.snowstore.diana.vo.logistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

@XmlAccessorType(XmlAccessType.FIELD)
public class RequestOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	private String MemberNO;
	private String WareHouseCode;
	private String SaleOrderCode;
	private String ExpressCode;
	private Receiver Receiver;
	private String ItemsValue;
	private String OrderAmount;
	private String Payment;
	@XmlElements(value = { @XmlElement(name = "Item", type = Item.class) })
	private List<Item> Items = new ArrayList<Item>();
	private String Remark;
	private String TradeId;

	public String getMemberNO() {
		return MemberNO;
	}

	public void setMemberNO(String memberNO) {
		MemberNO = memberNO;
	}

	public String getWareHouseCode() {
		return WareHouseCode;
	}

	public void setWareHouseCode(String wareHouseCode) {
		WareHouseCode = wareHouseCode;
	}

	public String getSaleOrderCode() {
		return SaleOrderCode;
	}

	public void setSaleOrderCode(String saleOrderCode) {
		SaleOrderCode = saleOrderCode;
	}

	public String getExpressCode() {
		return ExpressCode;
	}

	public void setExpressCode(String expressCode) {
		ExpressCode = expressCode;
	}

	public Receiver getReceiver() {
		return Receiver;
	}

	public void setReceiver(Receiver receiver) {
		this.Receiver = receiver;
	}

	public String getItemsValue() {
		return ItemsValue;
	}

	public void setItemsValue(String itemsValue) {
		ItemsValue = itemsValue;
	}

	public String getOrderAmount() {
		return OrderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		OrderAmount = orderAmount;
	}

	public String getPayment() {
		return Payment;
	}

	public void setPayment(String payment) {
		Payment = payment;
	}

	public List<Item> getItems() {
		return Items;
	}

	public void setItems(List<Item> items) {
		this.Items = items;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public String getTradeId() {
		return TradeId;
	}

	public void setTradeId(String tradeId) {
		TradeId = tradeId;
	}
}
