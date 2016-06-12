package com.snowstore.diana.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author Administratior
 *
 */
@Entity
@Table(name = "diana_tickets")
@EntityListeners(AuditingEntityListener.class)
public class Tickets extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7207892679613427027L;

	private String filmNo;			 	//影片编码
	private String name;				//电影名称
	private Date performTime;			//放映时间
	private String cinemaNo;		 	//影院编号
	private String performAddress;	//影院名称
	private String performAddressDetail;//
	private String sceneNo;			//演播厅编码
	private String scene;				//演播厅
	private String seat;				//席位号
	private String type;				//票据类型
	private String language;			//语言
	private String status;				//兑换状态
	private BigDecimal price;			//影票价格
	private Long userId;				//用户ID
	private Long unionId;				//union id
	private Long refOrder;				//订单ID
	private String code;				//电影票兑换码
	private Date timeOut;
	private String consume;			// 消耗方式
	private Long couponId;				//火票卡券ID
	private String ticketCode;		//取票机取票码
	private String ticketCaptcha;		//取票验证码
	private String ticketCodeFront;	//前台取票码
	private Date orderCreatedDate;	//订单创建时间
	private String orderCode;			//出票方订单号
	private BigDecimal orderAmount;	//订单金额
	private Date orderTimeout;		//支付超时时间
	private String orderPayStatus;	//火票订单支付状态 UNPAID-未支付,PAID-支付成功,FAILED-支付失败
	private String printMode;		 	//短信类型
	private String goodsType;		 	//商品类型
	private String voucherCode;	 	//凭证号
	private String printCode;		 	//取票号
	private String smsInfo;			//短信内容
	private String agency;			 	//出票服务机构
	private String orderStatus;		//票务订单状态
	@OneToOne
	@JoinColumn(name="sub_order")
	private Order subOrder;			//电影票转礼品权益子订单号

	@Version
	private Long version;

	public enum Status {
		初始, 未兑换, 已兑换
	}

	public enum Type {
		影视, 门票
	}

	public enum Consume {
		无, 分享, 兑换电影票
	}

	public enum OrderPayStatus{
		UNPAID,PAID,FAILED,OVERDUE
	}
	
	public enum Agency{
		HUO,ZHONGYING
	}

	public String getFilmNo() {
		return filmNo;
	}

	public void setFilmNo(String filmNo) {
		this.filmNo = filmNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getPerformTime() {
		return performTime;
	}

	public void setPerformTime(Date performTime) {
		this.performTime = performTime;
	}

	public String getCinemaNo() {
		return cinemaNo;
	}

	public void setCinemaNo(String cinemaNo) {
		this.cinemaNo = cinemaNo;
	}

	public String getPerformAddress() {
		return performAddress;
	}

	public void setPerformAddress(String performAddress) {
		this.performAddress = performAddress;
	}

	public String getPerformAddressDetail() {
		return performAddressDetail;
	}

	public void setPerformAddressDetail(String performAddressDetail) {
		this.performAddressDetail = performAddressDetail;
	}

	public String getSceneNo() {
		return sceneNo;
	}

	public void setSceneNo(String sceneNo) {
		this.sceneNo = sceneNo;
	}

	public String getScene() {
		return scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = seat;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUnionId() {
		return unionId;
	}

	public void setUnionId(Long unionId) {
		this.unionId = unionId;
	}

	public Long getRefOrder() {
		return refOrder;
	}

	public void setRefOrder(Long refOrder) {
		this.refOrder = refOrder;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Date timeOut) {
		this.timeOut = timeOut;
	}

	public String getConsume() {
		return consume;
	}

	public void setConsume(String consume) {
		this.consume = consume;
	}

	public Long getCouponId() {
		return couponId;
	}

	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}

	public String getTicketCode() {
		return ticketCode;
	}

	public void setTicketCode(String ticketCode) {
		this.ticketCode = ticketCode;
	}

	public String getTicketCaptcha() {
		return ticketCaptcha;
	}

	public void setTicketCaptcha(String ticketCaptcha) {
		this.ticketCaptcha = ticketCaptcha;
	}

	public String getTicketCodeFront() {
		return ticketCodeFront;
	}

	public void setTicketCodeFront(String ticketCodeFront) {
		this.ticketCodeFront = ticketCodeFront;
	}

	public Date getOrderCreatedDate() {
		return orderCreatedDate;
	}

	public void setOrderCreatedDate(Date orderCreatedDate) {
		this.orderCreatedDate = orderCreatedDate;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public Date getOrderTimeout() {
		return orderTimeout;
	}

	public void setOrderTimeout(Date orderTimeout) {
		this.orderTimeout = orderTimeout;
	}

	public String getOrderPayStatus() {
		return orderPayStatus;
	}

	public void setOrderPayStatus(String orderPayStatus) {
		this.orderPayStatus = orderPayStatus;
	}

	public String getPrintMode() {
		return printMode;
	}

	public void setPrintMode(String printMode) {
		this.printMode = printMode;
	}

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public String getVoucherCode() {
		return voucherCode;
	}

	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}

	public String getPrintCode() {
		return printCode;
	}

	public void setPrintCode(String printCode) {
		this.printCode = printCode;
	}

	public String getSmsInfo() {
		return smsInfo;
	}

	public void setSmsInfo(String smsInfo) {
		this.smsInfo = smsInfo;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Order getSubOrder() {
		return subOrder;
	}

	public void setSubOrder(Order subOrder) {
		this.subOrder = subOrder;
	}

	@Transient
	public boolean hasGift(){
		if(this.getSubOrder()!=null && this.getSubOrder().getUserGift()!=null){
			return true;
		}
		return false;
	}
}
