package com.snowstore.diana.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.PayInfo;

public interface PayInfoDao extends PagingAndSortingRepository<PayInfo, Long>, JpaSpecificationExecutor<PayInfo> {
	
	/**根据易联订单编号查询支付结果
	 * @param referenceOrder
	 * @return
	 */
	public PayInfo findByReferenceOrder(String referenceOrder);
	
	public PayInfo findByOrderId(Long ordrId);
	
	public PayInfo findByTradeNo(String tradeNo);
}