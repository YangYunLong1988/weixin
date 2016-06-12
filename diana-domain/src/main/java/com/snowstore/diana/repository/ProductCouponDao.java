package com.snowstore.diana.repository;

import com.snowstore.diana.domain.ProductCoupon;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/11/12.
 */
@Repository
public interface ProductCouponDao extends PagingAndSortingRepository<ProductCoupon, Long>, JpaSpecificationExecutor<ProductCoupon> {

	/**
	 * 根据产品ID，获取secretKey
	 * @author wulinjie
	 * @param productId	产品ID
	 * @return secretKey
	 */
	public ProductCoupon findByProductId(Long productId);

}
