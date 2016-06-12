package com.snowstore.diana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.snowstore.diana.domain.Dictionary;

@Repository
public interface DictionaryDao
		extends PagingAndSortingRepository<Dictionary, Long>, JpaSpecificationExecutor<Dictionary> {
	List<Dictionary> findByGroupName(String groupName);

	List<Dictionary> findByGroupCode(String groupCode);

	Dictionary findOneByItemCode(String itemCode);
}
