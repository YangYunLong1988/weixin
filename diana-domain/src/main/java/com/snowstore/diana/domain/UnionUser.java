package com.snowstore.diana.domain;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 唯一用户表（整合同一手机用户）
 * @author XieZG
 * @Date:2016年1月15日下午1:23:37
 */
@Entity
@Table(name = "diana_union_user", uniqueConstraints = @UniqueConstraint(columnNames = { "mobile" }))
@EntityListeners(AuditingEntityListener.class)
public class UnionUser extends AbstractEntity {
	/**
	 * @author XieZG
	 * @Date:2016年1月15日下午1:23:28 
	 */
	private static final long serialVersionUID = -9063364489762143972L;
	private String mobile;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
}
