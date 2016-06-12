package com.snowstore.diana.domain;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "diana_product")
@EntityListeners(AuditingEntityListener.class)
public class Product extends AbstractEntity {

	private static final long serialVersionUID = -2681137403483561282L;

	private Long referenceProduct;

	private String originalName;

	private String name; // 产品名称
	
	private Integer giftNum; // 礼品数量

	private BigDecimal rate; // 产品利率

	private BigDecimal amount; // 金额

	private BigDecimal availableAmount;// 剩余可投 金额

	private Integer period;// 期限

	private String periodType;// 期限类型（月，日）

	private BigDecimal minPurchaseAmount;// 起购金额

	private Date beginCountInterest;// 起息日期

	private Date endCountInterest;// 到期日期

	private Date raiseEndTime;// 募集截止日期 起息日前一日 21点之前

	private String guaranteeAgency;// 担保机构

	private String repaymentMode;// 偿还方式 到期一次性 付息还本 ，月还利息 到期付息还本

	@Column(length = 512)
	private String institutionInstruction;// 机构介绍

	private BigDecimal labelVolmoney;// 成交金额

	private Long certificate;// 对应证书

	private Status status; // 状态

	private String riskTip;// 风险提示

	private String type; // 产品类型

	private Long sortNum = 0L;// 排序

	private Long refConcert;// 演唱会

	private BigDecimal price = BigDecimal.ZERO; // 每份价格

	private String posterImgName; // 海报文件名
	@JsonIgnore
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] posterImg; // 海报

	private String titleImgName; // 封面文件名
	@JsonIgnore
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] titleImg; // 封面

	@Length(max = 200)
	private String url; // 链接地址

	private Integer exchangeAmount; // 兑票数量

	public enum Status {
		初始, 在售, 下架, 售罄
	}

	public enum Type {
		壹票玩电影,百元玩电影,多张组合,单张贵族,壹票众筹, 电影转礼品,大轰炸
	}

	public enum ImageType {
		封面, 海报
	}

	public Long getReferenceProduct() {
		return referenceProduct;
	}

	public void setReferenceProduct(Long referenceProduct) {
		this.referenceProduct = referenceProduct;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGiftNum() {
		return giftNum;
	}

	public void setGiftNum(Integer giftNum) {
		this.giftNum = giftNum;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	public BigDecimal getMinPurchaseAmount() {
		return minPurchaseAmount;
	}

	public void setMinPurchaseAmount(BigDecimal minPurchaseAmount) {
		this.minPurchaseAmount = minPurchaseAmount;
	}

	public Date getBeginCountInterest() {
		return beginCountInterest;
	}

	public void setBeginCountInterest(Date beginCountInterest) {
		this.beginCountInterest = beginCountInterest;
	}

	public Date getEndCountInterest() {
		return endCountInterest;
	}

	public void setEndCountInterest(Date endCountInterest) {
		this.endCountInterest = endCountInterest;
	}

	public Date getRaiseEndTime() {
		return raiseEndTime;
	}

	public void setRaiseEndTime(Date raiseEndTime) {
		this.raiseEndTime = raiseEndTime;
	}

	public String getGuaranteeAgency() {
		return guaranteeAgency;
	}

	public void setGuaranteeAgency(String guaranteeAgency) {
		this.guaranteeAgency = guaranteeAgency;
	}

	public String getRepaymentMode() {
		return repaymentMode;
	}

	public void setRepaymentMode(String repaymentMode) {
		this.repaymentMode = repaymentMode;
	}

	public String getInstitutionInstruction() {
		return institutionInstruction;
	}

	public void setInstitutionInstruction(String institutionInstruction) {
		this.institutionInstruction = institutionInstruction;
	}

	public BigDecimal getLabelVolmoney() {
		return labelVolmoney;
	}

	public void setLabelVolmoney(BigDecimal labelVolmoney) {
		this.labelVolmoney = labelVolmoney;
	}

	public Long getCertificate() {
		return certificate;
	}

	public void setCertificate(Long certificate) {
		this.certificate = certificate;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getRiskTip() {
		return riskTip;
	}

	public void setRiskTip(String riskTip) {
		this.riskTip = riskTip;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSortNum() {
		return sortNum;
	}

	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
	}

	public byte[] getPosterImg() {
		return posterImg;
	}

	public void setPosterImg(byte[] posterImg) {
		this.posterImg = posterImg;
	}

	public byte[] getTitleImg() {
		return titleImg;
	}

	public void setTitleImg(byte[] titleImg) {
		this.titleImg = titleImg;
	}

	public Long getRefConcert() {
		return refConcert;
	}

	public void setRefConcert(Long refConcert) {
		this.refConcert = refConcert;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getExchangeAmount() {
		return exchangeAmount;
	}

	public void setExchangeAmount(Integer exchangeAmount) {
		this.exchangeAmount = exchangeAmount;
	}

	public String getPosterImgName() {
		return posterImgName;
	}

	public void setPosterImgName(String posterImgName) {
		this.posterImgName = posterImgName;
	}

	public String getTitleImgName() {
		return titleImgName;
	}

	public void setTitleImgName(String titleImgName) {
		this.titleImgName = titleImgName;
	}	
	
	@Transient
	public boolean getIsAvailable() {
		if(Status.初始.equals(this.status)||Status.下架.equals(this.status)||Status.售罄.equals(this.status)){
			return false;
		}
		Calendar calendar = Calendar.getInstance();
		if(this.availableAmount.compareTo(this.minPurchaseAmount) < 0){
			return false;
		}else if(calendar.getTime().after(this.beginCountInterest)){
			return false;
		}else if(calendar.getTime().after(this.raiseEndTime)){
			return false;
		}else{
			return true;
		}
	}
}
