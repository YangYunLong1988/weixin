package com.snowstore.diana.service;

import com.snowstore.diana.domain.ProductCoupon;
import com.snowstore.diana.repository.ProductCouponDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wulinjie on 2015/11/12.
 */
@Service
@Transactional
public class ProductCouponService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductCouponService.class);

	@Autowired
	private ProductCouponDao productCouponDao;

	/**
	 * 根据产品ID，获取secretKey
	 * @author wulinjie
	 * @param productId
	 * @return
	 */
	public String getSecretKeyByProduct(Long productId){
		LOGGER.info("根据productId获取secretKey[productId：" + productId + "]");
		String secretKey = "";
		ProductCoupon productCoupon = productCouponDao.findByProductId(productId);
		if(productCoupon!=null){
			secretKey = productCoupon.getSecretKey();
		}
		LOGGER.info("获取secretKey结束[secretKey：" + secretKey + "]");
		return secretKey;
	}

}
