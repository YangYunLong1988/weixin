package com.snowstore.diana.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.BestcakeCard;

public interface BestcakeCardDao extends PagingAndSortingRepository<BestcakeCard, Long>, JpaSpecificationExecutor<BestcakeCard> {
	
	public List<BestcakeCard> findByIsBind(Boolean isBind,Pageable page);
}
