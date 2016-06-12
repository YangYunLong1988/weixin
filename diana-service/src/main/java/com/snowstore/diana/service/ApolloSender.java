package com.snowstore.diana.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.snowstore.hera.connector.vo.mars.MarsMessage;
import com.snowstore.hera.connector.vo.pluto.PlutoMessage;
import com.zendaimoney.hera.connector.EsbConnector;
import com.zendaimoney.hera.connector.vo.Datagram;

@Service
public class ApolloSender {
	
	@Value("${snowstore.esb.system.mars.code}")
	private String receiverSystemCode;
	
	@Value("${snowstore.esb.system.self.code}")
	private String systemCode;
	
    @Autowired
    private EsbConnector esbConnector;  
    
    /**
	 * 发送报文
	 * 
	 * @param message
	 */
	public void sendMessage(PlutoMessage message) {
		Assert.notNull(message);
		Datagram datagram = new Datagram();
		datagram.getDatagramHeader().setReceiverSystem(receiverSystemCode);
		datagram.setDatagramBody(message);
		esbConnector.send(datagram);
	}

	/**
	 * 发送邮件
	 * 
	 * @param message
	 */
	public void sendMailMessage(MarsMessage message) {
		Assert.notNull(message);
		Datagram datagram = new Datagram();
		datagram.getDatagramHeader().setSenderSystemCode(systemCode);
		datagram.getDatagramHeader().setReceiverSystem(receiverSystemCode);
		datagram.getDatagramHeader() .setMessageSequence(systemCode + UUID.randomUUID().toString());
		datagram.getDatagramHeader().setMessageCode("MarsMessage");
		datagram.setDatagramBody(message);
		esbConnector.send(datagram);
	}
}