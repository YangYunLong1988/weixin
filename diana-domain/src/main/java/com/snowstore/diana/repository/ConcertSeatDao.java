package com.snowstore.diana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.ConcertSeat;

public interface ConcertSeatDao extends PagingAndSortingRepository<ConcertSeat, Long>, JpaSpecificationExecutor<ConcertSeatDao> {
	public List<ConcertSeat> findByRefConcert(Long refConcert);
	/**
	 * 获取区间数量
	 * @param refConcert
	 * @return
	 */
	public Long countByRefConcert(Long refConcert);
	
	public List<ConcertSeat> findByRefConcertOrderByCreatedDateAsc(Long refConcert);
}