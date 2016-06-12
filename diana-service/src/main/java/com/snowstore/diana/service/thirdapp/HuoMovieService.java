package com.snowstore.diana.service.thirdapp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.domain.UserGift;
import com.snowstore.diana.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 火票务订单处理
 * Created by wulinjie on 2016/2/24.
 */
@Service
@Transactional
public class HuoMovieService implements BaseMovieService {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private CustomerService customerService;
	@Autowired
	private TicketsService ticketsService;
	@Autowired
	private UserGiftService userGiftService;
	@Autowired
	private CouponService couponService;

	@Override
	public JSONObject getLoginUrl(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String orderId = request.getParameter("orderId");
		//String[] ticketsIds = Arrays.co  (request.getParameterValues("ticketsIds"),new Long[]);
		String[] ticketsIds = new String[]{};
		LOGGER.info("登陆火票务，orderId：{}", orderId);
		User user = customerService.getCurrentUser();
		session.setAttribute("orderId", orderId);
		session.setAttribute("ticketsIds", ticketsIds);

		JSONObject json = JSON.parseObject("{}");
		Map<String, String> params = new HashMap<String, String>();

		try {
			long count = couponService.countUnusedCouponCount();		  //获取未使用的卡券数量
			if(count < ticketsIds.length){
				LOGGER.info("卡券不足,可用卡券数量：" + count);
				json.put("success", "false");
				json.put("message", "目前操作的用户较多，我们尽快处理，请稍候再试。");
			}

			String activityId = System.getProperty("caimiao.huo.rule"); //获取活动ID
			if(StringUtils.isEmpty(activityId)){
				LOGGER.info("活动编码未设置");
				json.put("success", "false");
				List<UserGift> orderGift = userGiftService.findByOrderId(Long.valueOf(orderId));
				if(null == orderGift || orderGift.size()==0){
					json.put("message", "影片上映，我们将以短信形式通知您获取电影票，您可以先领取礼品。");
				}else{
					json.put("message", "影片上映，我们将以短信形式通知您获取电影票。");
				}
			}

			if(count>= ticketsIds.length && StringUtils.isNotEmpty(activityId)){
				json.put("company", System.getProperty("caimiao.huo.appid")); 			//接入方标识
				json.put("phone", user.getMobile());	 									//用户手机号
				json.put("num", ticketsIds.length + "");									//最大购票数
				json.put("activityid", activityId);										//活动ID,根据具体的活动而定
				json.put("client", "mobile"); 											//终端类型
				//获取数字签名
				String secretKey = System.getProperty("caimiao.huo.secretKey");
				String sign = HuoClient.getKey(params, secretKey);
				json.put("sign", sign);
				json.put("success", "true");
				json.put("caimiao_url", System.getProperty("caimiao.url") + "/apiv2/couponticket");	 	//菜苗URL
			}

		}catch (Exception e){
			params = new HashMap<String, String>();
			params.put("success", "false");
			params.put("message", "系统繁忙，请稍后再试。");
			LOGGER.error("火票务登录失败", e);

		}finally {
			LOGGER.info(json.toJSONString());
		}
		return json;
	}

	@Override
	public String sychOrder(HttpServletRequest request) {
		LOGGER.info("开始处理下单同步通知");

		String[] ticketsIds = (String[])request.getSession().getAttribute("ticketsIds");

		String orderCode = request.getParameter("orderCode");
		String num = request.getParameter("num");
		String info = request.getParameter("info");
		String start = request.getParameter("start");
		String end = request.getParameter("end");
		String timeout = request.getParameter("timeout");
		String sign = request.getParameter("sign");

		LOGGER.info("{orderCode：" + orderCode + "，num:" + num + ",info:[" + info + "],start:" + start + ",end:" + end + ",timeout:" + timeout + ",sign:" + sign);

		//开始验证签名是否正确
		String secretKey = System.getProperty("caimiao.huo.secretKey");
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderCode", orderCode);
		params.put("num", num);
		params.put("info", info);
		params.put("start", start);
		params.put("end", end);
		params.put("timeout", timeout);
		String md5Result = HuoClient.getKey(params, secretKey);
		if( !md5Result.equals(sign) ){
			LOGGER.error("处理下单同步通知出错，签名错误，票务签名:" + md5Result + "，火票签名:" + sign);
			throw new RuntimeException("处理下单同步通知出错，签名错误");
		}

		//检查该订单号在易票系统是否已存在
		int existsTicket = ticketsService.countByOrderCode(orderCode);
		if(existsTicket > 0){
			//已存在，则不做重复的处理
			return orderCode;
		}

		String[] ticketInfo = StringUtils.split(info, ",");
		String[] ticketSeats= Arrays.copyOfRange(ticketInfo, 5, ticketInfo.length);
		StringBuilder sb=new StringBuilder();
		for(String seat:ticketSeats){
			if(sb.length()>0){
				sb.append(",");
			}
			sb.append(seat);
		}
		String seats = sb.toString();
		int i = 0;
		while(Long.parseLong(num) > i){
			Tickets tickets = ticketsService.get(Long.parseLong(ticketsIds[i]));
			tickets.setOrderCode(orderCode);//火票订单号
			tickets.setStatus(Tickets.Status.已兑换.name());	//电影票状态
			tickets.setName(ticketInfo[0]);	//电影名称
			tickets.setLanguage(ticketInfo[1] + "  " + ticketInfo[2]);
			tickets.setPerformAddress(ticketInfo[3]);	//影院
			tickets.setScene(ticketInfo[4]);	////影厅
			try{
				tickets.setSeat(seats);//座位信息
			}catch(ArrayIndexOutOfBoundsException e){
				break;
			}
			Date performTime = new Date();
			performTime.setTime(Long.valueOf(start) * 1000L);
			tickets.setPerformTime(performTime);//放映时间
			Date orderTimeout = new Date();
			orderTimeout.setTime(Long.valueOf(timeout)*1000L);
			tickets.setOrderTimeout(orderTimeout);//火票订单支付超时时间
			i++;
			ticketsService.saveOrUpdate(tickets);
		}
		return orderCode;
	}
}
