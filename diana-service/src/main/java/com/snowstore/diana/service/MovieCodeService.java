package com.snowstore.diana.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.snowstore.diana.domain.Coupon;
import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.domain.Tickets.Status;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.domain.UserGift;
import com.snowstore.diana.service.thirdapp.HuoClient;
import com.snowstore.diana.utils.CollectionUtil;

@Service
@Transactional
public class MovieCodeService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MovieCodeService.class);
	
	@Autowired
	private CustomerService customerService;
	@Autowired
	private TicketsService ticketsService;
	@Autowired
	private UserGiftService userGiftService;
	@Autowired
	private CouponService couponService;
	
	/**
	 * 获取火票token
	 * @author wulinjie
	 * @param orderId		订单ID
	 * @param ticketsIds	电影票兑换码ID
	 * @param session		HTTP SESSION
	 * @return
	 */
	public String getLoginToken(String orderId, List<Long> ticketsIds,HttpSession session){
		User user = customerService.getCurrentUser();
		//UserDetailsImpl userDetails = customerService.getUserDetails();
		//userDetails.getTransitData().put("orderId", orderId);
		//userDetails.getTransitData().put("ticketsIds", ticketsIds);
		session.setAttribute("orderId", orderId);
		session.setAttribute("ticketsIds", ticketsIds);
		
		JSONObject json = JSON.parseObject("{}");
		Map<String, String> params = new HashMap<String, String>();;
		
		try {
			String activityId = System.getProperty("caimiao.huo.rule"); //获取活动ID
			if(StringUtils.isNotEmpty(activityId)){
				params = new HashMap<String, String>();
				params.put("company", System.getProperty("caimiao.huo.appid")); 						//接入方标识
				params.put("phone", user.getMobile());	 												//用户手机号
				params.put("client", "mobile"); 														//终端类型
				params.put("activityid", activityId); 													//活动ID,根据具体的活动而定
				//火票卡券
				List<Tickets> ticketsList = ticketsService.findByIds(ticketsIds);
				List<String> huoCoupons = ticketsService.assignCoupon(ticketsList);
				if(huoCoupons!=null){
					params.put("codes", StringUtils.join(huoCoupons, ","));
				}
				//获取数字签名
				String secretKey = System.getProperty("caimiao.huo.secretKey");
				String sign = HuoClient.getKey(params, secretKey);
				params.put("sign", sign);
				params.put("success", "true");
				params.put("caimiao_url", System.getProperty("caimiao.url") + "/apiv2/couponticket");	 	//菜苗URL
			}else{
				params = new HashMap<String, String>();
				params.put("success", "false");
				List<UserGift> orderGift = userGiftService.findByOrderId(Long.valueOf(orderId));
				if(null == orderGift || orderGift.size()==0){
					params.put("message", "影片上映，我们将以短信形式通知您获取电影票，您可以先领取礼品。");
				}else{
					params.put("message", "影片上映，我们将以短信形式通知您获取电影票。");
				}
			}
		}catch(Exception e){
			params = new HashMap<String, String>();
			params.put("success", "false");
			params.put("message", "系统繁忙，请稍后再试。");
			LOGGER.error("菜苗登录失败", e);
		}finally {
			json = map2Json(params);
		}
		LOGGER.info("登陆菜苗，" +json.toJSONString());
		return JSON.toJSONString(json);
	}


	/**
	 * 定时同步菜苗座位信息
	 * @author wulinjie
	 */
	public void sychOrderResult(){
		LOGGER.info("开启定时同步菜苗座位信息");
		int count = 0;
		List<Tickets> ticketsList = ticketsService.findIssueTicketsList();
		if(!ticketsList.isEmpty()){
			//按照订单号分组
			Map<Object, List<Tickets>> orderMap = CollectionUtil.groupBy(ticketsList, "orderCode");
			Iterator<Object> iterator = orderMap.keySet().iterator();
			while(iterator.hasNext()){
				String orderCode = iterator.next().toString();	//订单号
				List<Tickets> subTicketsList = orderMap.get(orderCode);	//订单号对应的电影票
				JSONObject json = HuoClient.queryOrderInfo(orderCode);	//根据订单号，查询电影票信息
				if(json!=null && "0".equals(json.getString("code"))){
					JSONObject data = json.getJSONObject("data");
					String[] ticketInfo = StringUtils.split(data.getString("ticket_info"),",");
					for(int i = 0; i < subTicketsList.size(); i++){
						Tickets tickets = subTicketsList.get(i);
						tickets.setName(ticketInfo[0]);											//电影名称
						tickets.setPerformAddress(ticketInfo[1]);								//影院
						tickets.setScene(ticketInfo[2]);										//影厅
						try{
							tickets.setSeat(ticketInfo[3+i]);									//座位信息
						}catch(ArrayIndexOutOfBoundsException e){
							break;
						}
						tickets.setTicketCode(data.getString("ticket_code"));					//取票机取票码
						tickets.setTicketCaptcha(data.getString("ticket_captcha"));			//取票验证码
						tickets.setTicketCodeFront(data.getString("ticket_code_front"));		//前台取票码
						Date performTime = new Date();
						performTime.setTime(Long.valueOf(data.getString("film_start_time")) * 1000L);
						tickets.setPerformTime(performTime);									//场次
						tickets.setStatus(Tickets.Status.已兑换.name());
						ticketsService.saveOrUpdate(tickets);
						count++;
					}
				}
			}
		}
		LOGGER.info("菜苗座位信息同步结果数量：" + count);
	}
	

	/**
	 * 检查未兑换但绑定了卡券的电影票是否已经出票
	 * @author wulinjie
 	 * @param tickets 电影票
	 * @return 已出票则更新电影票兑换状态为已兑换
	 */
	public Tickets checkUnusedTickets(Tickets tickets){
		LOGGER.info("检查出票状态,ticket_id:" + tickets.getId()+",ticket_status:" + tickets.getStatus()+",coupon_id:"+ tickets.getCouponId());
		if(Status.未兑换.name().equals(tickets.getStatus()) && null != tickets.getCouponId()){
			Coupon coupon = couponService.getCouponById(tickets.getCouponId());
			JSONObject json = HuoClient.queryOrderCodeByCouponCode(coupon.getCode());
			String msg =json.getString("code");
			String orderCode = json.getString("data");
			if( "0".equals(msg) && orderCode.length() > 2 ){
				tickets.setOrderCode(orderCode);
				tickets.setStatus(Status.已兑换.name());
				tickets = ticketsService.saveOrUpdate(tickets);
				coupon.setOrderCode(orderCode);
				coupon.setIsUsed(1);
				couponService.saveOrUpdate(coupon);
			}
		}
		return tickets;
	}

	/**
	 * 核实未兑换的电影票出票状态
	 * @author wulinjie
	 * @param startDate 开始日期
	 * @param endDate	  结束日期
	 */
	public void verifyMovieCodeStatus(Date startDate, Date endDate){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		LOGGER.info("startDate:" + format.format(startDate) + ",endDate:" + format.format(endDate));
		if( startDate==null || endDate == null ){
			return;
		}
		List<Tickets> ticketsList = ticketsService.findUnusedTicketsList(startDate, endDate);
		if(!ticketsList.isEmpty()){
			for (Tickets tickets : ticketsList) {
				checkUnusedTickets(tickets);
			}
		}
	}
	
	/**
	 * map转换为json
	 * @author wulinjie
	 * @param map
	 * @return
	 */
	public JSONObject map2Json(Map<String,String> map){
		JSONObject json = JSON.parseObject("{}");
		Iterator<String> keys = map.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			String value = map.get(key);
			json.put(key, value);
		}
		return json;
	}

}
