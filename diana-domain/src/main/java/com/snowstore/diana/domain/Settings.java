package com.snowstore.diana.domain;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 系统设置
 * Created by wulinjie on 2016/3/2.
 */
@Entity
@Table(name = "diana_settings")
@EntityListeners(AuditingEntityListener.class)
public class Settings extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = -3392996603374825672L;

	private String title;
	private String key;
	private String value;
	private String type;
	private String description;

	public enum Type{
		CORE
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
