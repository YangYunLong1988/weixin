package com.snowstore.diana.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.Order;
import com.snowstore.diana.repository.OrderDao;
import com.snowstore.hera.connector.vo.mars.MarsMessage;


@Service
@Transactional
public class MessageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
	
	@Autowired
	private ApolloSender apolloSender;
	
	@Autowired
	private OrderDao orderDao;

	/**
	 * 公司名称
	 */
	public enum Company{
		易联天下("1"), 金鹿财行("2"), 易加("3"), 利人金融("4");

		private String value;

		private Company(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	
	@Async
	public void paySuccessMessageNotify(Long orderId,String messageString) {
		LOGGER.info("订单支付成功,准备发送短信通知用户");
		
		Order order = orderDao.findOne(orderId);	
		MarsMessage datagramBody = new MarsMessage();
		datagramBody.setTo(order.getUser().getMobile());
	
		datagramBody.setOprOption("4");
		datagramBody.setSubject(null);
		datagramBody.setMessageType("2");


		if ((order.getUser().getPlatformNo() >= 1000000) && (order.getUser().getPlatformNo() < 2000000)) {
			datagramBody.setFrom("2");//金鹿财行
			messageString=messageString.replace("400-6196-828", "400-0033-699");
		} else if ((order.getUser().getPlatformNo() >= 2000000) && (order.getUser().getPlatformNo() < 3000000)){
			datagramBody.setFrom("3");//易加
		} else if ((order.getUser().getPlatformNo() >= 5000000) && (order.getUser().getPlatformNo() < 6000000)){
			datagramBody.setFrom("4");//利人金融
		}else {
			datagramBody.setFrom("1");//易联天下
		}
		datagramBody.setContent(messageString);
		apolloSender.sendMailMessage(datagramBody);
		
	}

	/**
	 * 短信告知客户电影票信息
	 * @param mobile	手机号
	 * @param message	短信内容
	 * @param company	公司前缀
	 */
	@Async
	public void sendShortMessage(String mobile, String message, Company company ) {
		LOGGER.info("电影票预定成功,准备发送短信通知用户");

		MarsMessage datagramBody = new MarsMessage();
		datagramBody.setOprOption("4");
		datagramBody.setSubject(null);
		datagramBody.setMessageType("2");
		datagramBody.setFrom(company.getValue());
		datagramBody.setTo(mobile);
		if(company.compareTo(Company.金鹿财行) == 0){
			message = message.replace("400-6196-828", "400-0033-699");
		}

		datagramBody.setContent(message);
		apolloSender.sendMailMessage(datagramBody);
		
	}

}
