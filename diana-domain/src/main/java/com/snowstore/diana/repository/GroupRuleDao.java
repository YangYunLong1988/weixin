package com.snowstore.diana.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.GroupRule;

public interface GroupRuleDao extends PagingAndSortingRepository<GroupRule, Long>, JpaSpecificationExecutor<GroupRule> {

	@Query("select max(price) from GroupRule")
	public BigDecimal getMaxPrice();
}
