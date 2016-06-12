package com.snowstore.diana.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.snowstore.diana.domain.Coupon;

/**
 * Created by wulinjie on 2015/11/12.
 */
@Repository
public interface CouponDao extends PagingAndSortingRepository<Coupon, Long>, JpaSpecificationExecutor<Coupon> {

	/**
	 * 查询可用卡券
	 * @author wulinjie
	 * @param groupId	卡券组ID
	 * @return	卡券列表
	 */
	@Query("from Coupon M where M.id = (select min(P.id) from Coupon P where P.isBind = 0 and P.isUsed=0 )")
	public Coupon findUnusedCoupon();

	/**
	 * 根据卡券CODE，查询卡券详细信息
	 * @author wulinjie
	 * @param code 卡券code
	 * @return
	 */
	public Coupon findByCode(String code);

	@Query("select count(M.id) from Coupon  M where M.isBind = 0 and M.isUsed=0 ")
	public Long countAvaliableCoupon();

}
