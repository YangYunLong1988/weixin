package com.snowstore.diana.domain;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 代金券c009批次
 * 
 * @author stone
 */
@Entity
@Table(name = "diana_c009")
@EntityListeners(AuditingEntityListener.class)
public class C009Card extends AbstractEntity {
	private static final long serialVersionUID = -3455051584741607980L;
	private String codeDiana;
	private String code3th;

	public String getCodeDiana() {
		return codeDiana;
	}

	public String getCode3th() {
		return code3th;
	}

	public void setCodeDiana(String codeDiana) {
		this.codeDiana = codeDiana;
	}

	public void setCode3th(String code3th) {
		this.code3th = code3th;
	}
}
