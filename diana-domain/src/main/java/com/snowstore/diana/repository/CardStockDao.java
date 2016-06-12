package com.snowstore.diana.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.CardStock;

public interface CardStockDao extends PagingAndSortingRepository<CardStock, Long>, JpaSpecificationExecutor<CardStock> {
	@Query
	public CardStock findByExchangeCode(String exchangeCode);

	@Query
	public List<CardStock> findByUserId(Long userId);

	@Query
	public List<CardStock> findByUserIdAndMemoAndCreatedDateBetween(Long userId, String memo, Date start, Date end);

	@Query
	public List<CardStock> findByUserIdAndStatusAndOrderIdIsNull(Long userId, String status);

	@Query
	public List<CardStock> findByOrderIdAndStatus(Long orderId, String status);

	public List<CardStock> findByOrderId(Long orderId);
	
	public List<CardStock> findByUnionIdAndExchangeCodeLike(Long unionId,String exchangeCode);
}
