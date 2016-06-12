package com.snowstore.diana.domain;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author Administratior
 *
 */
@Entity
@Table(name = "diana_user_gift")
@EntityListeners(AuditingEntityListener.class)
public class UserGift extends AbstractEntity {

	private static final long serialVersionUID = 2331392710044069217L;

	private Long userId;// 用户id
	/**
	 * 用户唯一ID
	 */
	private Long unionId;
	private String refGift;// 礼品 config.js
	private Long refPackage;// 配送信息id
	private String giftName;// 礼品名称
	private String spuCode;// 商品条码,物流对接需要
	private String status;// 用户卡卷兑换状态
	@OneToOne
	private Order order;

	private String bestCakeCode;

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

	public String getRefGift() {
		return refGift;
	}

	public void setRefGift(String refGift) {
		this.refGift = refGift;
	}

	public Long getRefPackage() {
		return refPackage;
	}

	public void setRefPackage(Long refPackage) {
		this.refPackage = refPackage;
	}

	public String getGiftName() {
		return giftName;
	}

	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}

	public String getSpuCode() {
		return spuCode;
	}

	public void setSpuCode(String spuCode) {
		this.spuCode = spuCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getBestCakeCode() {
		return bestCakeCode;
	}

	public void setBestCakeCode(String bestCakeCode) {
		this.bestCakeCode = bestCakeCode;
	}

	public enum Status {
		未分配, 已分配
	}
}
