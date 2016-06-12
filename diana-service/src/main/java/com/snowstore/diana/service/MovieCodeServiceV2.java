package com.snowstore.diana.service;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.snowstore.diana.service.thirdapp.ZyClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.snowstore.diana.common.Calendars;
import com.snowstore.diana.domain.Coupon;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Order.Status;
import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.domain.Tickets.Agency;
import com.snowstore.diana.domain.Tickets.OrderPayStatus;
import com.snowstore.diana.service.thirdapp.HuoClient;
import com.snowstore.diana.service.thirdapp.ZyMovieService;
import com.snowstore.diana.utils.CollectionUtil;

/**
 * Created by wulinjie on 2016/1/8.
 */
@Service
@Transactional
public class MovieCodeServiceV2 {

	private static Logger LOGGER = LoggerFactory.getLogger(MovieCodeServiceV2.class);
	
	@Autowired
	private TicketsService ticketsService;
	
	@Autowired
	private CouponService couponService;
	
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private ZyMovieService zyMovieService;
	

	/**
	 * 登陆票务服务机构
	 * @author wulinjie
	 * @param request
	 * @param session
	 * @return
	 */
	public JSONObject getLoginUrl(HttpServletRequest request, HttpSession session){
		String agency = request.getParameter("agency");
		if(Agency.HUO == Agency.valueOf(agency)){				//火票务
			//return huoMovieService.getLoginUrl(request);
		}else if(Agency.ZHONGYING == Agency.valueOf(agency)){	//中影
			return zyMovieService.getLoginUrl(request);
		}
		return null;
	}

	/**
	 * 同步订单信息(同步)
	 * @author wulinjie
	 * @param request
	 * @return
	 */
	public String sychOrder(HttpServletRequest request) throws Exception {
		String agency = request.getSession().getAttribute("agency").toString();
		if(Agency.HUO == Agency.valueOf(agency)){				//火票务
			//return huoMovieService.sychOrder(request);
		}else if(Agency.ZHONGYING == Agency.valueOf(agency)){	//中影
			return zyMovieService.sychOrder(request);
		}
		return null;
	}

	/**
	 * 同步订单信息(异步)
	 * @author wulinjie
	 * @param request
	 * @return
	 */
	public JSONObject asychOrder(HttpServletRequest request) {
		return zyMovieService.asychOrder(request);
		//String agency = request.getSession().getAttribute("agency").toString();
		//if(Agency.HUO == Agency.valueOf(agency)){				//火票务
			//return huoMovieService.sychOrder(request);
		//}else if(Agency.ZHONGYING == Agency.valueOf(agency)){	//中影
		//
		//}
		//return null;
	}


	/**
	 * 获取未支付订单
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Object,List<Tickets>> findUnpaidTickets(){
		Query query = entityManager.createQuery("from Tickets T where T.agency = ?1 and T.orderCode is not null and T.orderStatus in ?2 and T.status = ?3 and exists (select 1 from Order O where O.id = T.refOrder and O.status = ?4) ");
		query.setParameter(1, Agency.ZHONGYING.name());
		query.setParameter(2, Arrays.asList(new String[]{ZyClient.STATUS_UNPAID, ZyClient.STATUS_PAID, ZyClient.STATUS_FAILED, ZyClient.STATUS_CANCEL, ZyClient.STATUS_DRAWBACK}));
		query.setParameter(3, Tickets.Status.已兑换.name());
		query.setParameter(4, Status.已付款.name());
		List<Tickets> ticketsList = query.getResultList();
		if(CollectionUtils.isEmpty(ticketsList)){
			ticketsList = new ArrayList<Tickets>();
		}
		LOGGER.info("待确认电影票数量：{}", ticketsList.size());
		Map<Object,List<Tickets>> ticketsMap = CollectionUtil.groupBy(ticketsList, "orderCode");	//按照火票订单号分组
		return ticketsMap;
	}


	/**
	 * 确认订单
	 * @param orderCode 订单编号
	 * @author wulinjie
	 */
	public void confirmOrderStatus(String orderCode,List<Tickets> ticketsList) throws Exception{
		zyMovieService.confirmOrderStatus(orderCode, ticketsList);
	}

