package com.snowstore.diana.job;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.snowstore.diana.domain.Order;
import com.snowstore.diana.service.OrderService;
import com.snowstore.poseidon.client.JopClient;
import com.snowstore.poseidon.client.exception.BusinessException;
import com.snowstore.poseidon.client.utils.JsonUtil;
import com.snowstore.poseidon.client.vo.request.OrderOfflinepayReq;
import com.snowstore.poseidon.client.vo.request.OrderPay;
import com.snowstore.poseidon.client.vo.response.OrderOfflinepayResp;
/**
 * 使用开放平台 1.3.1版本
 * 票务系统支付消息推送Job
 * @author: fuhongxing
 * @date:   2015年10月27日
 * @version 1.0.0
 */
@Service
public class AutoPushMessageToOpenform {
	@Autowired
	private JopClient jopClient;
	@Autowired
	private OrderService orderService;
	@Autowired
	private SettingsService settingsService;

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	/**
	 * 票务系统支付成功，通知开放平台（15分钟执行1次）
	 */
	@Scheduled(cron="0 0/15 * * * ?")
	public void notifyOpenPlatform() {
		//检查系统设置
		Settings settings = settingsService.getByKey("autoPushMessageToOpenform");
		if(null!=settings && "off".equals(settings.getValue())){
			return;
		}

		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.HOUR_OF_DAY)<2){
			return;
		}
		LOGGER.info("==========支付成功通知开放平台==========");
		List<Long> orderList = orderService.findByNotify(Order.Notify.WAIT.name(),Order.Status.已付款.name());
		//ps: 微信支付 没有银行卡信息 
		List<OrderPay> payList = null;
		OrderPay orderPay = new OrderPay();
		orderPay.setBankAccount("2222222288888888");
		orderPay.setAccountName("票务系统");
		orderPay.setBankCode("00080002");
		orderPay.setSubbranchProvince("上海");
		orderPay.setSubbranchCity("上海");
		orderPay.setBankSubbranch("长阳支行");
		OrderOfflinepayReq req = new OrderOfflinepayReq();
		for(Long id : orderList){
			Order order = orderService.get(id);
			payList = new ArrayList<OrderPay>();
			orderPay.setOrderCode(order.getReferenceOrder());
			payList.add(orderPay);
			req.setPayList(payList);
			try {
				OrderOfflinepayResp resp = jopClient.req(req);
				if(resp!=null){
					LOGGER.info("支付消息推送成功："+JsonUtil.objectToJson(resp));
					order.setNotify(Order.Notify.SUCCESS.name());
					orderService.add(order);
				}
			} catch (BusinessException e) {
				order.setNotify(Order.Notify.FAILED.name());
				orderService.add(order);
				LOGGER.error("支付成功推送消息到开发平台异常。订单ID:"+order.getReferenceOrder(),e);
			}
		}
	}
	
	
}
