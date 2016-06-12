package com.snowstore.diana.repository;

import java.util.List;

import com.snowstore.diana.domain.ChannelProduct;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by wulinjie on 2015/12/31.
 */
public interface ChannelProductDao extends PagingAndSortingRepository<ChannelProduct, Long>, JpaSpecificationExecutor<ChannelProduct> {
	
	public List<ChannelProduct> findByChannelId(Long channelId);

	public ChannelProduct findByProductTypeAndChannelId(String productType, Long channelId);

}
