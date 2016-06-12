package com.snowstore.diana.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.Concert;

public interface ConcertDao extends PagingAndSortingRepository<Concert, Long>, JpaSpecificationExecutor<Concert> {
	/**
	 * 根据名称查找电影票方案，根据设计，名称是唯一的
	 * 
	 * @param name
	 * @return
	 */
	public Concert findByName(String name);

}