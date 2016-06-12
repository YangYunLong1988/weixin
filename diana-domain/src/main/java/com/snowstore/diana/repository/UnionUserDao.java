package com.snowstore.diana.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.UnionUser;

public interface UnionUserDao extends PagingAndSortingRepository<UnionUser, Long>, JpaSpecificationExecutor<UnionUser> {
	@Query
	public UnionUser findByMobile(String mobile);
}
