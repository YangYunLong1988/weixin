package com.snowstore.diana.vo;

import java.io.Serializable;

public class ImgVo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3496040393534710850L;
	private String name;
	private String type;
	private byte[] content;
	private String suffix;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
}