	@Async
	public void confirmOrderStatus(Order order) {
		LOGGER.info("开始异步确认中影订单，orderId：{}", order.getId());
		int count = 0;
		try{
			List<Tickets> ticketsList = ticketsService.findByOrderId(order.getId());
			Map<Object,List<Tickets>> ticketsMap = CollectionUtil.groupBy(ticketsList, "orderCode");
			for (Object orderCode : ticketsMap.keySet()) {
				List<Tickets> items = ticketsMap.get(orderCode);
				if(null != orderCode && ZyClient.STATUS_UNPAID.equals(items.get(0).getOrderStatus())){
					zyMovieService.confirmOrderStatus(orderCode.toString(), items);
					count++;
				}
			}
		}catch (Exception e){
			LOGGER.info("异步确认中影订单异常", e);
		}
		LOGGER.info("结束异步确认中影订单，orderId：{}，count：{}", order.getId(), count);
	}


	/**
	 * 取消订单
	 * @param orderCode 订单编号
	 * @author wulinjie
	 */
	public void cancelOrder(String orderCode){
		zyMovieService.cancelOrder(orderCode);
	}

	/**
	 * 获取卡券等待支付和支付失败的火票务订单
	 * @author wulinjie
	 * @return 等待支付和支付失败的火票务订单
	 */
	@SuppressWarnings("unchecked")
	public Map<Object,List<Tickets>> findWaitAndFailedPayTickets(){
		Query query = entityManager.createQuery("from Tickets T where T.orderPayStatus in ?1 and T.orderCode is not null and exists (select 1 from Order O where O.id = T.refOrder and O.status = ?2) ");
		query.setParameter(1, Arrays.asList(new String[]{OrderPayStatus.UNPAID.name(), OrderPayStatus.FAILED.name()}));
		query.setParameter(2, Status.已付款.name());
		List<Tickets> ticketsList = query.getResultList();
		if(ticketsList.isEmpty()){
			return null;
		}
		LOGGER.info("问题订单数量：{}", ticketsList!=null ? ticketsList.size() : 0);
		Map<Object,List<Tickets>> ticketsMap = CollectionUtil.groupBy(ticketsList, "orderCode");	//按照火票订单号分组
		return ticketsMap;
	}

	/**
	 * 处理卡券发送失败的火票务订单
	 * @author wulinjie
	 */
	public void processPayFailedTickets(String orderCode,List<Tickets> ticketsList){
		LOGGER.info("开始处理火票订单{}", orderCode==null ? "" : orderCode.toString());

		//检查卡券是否已绑定
		for(Tickets tickets : ticketsList){
			if(null != tickets.getCouponId()){
				Coupon coupon = couponService.getCouponById(tickets.getCouponId());
				JSONObject json = HuoClient.queryOrderCodeByCouponCode(coupon.getCode());
				String s_msg =json.getString("code");
				String s_orderCode = json.getString("data");
				//TODO 火票务部分卡券无法查询到订单号，导致无法解除绑定
				if("0".equals(s_msg) && s_orderCode.length() > 2 && !orderCode.toString().equals(s_orderCode) ){
					LOGGER.info("解除订单{}对应的问题卡券{}", orderCode, coupon.getCode());
					tickets.setCouponId(null);	//解除卡券绑定
					tickets = ticketsService.saveOrUpdate(tickets);
				}
			}
		}
		ticketsList = ticketsService.findByOrderCode(orderCode);
		notifyPayInfo(orderCode, ticketsList);	//开始支付火票订单
	}

	@Async
	public void notifyPayInfoAsync(Order order){
		notifyPayInfo(order);
	}

	/**
	 * 已付款订单发送支付通知菜苗
	 * @param order 订单信息
	 * @author wulinjie
	 */
	@Transactional
	public void notifyPayInfo(Order order){
		LOGGER.info("开始处理火票订单支付,orderId:{}", order.getId());
		List<Tickets> ticketsList = ticketsService.findTicketByOrderId(order.getId());
		Map<Object,List<Tickets>> ticketsMap = CollectionUtil.groupBy(ticketsList, "orderCode");
		for (Object orderCode : ticketsMap.keySet()){
			if(orderCode != null){
				List<Tickets> items = ticketsMap.get(orderCode);
				try{
					notifyPayInfo(orderCode.toString(), items);
				}catch (Exception e){
					changeTicketsOrderPayStatus(OrderPayStatus.FAILED, items);
					LOGGER.error("火票订单支付失败", e);
				}
			}
		}
		LOGGER.info("火票订单支付处理完成");
	}

