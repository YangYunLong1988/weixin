package com.snowstore.diana.vo.logistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "UpdateInfos")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateInfos implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "UpdateInfo")
	private List<UpdateInfo> UpdateInfos = new ArrayList<UpdateInfo>();

	public List<UpdateInfo> getUpdateInfos() {
		return UpdateInfos;
	}

	public void setUpdateInfos(List<UpdateInfo> updateInfos) {
		UpdateInfos = updateInfos;
	}

}
