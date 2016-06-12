package com.snowstore.diana.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.Logistics;

public interface LogisticsDao extends PagingAndSortingRepository<Logistics, Long>, JpaSpecificationExecutor<Logistics> {

	Logistics findOneByOrderId(Long valueOf);

}
