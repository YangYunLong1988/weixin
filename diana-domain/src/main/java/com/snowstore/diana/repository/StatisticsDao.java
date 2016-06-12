
package com.snowstore.diana.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.Statistics;

public interface StatisticsDao extends PagingAndSortingRepository<Statistics, Long>, JpaSpecificationExecutor<Statistics> {

	/**根据日期查找统计信息
	 * @author 王腾飞
	 * @param date
	 * @return
	 */
	public Statistics findByDateStatistics(Date date);
	
	/**
	 * 根据日期和用户平台，查询统计信息
	 * @author 吴林杰
	 * @param date		统计日期
	 * @param platform	用户平台
	 * @return
	 */
	public Statistics findByDateStatisticsAndPlatform(Date date,String platform);
	
	/**
	 * @author 吴林杰
	 * @param date
	 * @return
	 */
	public Long countByDateStatistics(Date date);
	
	/**
	 * 根据日期和平台，统计数据
	 * @author wulinjie
	 * @param date
	 * @param platform
	 * @return
	 */
	public Long countByDateStatisticsAndPlatform(Date date,String platform);
}
