package com.snowstore.diana.job;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.snowstore.diana.common.Constants.QuickpayStatus;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.PayInfo;
import com.snowstore.diana.domain.Order.Status;
import com.snowstore.diana.service.OrderService;
import com.snowstore.diana.service.PayInfoService;
import com.snowstore.diana.service.QuickPayService;
import com.snowstore.poseidon.client.vo.response.CustomerOrder;

@Component
public class SynchronizeOrderJob {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrderService orderService;
	@Autowired
	private QuickPayService quickPayService;
	@Autowired
	private PayInfoService payInfoService;
	@Autowired
	private SettingsService settingsService;

	@Scheduled(cron = "0 0/5 * * * ?")
	public void execute() {
		//检查系统设置
		Settings settings = settingsService.getByKey("generateOrderFromAtlantisa");
		if(null!=settings && "off".equals(settings.getValue())){
			return;
		}

		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.HOUR_OF_DAY) < 2) {
			return;
		}
		List<Long> orders = orderService.getUnSynchronizeOrder();
		LOGGER.info("开始同步订单，需要同步条数，{}",orders.size());
		for (Long id : orders) {
			Order order = orderService.get(id);
			LOGGER.info("订单同步[{}]", order.getId());
			try {
				orderService.generateOrderFromAtlantisa(order);
			} catch (Exception e) {
				LOGGER.error("订单" + id + "同步异常", e);
				order.setReferenceOrder("FAILED");
				orderService.saveOrUpdate(Arrays.asList(order));
			}
		}
	}

	/**
	 * 同步易联获取支付中订单状态(3分钟一次)
	 * 
	 * @author XieZG
	 * @Date:2016年2月22日下午1:36:05
	 */
	@Scheduled(cron = "0 0/3 * * * ?")
	public void updateOrder() {
		//检查系统设置
		Settings settings = settingsService.getByKey("updateQuickOrder");
		if(null!=settings && "off".equals(settings.getValue())){
			return;
		}

		LOGGER.info("开始更新支付中订单......");
		List<Order> orders = orderService.findByStatus(Status.付款中.name());
		// 单个查询
		for (Order order : orders) {
			List<CustomerOrder> customerOrders = quickPayService.queryOrder(order.getReferenceOrder());
			if (customerOrders != null && customerOrders.size() > 0) {
				// 更新订单状态
				String payStatus = customerOrders.get(0).getPayStatus();
				if (QuickpayStatus.PAYSUCCESS.equals(payStatus)) {
					// 支付成功
					orderService.orderPaySuccess(order.getId());
					PayInfo payInfo = payInfoService.getByOrderId(order.getId());
					if (null == payInfo) {
						payInfo = new PayInfo();
						payInfo.setOrderId(order.getId());
					}
					payInfo.setStatus(PayInfo.Status.支付成功.name());
					payInfo.setTradeNo(orderService.generatePayCode());
					payInfo.setPayPlatform(PayInfo.Platform.QUICK_PAY.name());
					// 保存payInfo对象，否则后台支付金额会为0
					payInfo.setPayAmount(order.getAmount());
					payInfoService.saveOrUpdate(payInfo);
				} else if (QuickpayStatus.PAYFAILURE.equals(payStatus)) {
					// 支付失败
					order.setStatus(com.snowstore.diana.domain.Order.Status.付款失败.name());
					orderService.saveOrUpdate(order);
				}
			} else {
				LOGGER.info("订单" + order.getId() + "在易联中未查到！");
			}
		}
	}
}
