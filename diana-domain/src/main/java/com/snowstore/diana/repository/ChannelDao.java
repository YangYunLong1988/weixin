package com.snowstore.diana.repository;

import com.snowstore.diana.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by wulinjie on 2015/12/31.
 */
public interface ChannelDao extends JpaRepository<Channel, Long>,PagingAndSortingRepository<Channel, Long>, JpaSpecificationExecutor<Channel> {

	/**
	 * 根据渠道代码，查询渠道信息
	 * @author wulinjie
	 * @param code	渠道代码
	 * @return	渠道信息
	 */
	public Channel findByCode(String code);

	/**
	 * 根据查询路径，查询渠道信息
	 * @param path
	 * @return
	 */
	public Channel findByPath(String path);

	/**
	 * 根据上级渠道，查询所有子渠道
	 * @author wulinjie
	 * @param parentId 上级渠道ID
	 * @return 子渠道列表
	 */
	public List<Channel> findByParentId(Long parentId);

	@Query("from Channel L where L.parentId is null")
	public List<Channel> findAllTopChannel();

	/**
	 * 获取最大的渠道编号
	 * @authur wulinjie
	 * @return
	 */
	public Channel findFirstByOrderByPlatformNoDesc();
}
