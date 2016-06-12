package com.snowstore.diana.job;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.OrderService;
import com.snowstore.diana.service.SettingsService;
import com.snowstore.diana.service.logistics.LogisticsService;

@Component
public class LogisticsJob {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private LogisticsService logisticsService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private SettingsService settingsService;

	/*
	 * 礼品发货 修改为每两小时执行,每天10:30分开始到23:30分结束
	 */
	@Scheduled(cron = "0 30 10-23/2 * * ?")
	public void deliveryGift() {
		// 检查系统设置
		Settings settings = settingsService.getByKey("logistics");
		if (null != settings && "off".equals(settings.getValue())) {
			return;
		}

		LOGGER.info("start delevery gift");
		while (true) {
			// 分页查询 每页 1000条
			Page<Order> pageOrder = orderService.findUndeliveredOrders(0, 100);
			LOGGER.info("order count:{}", pageOrder.getTotalElements());
			if (CollectionUtils.isNotEmpty(pageOrder.getContent())) {
				for (Order order : pageOrder.getContent()) {
					try {
						logisticsService.deliver(order);
					} catch (Exception e) {
						LOGGER.warn(e.getMessage(), e);
					}
				}
				if (1 == pageOrder.getTotalPages()) {
					break;
				}
			} else {
				break;
			}
		}
		LOGGER.info("end delevery gift");
	}

}
