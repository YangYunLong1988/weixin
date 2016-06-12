package com.snowstore.diana.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.Order;
import com.snowstore.diana.service.OrderService;
import com.snowstore.diana.utils.DateUtils;

/**
 * 撤销客户订单job  每15分钟执行一次
 * @author: fuhongxing
 * @date:   2015年10月27日
 * @version 1.0.0
 */
@Service
@Transactional
public class AutoCancelOrderJob {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Autowired
	private OrderService orderService;
	@Autowired
	private SettingsService settingsService;

	/**
	 * 撤单  2分钟执行一次
	 * v1.3.1 使用开放平台 1.3.1版本
	 */
	@Scheduled(fixedRate=120000)
	public void cannelOverTimeOrders(){
		//检查系统设置
		Settings settings = settingsService.getByKey("autoCancelOrder");
		if(null!=settings && "off".equals(settings.getValue())){
			return;
		}

		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.HOUR_OF_DAY)<2){
			return;
		}
		LOGGER.info("=========15分钟未支付的订单执行撤单任务==========");
		List<Order> orderList = orderService.findByStatus(Order.Status.待付款.name());
		Long date = null;
		for (Order order : orderList) {
			date = new Date().getTime();
			//判断是否超过半小时未支付
			if(date > (DateUtils.addMinute(order.getCreatedDate(), 15)).getTime()){
				orderService.cancleOrder(order);
			}
		}
		
	}
}
