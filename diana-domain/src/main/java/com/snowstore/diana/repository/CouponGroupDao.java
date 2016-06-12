package com.snowstore.diana.repository;


import com.snowstore.diana.domain.CouponGroup;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wulinjie on 2015/11/12.
 */
@Repository
public interface CouponGroupDao extends PagingAndSortingRepository<CouponGroup, Long>, JpaSpecificationExecutor<CouponGroup> {

	/**
	 * 根据加密密钥查询可用的卡券组
	 * @author wulinjie
	 * @param secretKey	加密密钥
	 * @param userId 		用户ID
	 * @return
	 */
	@Query("  from CouponGroup M 													  \n" +
			" where M.secretKey = ?1 												  \n" +
			" and M.id = ( 															  \n" +
			"    select min(P.id) from CouponGroup P 								  \n" +
			"    where exists (  													  \n" +
			"      select 1 from Coupon L 											  \n" +
			"      where L.groupId = P.id  											  \n" +
			"      and  L.isBind = 0 		  										  \n" +
			"   )  																	  \n" +
			" ) 																	  \n")
	public CouponGroup findAvaliableGroupBySecretKey(String secretKey);

}
