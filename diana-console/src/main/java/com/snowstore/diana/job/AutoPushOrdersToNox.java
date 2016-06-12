package com.snowstore.diana.job;

import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Order.NoxNotify;
import com.snowstore.diana.domain.Order.Status;
import com.snowstore.diana.domain.Product.Type;
import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.OrderService;
import com.snowstore.diana.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class AutoPushOrdersToNox {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrderService orderService;
	@Autowired
	private SettingsService settingsService;

	/**
	 * 推送订单信息到分销系统
	 */
	@Scheduled(cron = "0 0/5 * * * ?")
	public void pushOrdersToNox(){
		LOGGER.info("开始同步订单信息到分销系统");
		Settings settings = settingsService.getByKey("pushOrderToNox");
		if(null!=settings && "on".equals(settings.getValue())){
			List<Order> orderList = orderService.findTop100ByNoxNotify(NoxNotify.WAIT, Status.已付款, Type.电影转礼品);
			orderService.pushOrdersToNox(orderList);
		}else{
			LOGGER.info("任务未开启");
		}
		LOGGER.info("订单信息同步结束");
	}

}
