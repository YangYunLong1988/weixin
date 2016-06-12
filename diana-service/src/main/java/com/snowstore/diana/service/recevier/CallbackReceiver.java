package com.snowstore.diana.service.recevier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.hera.connector.vo.diana.D01000001;
import com.zendaimoney.hera.connector.MessageReceiver;
import com.zendaimoney.hera.connector.vo.Datagram;
import com.zendaimoney.hera.connector.vo.DatagramBody;

/**
 * 分销系统报文接收
 * Created by wulinjie on 2016/1/25.
 */
@Service
@Transactional
public class CallbackReceiver implements MessageReceiver {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private D01000001Receiver d01000001Receiver;

	private static final String RES_CODE_EXCEPTION = "000001"; // 异常

	@Override
	public Datagram receive(Datagram datagram) {
		Datagram response = null;
		try{
			LOGGER.info("--------报文处理开始---------");
			DatagramBody datagramBody = datagram.getDatagramBody();
			if (datagramBody instanceof D01000001){
				response = d01000001Receiver.receive(datagram);
			}else{
				LOGGER.info("--------没有对应的receiver---------");
			}
			LOGGER.info("--------报文处理结束---------");
			return response;
		}catch (Exception e){
			datagram.getDatagramBody().setMemo(e.getMessage());
			datagram.getDatagramBody().setOperateCode(RES_CODE_EXCEPTION);
			LOGGER.info("--------报文处理失败---------");
			return datagram;
		}
	}
}
