package com.snowstore.diana.vo;

/**
 * 电影兑换码
 * 
 * @author stone
 *
 */
public class MovieCodeVo {
	private long id;// ID
	private String code;// 编码
	private boolean isUsed;// 是否已使用
	private boolean isFromShared;// 是否分享所得
	private long timeout;// 过期时间，单位毫秒(例如将2015-12-31转化成毫秒值)
	private String memo;// 状态
	private MovieTicketVo ticket;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	public boolean isFromShared() {
		return isFromShared;
	}

	public void setFromShared(boolean isFromShared) {
		this.isFromShared = isFromShared;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public MovieTicketVo getTicket() {
		return ticket;
	}

	public void setTicket(MovieTicketVo ticket) {
		this.ticket = ticket;
	}
}
