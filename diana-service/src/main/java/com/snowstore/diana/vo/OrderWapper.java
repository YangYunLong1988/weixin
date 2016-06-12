package com.snowstore.diana.vo;

import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Product;

public class OrderWapper {
	private Boolean pickallgiftornot;//用户没有领取礼品或者没有兑换电影票的时候都设置为true
	private Order order;
	private Product product;
	
	public Boolean getPickallgiftornot() {
		return pickallgiftornot;
	}
	public void setPickallgiftornot(Boolean pickallgiftornot) {
		this.pickallgiftornot = pickallgiftornot;
	}
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	
}
