package com.snowstore.diana.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.snowstore.poseidon.client.JopClient;
import com.snowstore.poseidon.client.exception.BusinessException;
import com.snowstore.poseidon.client.exception.TimeoutException;
import com.snowstore.poseidon.client.vo.RequestBody;
import com.snowstore.poseidon.client.vo.Response;
import com.snowstore.poseidon.client.vo.request.CustomerOrderReq;
import com.snowstore.poseidon.client.vo.request.OrderQuickPayReq;
import com.snowstore.poseidon.client.vo.request.OrderQuickPayVerifyReq;
import com.snowstore.poseidon.client.vo.request.OrderReq;
import com.snowstore.poseidon.client.vo.request.QuickAuthenticationReq;
import com.snowstore.poseidon.client.vo.request.QuickVerificationReq;
import com.snowstore.poseidon.client.vo.request.SubbranchReq;
import com.snowstore.poseidon.client.vo.request.SupportBankReq;

/**
 * JopClient代理类 目前为了记录报文日志以及处理快捷支付返回规则不一的问题
 * 
 * @author XieZG
 * @Date:2016年2月24日上午11:31:02
 */
@Component
public class JopClientProxy {
	@Autowired
	private JopClient jopClient;
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	public Response req(RequestBody request) throws BusinessException {
		LOGGER.info("发送报文：" + JSONObject.toJSON(request));
		try {
			Response response = null;
			if (request instanceof SupportBankReq) {
				response = jopClient.req((SupportBankReq) request);
			} else if (request instanceof QuickAuthenticationReq) {
				response = jopClient.req((QuickAuthenticationReq) request);
			} else if (request instanceof QuickVerificationReq) {
				response = jopClient.req((QuickVerificationReq) request);
			} else if (request instanceof SubbranchReq) {
				response = jopClient.req((SubbranchReq) request);
			} else if (request instanceof OrderQuickPayReq) {
				response = jopClient.req((OrderQuickPayReq) request);
			} else if (request instanceof OrderQuickPayVerifyReq) {
				response = jopClient.req((OrderQuickPayVerifyReq) request);
			} else if (request instanceof CustomerOrderReq) {
				response = jopClient.req((CustomerOrderReq) request);
			} else if (request instanceof OrderReq) {
				response = jopClient.req((OrderReq) request);
			} else {
				throw new RuntimeException("没有这个请求对象");
			}
			if (response != null) {
				LOGGER.info("接收报文：" + JSONObject.toJSON(response));
			} else {
				LOGGER.info("接收报文：" + JSONObject.toJSON(response));
			}

			return response;

		}/* catch (BusinessException e) {
			LOGGER.info("接收报文：" + e);
			// 易连那边只要非成功状态就有可能抛异常
			throw e;
		} catch (SocketTimeoutException e2) {
			LOGGER.info("等待响应超时" + e2);
			BusinessException businessException = new BusinessException();
			businessException.setStatus("9999999");// 超时异常
			throw new BusinessException(businessException);
		}*/ catch (TimeoutException e) {
			LOGGER.info("接收报文：" + e);
			// 易连那边只要非成功状态就有可能抛异常
			throw e;
		}
	}
}
