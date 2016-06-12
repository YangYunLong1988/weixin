package com.snowstore.diana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.snowstore.diana.domain.Settings;

/**
 * Created by wulinjie on 2016/3/2.
 */
@Repository
public interface SettingsDao extends JpaRepository<Settings, Long>, PagingAndSortingRepository<Settings, Long>, JpaSpecificationExecutor<Settings> {

	@Query("select key from Settings")
	public List<String> findKeys();

	public Settings findByKey(String key);

}
