package com.snowstore.diana.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Created by wulinjie on 2015/12/28.
 */
@Entity
@Table(name = "diana_channel")
@EntityListeners(AuditingEntityListener.class)
public class Channel extends AbstractEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	private String name;		//渠道名称
	private String code;		//渠道代码
	private String path;	 	//节点路径（JPA递归查询用）
	private Long parentId;		//父渠道ID
	private Long userId;		//用户ID
	private Long platformNo;	//平台编号
	private String secretKey;	//渠道接入密钥

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getPlatformNo() {
		return platformNo;
	}

	public void setPlatformNo(Long platformNo) {
		this.platformNo = platformNo;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
}
