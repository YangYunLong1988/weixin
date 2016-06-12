package com.snowstore.diana.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snowstore.diana.domain.PayInfo;
import com.snowstore.diana.repository.PayInfoDao;

@Service
public class PayInfoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PayInfoService.class);

	@Autowired
	private PayInfoDao payInfoDao;
	
	/**根据易联订单编号查询支付信息
	 * @param referenceOrder
	 * @return
	 */
	public PayInfo getByReferenceOrder(String referenceOrder){
		LOGGER.info("查询支付信息");
		return payInfoDao.findByReferenceOrder(referenceOrder);
	}
	
	public PayInfo saveOrUpdate(PayInfo payInfo){
		return payInfoDao.save(payInfo);
	}
	
	/**根据微信交易号查询支付信息
	 * @param referenceOrder
	 * @return
	 */
	public PayInfo getByTradeNo(String tradeNo){
		LOGGER.info("查询支付信息");
		return payInfoDao.findByTradeNo(tradeNo);
	}
	
	/**根据订单查询支付信息
	 * @param orderId
	 * @return
	 */
	public PayInfo getByOrderId(Long orderId){
		LOGGER.info("查询支付信息");
		return payInfoDao.findByOrderId(orderId);
	}
}
