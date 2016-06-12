package com.snowstore.diana.vo;

import java.io.Serializable;

/**
 * 票订单
 * 
 * @author stone
 *
 */
public class TicketOrderVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private long productId;// 产品ID
	private String name;// 名称
	private String scene;// 场所
	private TicketTimeVo time;// 时间场次
	private TicketAreaVo area;// 座位区域
	private double price;// 单价
	private double payAmount;//实际支付金额
	private int count;// 份数
	private double money;// 总金额
	private boolean isDelivery;// 是否快递
	private AddressVo deliveryAddress;// 快递收货地址
	private String diyAddress;// 上门自取地址
	private String diyTime;// 上门自取时间
	private int limit;// 每人购买限次，若无则为0或－1
	private boolean isMovieTicket;// 是否电影票
	private int ticketScale;// 每份对应的电影票数量
	private int giftNum;// 礼物数

	public AddressVo getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(AddressVo deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getDiyAddress() {
		return diyAddress;
	}

	public void setDiyAddress(String diyAddress) {
		this.diyAddress = diyAddress;
	}

	public String getDiyTime() {
		return diyTime;
	}

	public void setDiyTime(String diyTime) {
		this.diyTime = diyTime;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScene() {
		return scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}

	public TicketTimeVo getTime() {
		return time;
	}

	public void setTime(TicketTimeVo time) {
		this.time = time;
	}

	public TicketAreaVo getArea() {
		return area;
	}

	public void setArea(TicketAreaVo area) {
		this.area = area;
	}

	public boolean isDelivery() {
		return isDelivery;
	}

	public void setDelivery(boolean isDelivery) {
		this.isDelivery = isDelivery;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isMovieTicket() {
		return isMovieTicket;
	}

	public void setMovieTicket(boolean isMovieTicket) {
		this.isMovieTicket = isMovieTicket;
	}

	public int getTicketScale() {
		return ticketScale;
	}

	public void setTicketScale(int ticketScale) {
		this.ticketScale = ticketScale;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getGiftNum() {
		return giftNum;
	}

	public void setGiftNum(int giftNum) {
		this.giftNum = giftNum;
	}

	public double getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(double payAmount) {
		this.payAmount = payAmount;
	}

}
