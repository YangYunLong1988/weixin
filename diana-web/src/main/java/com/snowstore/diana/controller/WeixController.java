package com.snowstore.diana.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.foxinmy.weixin4j.api.BaseApi;
import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.mp.api.OauthApi;
import com.foxinmy.weixin4j.mp.model.OauthToken;
import com.foxinmy.weixin4j.token.TokenHolder;
import com.foxinmy.weixin4j.util.DigestUtil;
import com.foxinmy.weixin4j.util.MapUtil;
import com.snowstore.diana.common.DianaConstants.DianaDevice;
import com.snowstore.diana.domain.PayInfo;
import com.snowstore.diana.service.CardStockService;
import com.snowstore.diana.service.CustomerService;
import com.snowstore.diana.service.OrderService;
import com.snowstore.diana.service.PayInfoService;
import com.snowstore.diana.vo.Result;
import com.snowstore.log.annotation.UserLog;


@Controller
public class WeixController {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Autowired
	private PayInfoService payInfoService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OauthApi oauthApi;
	@Autowired
	private TokenHolder weixinJSTicketTokenHolder;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private CardStockService cardStockService;

	@RequestMapping("/weixin/notify")
	@ResponseBody
	@UserLog(remark = "【微信】微信支付回调")
	public String weixPayNotify(@RequestBody String msg, HttpServletRequest req) {
		// TODO 缺少签名校验
		LOGGER.info("支付消息通知.......");
		Document document = null;
		try {
			document = DocumentHelper.parseText(msg);
		} catch (DocumentException e) {
			LOGGER.error("", e);
		}
		Element root = document.getRootElement();
		LOGGER.info(root.asXML());
		String returnCode = root.elementText("return_code");
		if ("FAIL".equals(returnCode)) {
			return null;// 消息通讯失败
		}
		String resultCode = root.elementText("result_code");
		String outTradeNo = root.elementText("out_trade_no");
		PayInfo payInfo = payInfoService.getByTradeNo(outTradeNo);
		payInfo.setPayResultNotify(root.asXML());
		if ("FAIL".equals(resultCode)) {
			payInfo.setStatus(PayInfo.Status.支付失败.name());
		} else {
			LOGGER.info("支付成功.......");
			orderService.orderPaySuccess(payInfo.getOrderId());
			cardStockService.paySuccess(payInfo.getOrderId());
			payInfo.setStatus(PayInfo.Status.支付成功.name());
		}
		payInfoService.saveOrUpdate(payInfo);
		return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
	}	
	
	/**
	 * 校验订单是否支付成功
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/weixin/checkPayStatus")
	@ResponseBody
	@UserLog(remark = "【微信】校验订单是否支付成功")
	public Result<String> checkPayStatus(Long id) {
		LOGGER.info("校验订单是否支付成功......");
		Result<String> result = new Result<String>();
		PayInfo payInfo = payInfoService.getByOrderId(id);
		if (PayInfo.Status.支付成功.name().equals(payInfo.getStatus())) {
			LOGGER.info("支付成功.......");
			result.setType(Result.Type.SUCCESS);
		} else if (PayInfo.Status.支付失败.name().equals(payInfo.getStatus())) {
			result.setType(Result.Type.FAILURE);
			result.addMessage("支付失败");
		} else {
			result.setType(Result.Type.FAILURE);
			result.addMessage("您还未支付");
		}
		return result;
	}

	@RequestMapping(value = "/authorizeCallback")
	@UserLog(remark = "【微信】微信授权")
	public String authorizeCallback(String code, String state, HttpServletRequest request) throws Exception {
		OauthToken oauthToken = oauthApi.getOauthToken(code);
		customerService.setUserDevice(request, DianaDevice.JUPITER.name());
		return "redirect:" + state + "?openId=" + oauthToken.getOpenId();
	}

	@RequestMapping("/weixin/getWeixinJSConfig")
	@ResponseBody
	@UserLog(remark = "【微信】生成微信签名")
	public Result<JSONObject> getWeixinJSConfig(String url) {
		Result<JSONObject> result = new Result<JSONObject>(Result.Type.SUCCESS);
		JSONObject conf = new JSONObject();
		try {
			conf.put("jsapi_ticket", weixinJSTicketTokenHolder.getAccessToken());
		} catch (WeixinException e) {
			result.setType(Result.Type.FAILURE);
			result.addMessage("获取sapi_ticket失败");
			LOGGER.error("获取sapi_ticket失败", e);
			return result;
		}
		conf.put("noncestr", "123456");
		conf.put("timestamp", new Date().getTime() / 1000 + "");
		conf.put("url", url);
		StringBuilder sb = new StringBuilder();
		// a--->string1
		sb.append(MapUtil.toJoinString(conf, false, false, null));
		String signature = DigestUtil.SHA1(sb.toString());
		conf.put("signature", signature);
		conf.put("appId", BaseApi.DEFAULT_WEIXIN_ACCOUNT.getId());
		result.setData(conf);
		return result;

	}
}
