package com.snowstore.diana.vo;

import java.util.ArrayList;
import java.util.List;

import com.snowstore.diana.domain.Concert;
import com.snowstore.diana.domain.ConcertSeat;

public class ConcertVo {
	private Concert concert;
	private List<ConcertSeat> seats = new ArrayList<ConcertSeat>();
	private Long concertId;

	public Concert getConcert() {
		return concert;
	}

	public void setConcert(Concert concert) {
		this.concert = concert;
	}

	public List<ConcertSeat> getSeats() {
		return seats;
	}

	public void setSeats(List<ConcertSeat> seats) {
		this.seats = seats;
	}

	public Long getConcertId() {
		return concertId;
	}

	public void setConcertId(Long concertId) {
		this.concertId = concertId;
	}
}