	/**
	 * 已付款订单发送支付通知菜苗
	 * @author wulinjie
	 * @param orderCode 火票务订单编号
	 * @param ticketsList 电影票信息
	 */
	@Transactional
	public boolean notifyPayInfo(String orderCode, List<Tickets> ticketsList) {
		LOGGER.info("开始支付火票订单[orderCode:{}]", orderCode);

		//检查是否已出票或已支付
		JSONObject json = HuoClient.queryOrderInfo(orderCode.toString());
		LOGGER.info("检查出票状态，检查结果：{}", json.toJSONString());

		//如果订单11-支付成功、12-出票成功、13-已取票，则更新订单支付状态
		if(json != null && json.getString("data").length() > 2) {
			JSONObject data = json.getJSONObject("data");
			String status = data.getString("status");
			if(StringUtils.isNotEmpty(status) && ("11".equals(status) || "12".equals(status) || "13".equals(status))){
				changeTicketsOrderPayStatus(OrderPayStatus.PAID, ticketsList);
				return true;
			}
		}

		//检查订单是否已过期
		Date currDate = Calendars.getCurrentDate();
		if(ticketsList.get(0).getOrderTimeout()!=null && currDate.after(ticketsList.get(0).getOrderTimeout())){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			LOGGER.info("订单已过期无法处理,过期时间：{}", simpleDateFormat.format(ticketsList.get(0).getOrderTimeout()));
			changeTicketsOrderPayStatus(OrderPayStatus.OVERDUE, ticketsList);
			ticketsService.saveOrUpdate(ticketsList);
			return true;
		}

		List<String> couponList = null;
		try {
			couponList = ticketsService.assignCoupon(ticketsList);		//分配卡券
		}catch (Exception e){
			LOGGER.error("分配卡券失败", e);
			throw new RuntimeException(e);
		}
		json = HuoClient.payOrder(orderCode.toString(), couponList);			//发送支付通知到火票务
		LOGGER.info("支付结果：{}", JSONObject.toJSONString(json));
		if(json==null || !"0".equals(json.getString("code"))){
			LOGGER.info("火票订单[{}]支付失败", orderCode);
			changeTicketsOrderPayStatus(OrderPayStatus.FAILED, ticketsList);
			return false;
		}else{
			LOGGER.info("火票订单[{}]支付成功", orderCode);
			changeTicketsOrderPayStatus(OrderPayStatus.PAID, ticketsList);
			return true;
		}
	}

	/**
	 * 批量修改电影票支付状态
	 * @author wulinjie
	 * @param status	{@link com.snowstore.diana.domain.Tickets.OrderPayStatus}支付状态
	 * @param ticketsList
	 */
	public void changeTicketsOrderPayStatus(OrderPayStatus status,List<Tickets> ticketsList){
		for(Tickets tickets : ticketsList){
			tickets.setOrderPayStatus(status.name());
		}
		ticketsService.saveOrUpdate(ticketsList);
	}

	@Transactional(readOnly = true)
	public List<Object[]> getSeatMerged(int maxRow){
		LOGGER.info("获取未拆分座位的电影票信息");
		StringBuffer sql = new StringBuffer("");
		sql.append(" select * from ( \n ")
		   .append("   select t1.order_code, t1.seat, count(t1.id) as cnt from diana_tickets t1 \n ")
		   .append("   where t1.seat is not null \n ")
		   .append("   and t1.order_code is not null \n ")
		   .append("   and t1.seat like '%,%' \n ")
		   .append("   group by t1.order_code,t1.seat \n ")
		   .append("   having count(t1.id) > 1 \n ")
		   .append("   order by t1.order_code \n ")
		   .append(" ) where rownum <= ?1 ");
		Query query = entityManager.createNativeQuery(sql.toString()).setParameter(1, maxRow);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		LOGGER.info("未拆分座位的电影票数量:{}", resultList.size());
		return resultList;
	}

	public void splitSeatInfo(List<Object[]> resultList){
		LOGGER.info("开始拆分电影票座位信息");
		int count = 0;
		for (Object[] result : resultList) {							//更新出票订单
			String orderCode = result[0].toString();					//出票订单号
			LOGGER.info("orderCode:{}", orderCode);
			String[] seatInfo = result[1].toString().split(",");		//拆分后的座位信息
			int ticketCount = Integer.parseInt(result[2].toString());	//座位数量
			//开始更新座位信息
			List<Tickets> ticketsList = ticketsService.findByOrderCode(orderCode);
			for(int i = 0; i < ticketCount; i++){
				Tickets tickets = ticketsList.get(i);
				tickets.setSeat(seatInfo[i]);
				ticketsService.saveOrUpdate(tickets);
			}
			count++;
		}
		LOGGER.info("结束拆分电影票座位信息，处理数量:{}", count);
	}

}
