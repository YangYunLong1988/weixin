package com.snowstore.diana.service;

import com.snowstore.hera.connector.vo.nox.D99000001;
import com.zendaimoney.hera.connector.EsbConnector;
import com.zendaimoney.hera.connector.vo.Datagram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wulinjie on 2016/1/25.
 */
@Service
@Transactional
public class NoxSender {
	@Value("${snowstore.esb.system.nox.code}")
	private String receiverSystemCode;
	@Value("${snowstore.esb.system.nox.username}")
	private String receiverUserName;
	@Value("${snowstore.esb.system.nox.password}")
	private String receiverPassword;
	@Value("${snowstore.esb.system.self.code}")
	private String systemCode;
	@Autowired
	private EsbConnector esbConnector;

	/**
	 * 推送订单信息到分销系统
	 * @author wulinjie
	 * @param message	订单信息
	 */
	public Datagram sendOrders(D99000001 message) {
		Datagram datagram = new Datagram();
		datagram.getDatagramHeader().setReceiverSystem(receiverSystemCode);
		datagram.getDatagramHeader().setUserName(receiverUserName);
		datagram.getDatagramHeader().setPassword(receiverPassword);
		datagram.getDatagramHeader().setSenderSystemCode(systemCode);
		datagram.setDatagramBody(message);
		Datagram response = esbConnector.sendAndReceive(datagram);
		return response;
	}
}
