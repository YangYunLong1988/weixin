package com.snowstore.diana.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.C009Card;

public interface C009CardDao extends PagingAndSortingRepository<C009Card, Long>, JpaSpecificationExecutor<C009Card> {
	public C009Card findByCodeDiana(String code);
}
