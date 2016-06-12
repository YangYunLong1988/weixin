package com.snowstore.diana.vo.logistics;

import java.io.Serializable;

public class Response implements Serializable {
	private static final long serialVersionUID = 1L;
	private Boolean success = Boolean.TRUE;
	private String reason;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		if (success) {
			return "<Response><success>true</success></Response>";
		}
		return "<Response><success>false</success><reason>" + reason + "</reason></Response>";
	}
}
