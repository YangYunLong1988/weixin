package com.snowstore.diana.service.thirdapp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.snowstore.diana.common.HttpUtil;

/**
 * 中影客户端
 * @author wulinjie
 * Created by wulinjie on 2016/2/25.
 */
public class ZyClient {

	private static Logger LOGGER = LoggerFactory.getLogger(ZyClient.class);

	public static final String CODE_SUCCESS = "001";			//成功
	public static final String CODE_PENDING = "7032";			//出票中
	public static final String CODE_REPEAT_FORBIDDEN = "7007";	//不能重复确认

	public static final String STATUS_UNPAID = "1";		//未支付
	public static final String STATUS_CANCEL = "2";		//已取消
	public static final String STATUS_PAID = "3";		//已支付
	public static final String STATUS_PRINTED = "4";	//出票成功
	public static final String STATUS_FAILED = "5";		//出票失败
	public static final String STATUS_DRAWBACK = "6";	//已退票

	/**
	 * 查询订单
	 * @param orderCode  订单号
	 * @return
	 */
	public static JSONObject queryOrderDetail(String orderCode,String uid, String orderTime, String filmNo, String areaNo){
		LOGGER.info("查询中影订单信息, orderCode:{},uid:{},orderTime:{},filmNo:{},areaNo:{}", orderCode,uid,orderTime,filmNo,areaNo);
		String result = "";
		try{
			Map<String, String> params = new HashMap<String, String>();
			params.put("channelCode", System.getProperty("zhongying.channelno"));
			params.put("orderCode", orderCode);
			params.put("uid", uid);
			params.put("orderTime", orderTime);
			params.put("filmNo", filmNo);
			params.put("areaNo", areaNo);
			String sign = getKey(params, System.getProperty("zhongying.api.secretKey"));
			params.put("sign", sign);

			String url = System.getProperty("zhongying.api.url") + "?method=ysQueryOrder.json";
			String body = HttpUtil.getSerializeParams(params);
			LOGGER.info("请求参数:{}", url + "&" +body);
			result = HttpUtil.sendRequest(url + "&" + body);
			result = URLDecoder.decode(result,"utf-8");
			LOGGER.info("查询结果:{} ", result);
			if(StringUtils.isNotEmpty(result)){
				return JSON.parseObject(result);
			}
		}catch (Exception e){
			LOGGER.error("查询中影订单信息异常", e);
		}
		return null;
	}

	/**
	 *
	 * @param orderCode
	 * @return
	 */
	public static JSONObject confirmOrderStatus(String orderCode){
		LOGGER.info("开始确认中影订单状态, orderCode:{}", orderCode);
		String result = "";
		try{
			Map<String, String> params = new HashMap<String, String>();
			params.put("channelCode", System.getProperty("zhongying.channelno"));
			params.put("orderCode", orderCode);
			String sign = getKey(params, System.getProperty("zhongying.api.secretKey"));
			params.put("sign", sign);

			String url = System.getProperty("zhongying.api.url") + "?method=ysSubmitOrder.json";
			String body = HttpUtil.getSerializeParams(params);
			result = HttpUtil.sendPostRequest(url, body);
			result = URLDecoder.decode(result,"utf-8");
			//result = HttpUtil.sendRequest(url + "&" + body);
			LOGGER.info("确认结果:{} ", result);
			if(StringUtils.isNotEmpty(result)){
				return JSON.parseObject(result);
			}
		}catch (Exception e){
			LOGGER.error("确认中影订单异常", e);
		}
		return JSON.parseObject("{}");
	}

	/**
	 * 取消订单
	 * @param orderCode  订单号
	 * @return
	 */
	public static JSONObject cancelOrder(String orderCode){
		LOGGER.info("取消中影订单, orderCode:{}", orderCode);
		String result = "";
		try{
			Map<String, String> params = new HashMap<String, String>();
			params.put("channelCode", System.getProperty("zhongying.channelno"));
			params.put("orderCode", orderCode);
			String sign = getKey(params, System.getProperty("zhongying.api.secretKey"));
			params.put("sign", sign);

			String url = System.getProperty("zhongying.api.url") + "?method=ysCancelOrder.json";
			String body = HttpUtil.getSerializeParams(params);
			result = HttpUtil.sendRequest(url + "&" + body);
			result = URLDecoder.decode(result,"utf-8");
			LOGGER.info("取消订单执行结果:{} ", result);
			if(StringUtils.isNotEmpty(result)){
				return JSON.parseObject(result);
			}
		}catch (Exception e){
			LOGGER.error("取消中影订单", e);
		}
		return null;
	}

	/**
	 * 获取签名
	 * @author wulinjie
	 * @param params			查询参数
	 * @param secretKey		密钥
	 * @return
	 */
	public static String getKey(Map<String, String> params,String secretKey){
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);

		List<String> paramPairs = new ArrayList<String>();
		for (int i = 0; i < keys.length; i++) {
			//method参数不参与签名的生成
			if (keys[i].equals("method")) {
				continue;
			}
			//连接key和value：key=value
			paramPairs.add(keys[i] + "=" + params.get(keys[i]));
		}
		//每个键值对用&连接：key1=value1&key2=value2&key3=value3
		String temp = StringUtils.join(paramPairs, "&");
		//对字符串进行URLl编码
		try {
			temp = URLEncoder.encode(temp, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			LOGGER.error("生成中影签名错误", e);
		}
		temp = secretKey + temp + secretKey;		//参数串首、尾部加上通讯密钥
		String sign = DigestUtils.md5Hex(temp);
		return sign;
	}

}
