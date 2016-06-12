package com.snowstore.diana.vo;

import java.io.Serializable;

/**
 * 电影票详情
 * 
 * @author stone
 *
 */
public class MovieTicketVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String movie;// 电影
	private String cinema;// 影院
	private String scene;// 影厅
	private String seat;// 座位
	private String time;// 场次时间
	private boolean isChecked;// 是否出票
	private boolean isSuccess;// 出票是否成功
	private String ticketMachine;// 取票机
	private String ticketCode;// 取票码
	private String ticketCaptcha;// 验证码
	private String ticketCodeFront;// 前提取票码
	private String type;// 类型, 如 3D,中文
	private String ticket3rd;// 第3方选座平台
	private String ticket3rdCode;// 第3方选座平台码
	private String ticketExtractInfo;//取票信息
	private String performAddressDetail;
	private String smsInfo;

	public String getMovie() {
		return movie;
	}

	public void setMovie(String movie) {
		this.movie = movie;
	}

	public String getCinema() {
		return cinema;
	}

	public void setCinema(String cinema) {
		this.cinema = cinema;
	}

	public String getScene() {
		return scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = seat;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTicketMachine() {
		return ticketMachine;
	}

	public void setTicketMachine(String ticketMachine) {
		this.ticketMachine = ticketMachine;
	}

	public String getTicketCode() {
		return ticketCode;
	}

	public void setTicketCode(String ticketCode) {
		this.ticketCode = ticketCode;
	}

	public String getTicketCaptcha() {
		return ticketCaptcha;
	}

	public void setTicketCaptcha(String ticketCaptcha) {
		this.ticketCaptcha = ticketCaptcha;
	}

	public String getTicketCodeFront() {
		return ticketCodeFront;
	}

	public void setTicketCodeFront(String ticketCodeFront) {
		this.ticketCodeFront = ticketCodeFront;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTicket3rd() {
		return ticket3rd;
	}

	public void setTicket3rd(String ticket3rd) {
		this.ticket3rd = ticket3rd;
	}

	public String getTicket3rdCode() {
		return ticket3rdCode;
	}

	public void setTicket3rdCode(String ticket3rdCode) {
		this.ticket3rdCode = ticket3rdCode;
	}

	public String getTicketExtractInfo() {
		return ticketExtractInfo;
	}

	public void setTicketExtractInfo(String ticketExtractInfo) {
		this.ticketExtractInfo = ticketExtractInfo;
	}

	public String getPerformAddressDetail() {
		return performAddressDetail;
	}

	public void setPerformAddressDetail(String performAddressDetail) {
		this.performAddressDetail = performAddressDetail;
	}

	public String getSmsInfo() {
		return smsInfo;
	}

	public void setSmsInfo(String smsInfo) {
		this.smsInfo = smsInfo;
	}
}
