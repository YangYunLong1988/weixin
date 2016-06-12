package com.snowstore.diana.service.thirdapp;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.snowstore.diana.common.HttpUtil;
import com.snowstore.diana.domain.CouponGroup;
import com.snowstore.diana.utils.Md5Util;

/**
 * 火票相关接口
 * Created by wulinjie on 2015/11/12.
 */
public class HuoClient {

	private static Logger LOGGER = LoggerFactory.getLogger(HuoClient.class);

	
	/**
	 * 获取火票的token
	 * @author wulinjie
	 * @return token
	 */
	public static String getToken() throws IOException {
		LOGGER.info("获取火票的token");
		String token = "";
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid",  System.getProperty("caimiao.huo.appid"));
		params.put("secret", System.getProperty("caimiao.huo.secret"));
		String body = HttpUtil.getSerializeParams(params);

		String url = System.getProperty("caimiao.huo.url");
		url = url + "/oauth/token";
		String result = HttpUtil.sendPostRequest(url, body);

		LOGGER.info("getToken result :{} ", result);

		JSONObject tokenJson = JSON.parseObject(result);
		JSONObject dataJson = (JSONObject)tokenJson.get("data");
		token = dataJson.getString("token");

		return token;
	}

	/**
	 * 根据卡券ID，查询卡券列表
	 * @author wulinjie
	 * @param groupId 卡券组ID
	 * @return
	 */
	public static JSONObject queryByGroup(String groupId) throws IOException{
		LOGGER.info("根据卡券组ID，查询火票卡券列表");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", getToken());
		params.put("systemKey", System.getProperty("caimiao.huo.systemKey"));	//接入方系统标示
		params.put("groupId", groupId);
		String url = System.getProperty("caimiao.huo.url") + "/coupon/queryByGroup";
		String body = HttpUtil.getSerializeParams(params);
		String result =  HttpUtil.sendPostRequest(url, body);
		
		LOGGER.info("queryByGroup result：{}", result );
		
		return JSON.parseObject(result);
	}

	/**
	 * 生成卡券组
	 * @author wulinjie
	 * @param group 卡券组VO
	 * @return 处理结果
	 */
	public static JSONObject createCouponGroup(CouponGroup group) throws IOException {
		LOGGER.info("创建火票卡券组");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", HuoClient.getToken());
		params.put("systemKey", System.getProperty("caimiao.huo.systemKey"));	//接入方系统标示
		params.put("name",  group.getGroupName());								//卡券组名称
		params.put("typeCode", group.getTypeCode());							//卡券类型
		params.put("amount", group.getAmout().toString());						//金额
		params.put("total", group.getTotal()+"");								//卡券数量
		params.put("enablePwd", group.getEnablePwd()+"");						//是否加密
		params.put("isGroupMovie", group.getIsGroupMovie()+"");					//是否集团投资电影
		params.put("secretKey", group.getSecretKey());							//活动标示
		params.put("note", group.getNote());									//备注

		String url = System.getProperty("caimiao.huo.url") + "/coupon/createGroup";
		String body = HttpUtil.getSerializeParams(params);
		String result = HttpUtil.sendPostRequest(url, body);
		
		LOGGER.info("createCouponGroup result:{}", result);

		return JSON.parseObject(result);
	}
	
	/**
	 * 获取排序加密后的URI
	 * @param params 要加密的参数
	 * @param secretKey 加密秘钥
	 * **/
	public static String getKey(Map<String, String> params,String secretKey){

		Object[] objs = params.keySet().toArray();
		Arrays.sort(objs);

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < objs.length; i++) {
			buffer.append(objs[i].toString());
			buffer.append("=");
			buffer.append(params.get(objs[i]));

			if(objs.length - 1 == i) break;

			buffer.append("&");
		}

		return Md5Util.MD5(Md5Util.MD5(secretKey+buffer.toString())+secretKey);
	}
	
	/**
	 * 查询火票订单信息
	 * @author wulinjie
	 * @param orderCode
	 * @return
	 */
	public static JSONObject queryOrderInfo(String orderCode){
		LOGGER.info("查询火票订单信息,orderCode:{}", orderCode);
		String result = "";
		try{
			Map<String, String> params = new HashMap<String, String>();
			params.put("company", System.getProperty("caimiao.huo.appid"));
			params.put("order_code", orderCode);
			String sign = getKey(params, System.getProperty("caimiao.huo.secretKey"));
			params.put("sign", sign);
			
			String url = System.getProperty("caimiao.url") + "/api/orderinfo";
			String body = HttpUtil.getSerializeParams(params);
			result = HttpUtil.sendPostRequest(url, body);

			LOGGER.info("查询火票订单信息，查询结果:{} ", result);

			return JSON.parseObject(result);
		}catch(Exception e){
			LOGGER.error("查询火票订单信息出错", e);
		}
		return null;
	}

	/**
	 * 根据卡券编号，查询火票订单编号
	 * @author wulinjie
	 * @param couponCode	卡券编号
	 * @return 火票订单编号
	 */
	public static JSONObject queryOrderCodeByCouponCode(String couponCode){
		LOGGER.info("查询火票订单编号,couponCode:{}", couponCode);
		String result = "";
		try{
			Map<String, String> params = new HashMap<String, String>();
			params.put("company", System.getProperty("caimiao.huo.appid"));
			params.put("code", couponCode);
			String sign = getKey(params, System.getProperty("caimiao.huo.secretKey"));
			params.put("sign", sign);

			String url = System.getProperty("caimiao.url") + "/api/getorder";
			String body = HttpUtil.getSerializeParams(params);
			result = HttpUtil.sendPostRequest(url, body);

			LOGGER.info("查询火票订单编号，查询结果: {}" , result);

			return JSON.parseObject(result);
		}catch(Exception e){
			LOGGER.error("查询火票订单编号出错", e);
		}
		return null;
	}

	/**
	 * 订单支付(使用卡券)
	 * @param orderCode	订单编号
	 * @param codes		卡券代码
	 * @return
	 */
	public static JSONObject payOrder(String orderCode,List<String> codes){
		LOGGER.info("火票订单支付,orderCode:{},codes:{}", orderCode, codes);
		String result = "";
		try{
			Map<String, String> params = new HashMap<String, String>();
			params.put("company", System.getProperty("caimiao.huo.appid"));
			params.put("orderCode", orderCode);
			params.put("codes",StringUtils.join(codes.toArray(),","));
			String sign = getKey(params, System.getProperty("caimiao.huo.secretKey"));
			params.put("sign", sign);

			String url = System.getProperty("caimiao.url") + "/apiv2/payOrderFormCoupon";
			String body = HttpUtil.getSerializeParams(params);
			result = HttpUtil.sendPostRequest(url, body);

			LOGGER.info("查询火票订单编号，查询结果:{} ", result);

			return JSON.parseObject(result);
		}catch(Exception e){
			LOGGER.error("火票订单支付失败", e);
		}
		return null;
	}
}
