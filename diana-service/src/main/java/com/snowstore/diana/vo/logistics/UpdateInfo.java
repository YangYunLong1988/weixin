package com.snowstore.diana.vo.logistics;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String MemberNO;
	private String SaleOrderCode;
	private String CarryCode;
	private String InfoType;
	private String InfoContent;
	private String ExpressCode;
	private String Remark;
	private String Weight;
	private String WareHouseCode;
	private String TrackDate;
	private String ArriveTime;

	public String getMemberNO() {
		return MemberNO;
	}

	public void setMemberNO(String memberNO) {
		MemberNO = memberNO;
	}

	public String getSaleOrderCode() {
		return SaleOrderCode;
	}

	public void setSaleOrderCode(String saleOrderCode) {
		SaleOrderCode = saleOrderCode;
	}

	public String getCarryCode() {
		return CarryCode;
	}

	public void setCarryCode(String carryCode) {
		CarryCode = carryCode;
	}

	public String getInfoType() {
		return InfoType;
	}

	public void setInfoType(String infoType) {
		InfoType = infoType;
	}

	public String getInfoContent() {
		return InfoContent;
	}

	public void setInfoContent(String infoContent) {
		InfoContent = infoContent;
	}

	public String getExpressCode() {
		return ExpressCode;
	}

	public void setExpressCode(String expressCode) {
		ExpressCode = expressCode;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public String getWeight() {
		return Weight;
	}

	public void setWeight(String weight) {
		Weight = weight;
	}

	public String getWareHouseCode() {
		return WareHouseCode;
	}

	public void setWareHouseCode(String wareHouseCode) {
		WareHouseCode = wareHouseCode;
	}

	public String getTrackDate() {
		return TrackDate;
	}

	public void setTrackDate(String trackDate) {
		TrackDate = trackDate;
	}

	public String getArriveTime() {
		return ArriveTime;
	}

	public void setArriveTime(String arriveTime) {
		ArriveTime = arriveTime;
	}

}
