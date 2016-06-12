package com.snowstore.diana.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "diana_order")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value={"userGift","logistics","giftTickets"})
public class Order extends AbstractEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3392996603374825672L;
	private Long productId;// 理财产品Id
	private String productName;// 理财产品
	private BigDecimal amount;// 订单金额
	private String referenceOrder;// 对应易联-->订单编号
	private String type;// 订单类型 取值 产品类型
	private String status;// 状态
	private Integer ticketNum;
	private Integer gitfNum;
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "order")
	private Logistics logistics;
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "order")
	private UserGift userGift;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "userId")
	private User user;
	/**
	 * 用户唯一ID
	 */
	private Long unionId;
	private Date performTime;// 演出时间

	private String notify = Order.Notify.WAIT.name(); // 支付成功 通知开放平台标识
	private String dataType;

	private String noxNotify;	//同步到分销平台标识

	@Version
	private Long version;

	@OneToOne(fetch=FetchType.LAZY, mappedBy="subOrder")
	private Tickets giftTickets;

	public enum Status {
		待付款, 已付款, 已撤单,付款中,付款失败
	}

	public enum Type {
		影视, 门票
	}

	public enum Notify {
		WAIT, SUCCESS,FAILED
	}

	public enum NoxNotify{
		WAIT, SUCCESS
	}

	public enum DataType {
		FORTURN, POSEIDON, OFFLINE
	}

	public Long getUnionId() {
		return unionId;
	}

	public void setUnionId(Long unionId) {
		this.unionId = unionId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getReferenceOrder() {
		return referenceOrder;
	}

	public void setReferenceOrder(String referenceOrder) {
		this.referenceOrder = referenceOrder;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getTicketNum() {
		return ticketNum;
	}

	public void setTicketNum(Integer ticketNum) {
		this.ticketNum = ticketNum;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Date getPerformTime() {
		return performTime;
	}

	public void setPerformTime(Date performTime) {
		this.performTime = performTime;
	}

	public String getNotify() {
		return notify;
	}

	public void setNotify(String notify) {
		this.notify = notify;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getGitfNum() {
		return gitfNum;
	}

	public void setGitfNum(Integer gitfNum) {
		this.gitfNum = gitfNum;
	}

	public Logistics getLogistics() {
		return logistics;
	}

	public void setLogistics(Logistics logistics) {
		this.logistics = logistics;
	}

	public UserGift getUserGift() {
		return userGift;
	}

	public void setUserGift(UserGift userGift) {
		this.userGift = userGift;
	}

	public String getNoxNotify() {
		return noxNotify;
	}

	public void setNoxNotify(String noxNotify) {
		this.noxNotify = noxNotify;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Tickets getGiftTickets() {
		return giftTickets;
	}

	public void setGiftTickets(Tickets giftTickets) {
		this.giftTickets = giftTickets;
	}
}
