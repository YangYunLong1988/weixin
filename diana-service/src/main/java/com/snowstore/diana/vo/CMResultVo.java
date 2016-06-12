package com.snowstore.diana.vo;

public class CMResultVo {
	/**
	 * 0表示全部有效，-1标识无效
	 */
	private String status;
	/**
	 * 结果文字描述
	 */
	private String respDesc;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRespDesc() {
		return respDesc;
	}

	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
}
