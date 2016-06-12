package com.snowstore.diana.service.thirdapp;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.domain.Tickets.Agency;
import com.snowstore.diana.domain.Tickets.Status;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.domain.UserGift;
import com.snowstore.diana.service.MessageService.Company;
import com.snowstore.diana.utils.SignUtil;

/**
 * 中影订单处理
 * Created by wulinjie on 2016/2/24.
 */
@Service
@Transactional
public class ZyMovieService {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private TicketsService ticketsService;
	@Autowired
	private UserGiftService userGiftService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private SettingsService settingsService;

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 中影登陆URL
	 * @author wulinjie
	 * @param request
	 * @return
	 */
	public JSONObject getLoginUrl(HttpServletRequest request) {
		HttpSession session = request.getSession();

		User user = customerService.getCurrentUser();

		String agency = request.getParameter("agency");
		String orderId = request.getParameter("orderId");
		String[] ticketsIdsStr = request.getParameter("ticketsIds").split(",");
		List<Long> ticketsIds = new ArrayList<Long>();
		for (int i=0;i<ticketsIdsStr.length;i++) {
			ticketsIds.add(Long.parseLong(ticketsIdsStr[i]));
		}

		LOGGER.info("登陆中影，orderId：{}", orderId);
		session.setAttribute("orderId", orderId);
		session.setAttribute("ticketsIds", ticketsIds);
		session.setAttribute("agency", agency);

		JSONObject json = JSON.parseObject("{}");
		try{
			Settings settings = settingsService.getByKey("filmLogin");
			if(null!=settings && "off".equals(settings.getValue())){
				LOGGER.info("禁止登陆中影选座页面");
				json.put("success","false");
				List<UserGift> orderGift = userGiftService.findByOrderId(Long.valueOf(orderId));
				if(null == orderGift || orderGift.size()==0){
					json.put("message", "影片上映，我们将以短信形式通知您获取电影票，您可以先领取礼品。");
				}else{
					json.put("message", "影片上映，我们将以短信形式通知您获取电影票。");
				}
				return json;
			}

			String url = System.getProperty("zhongying.url");
			if(StringUtils.isEmpty(url)){
				LOGGER.info("中影URL编码未设置");
				json.put("success","false");
				List<UserGift> orderGift = userGiftService.findByOrderId(Long.valueOf(orderId));
				if(null == orderGift || orderGift.size()==0){
					json.put("message", "影片上映，我们将以短信形式通知您获取电影票，您可以先领取礼品。");
				}else{
					json.put("message", "影片上映，我们将以短信形式通知您获取电影票。");
				}
				return json;
			}

			//获取影片编码
			String filmNo = System.getProperty("zhongying.filmno");
			if(StringUtils.isEmpty(filmNo)){
				LOGGER.info("中影影片编码未设置");
				json.put("success","false");
				List<UserGift> orderGift = userGiftService.findByOrderId(Long.valueOf(orderId));
				if(null == orderGift || orderGift.size()==0){
					json.put("message", "影片上映，我们将以短信形式通知您获取电影票，您可以先领取礼品。");
				}else{
					json.put("message", "影片上映，我们将以短信形式通知您获取电影票。");
				}
				return json;
			}

			//获取影片编码
			String channelNo = System.getProperty("zhongying.channelno");
			if(StringUtils.isEmpty(channelNo)){
				LOGGER.info("中影渠道编码未设置");
				json.put("success","false");
				List<UserGift> orderGift = userGiftService.findByOrderId(Long.valueOf(orderId));
				if(null == orderGift || orderGift.size()==0){
					json.put("message", "影片上映，我们将以短信形式通知您获取电影票，您可以先领取礼品。");
				}else{
					json.put("message", "影片上映，我们将以短信形式通知您获取电影票。");
				}
				return json;
			}

			//校验该兑换码是否选择了礼品
			List<Tickets> ticketsList = ticketsService.findByIds(ticketsIds);
			if(CollectionUtils.isNotEmpty(ticketsList)){
				for (Tickets tickets : ticketsList) {
					if(tickets.getSubOrder()!=null && tickets.getSubOrder().getUserGift()!=null){
						json.put("success","false");
						json.put("message", "已选择礼品的兑换码无法选座。");
						return json;
					}
				}

			}

			//开始签名
			Map<String, String> params = new HashMap<String, String>();
			params.put("filmno", filmNo);
			params.put("channelno", channelNo);
			params.put("uid", user.getId()+"");
			params.put("num", ticketsIds.size()+"");
			String sign = SignUtil.signature(System.getProperty("diana.api.secretKey"), params);

			StringBuffer sb = new StringBuffer();
			sb.append(url).append("?filmno=").append(params.get("filmno")).append("&channelno=").append(params.get("channelno")).append("&uid=").append(params.get("uid")).append("&num=").append(params.get("num")).append("&sign=").append(sign);

			json.put("success","true");
			json.put("url", sb.toString());

		}catch (Exception e){
			json.put("success","false");
			json.put("message","系统繁忙，请稍后再试。");
			LOGGER.error("中影登陆失败", e);
		}
		return json;
	}

	/**
	 * 中影同步回调接口
	 * @author wulinjie
	 * @param request
	 * @return
	 */
	public String sychOrder(HttpServletRequest request) throws Exception{
		LOGGER.info("开始处理中影同步回调信息");

		@SuppressWarnings("unchecked")
		List<Long> ticketsIds = (List<Long>) request.getSession().getAttribute("ticketsIds");

		//获取参数
		String orderCode = request.getParameter("orderCode");
		String num = request.getParameter("num");
		String info = request.getParameter("info");
		String start = request.getParameter("start");
		String end = request.getParameter("end");
		String timeout = request.getParameter("timeout");
		String sign = request.getParameter("sign");

		LOGGER.info("{orderCode：" + orderCode + "，num:" + num + ",info:{" + info + "},start:" + start + ",end:" + end + ",timeout:" + timeout + ",sign:" + sign + "}");

		//验证签名
		Map<String, String> params = new HashMap<String,String>();
		params.put("orderCode", orderCode);
		params.put("num", num);
		params.put("info", info);
		params.put("start", start);
		params.put("end", end);
		params.put("timeout", timeout);
		String signCheck = SignUtil.signature(System.getProperty("diana.api.secretKey"), params);
		if(!sign.toLowerCase().equals(signCheck.toLowerCase())){
			LOGGER.info("签名错误，易票签名：{}，中影签名：{}", signCheck, sign);
			return orderCode;
		}

		//校验该电影票是否已转为礼品
		if(CollectionUtils.isNotEmpty(ticketsIds)){
			for (Long ticketsId : ticketsIds) {
				Tickets tickets = ticketsService.get(ticketsId);
				if(tickets!=null && tickets.hasGift()){
					LOGGER.info("电影票{}已转为礼品，无法同步中影订单", ticketsId);
					return orderCode;
				}
			}
		}

		//检查该订单号在易票系统是否已存在
		List<Tickets> ticketsList = ticketsService.findByOrderCode(orderCode);
		if( CollectionUtils.isNotEmpty(ticketsList) ){
			return orderCode;	//已存在，则不做重复的处理
		}

		String[] ticketInfo = StringUtils.split(info, ",");
		String[] ticketSeats= ticketInfo[5].split("[|]");
		if(Integer.parseInt(num) != ticketSeats.length){
			throw new RuntimeException("参数num与座位数量不一致，num:" + num + ",seatNum:" + ticketSeats.length);
		}
		int i = 0;
		while(Long.parseLong(num) > i){
			Tickets tickets = ticketsService.get(Long.parseLong(ticketsIds.get(i)+""));
			tickets.setOrderCode(orderCode);//火票订单号
			tickets.setStatus(Tickets.Status.已兑换.name());//电影票状态
			tickets.setName(ticketInfo[0]);	//电影名称
			tickets.setLanguage(ticketInfo[1] + "  " + ticketInfo[2]);
			tickets.setPerformAddress(ticketInfo[3]);	//影院
			tickets.setScene(ticketInfo[4]);	////影厅
			tickets.setSeat(ticketSeats[i]);//座位信息
			Date performTime = new Date();
			performTime.setTime(Long.valueOf(start));
			tickets.setPerformTime(performTime);//放映时间
			Date orderTimeout = new Date();
			orderTimeout.setTime(Long.valueOf(timeout));
			tickets.setOrderTimeout(orderTimeout);			//订单支付超时时间
			tickets.setAgency(Agency.ZHONGYING.name());
			tickets.setOrderStatus(ZyClient.STATUS_UNPAID);//未支付
			i++;
			ticketsService.saveOrUpdate(tickets);
		}
		LOGGER.info("结束处理中影同步回调信息");
		return orderCode;

	}

	/**
	 * 中影异步回调接口
	 * @author wulinjie
	 * @param request
	 * @return
	 */
	public JSONObject asychOrder(HttpServletRequest request) {
		LOGGER.info("开始处理中影异步回调信息");
		JSONObject json = JSON.parseObject("{}");

		try{
			request.setCharacterEncoding("UTF-8");
			Map<String,String> params = decodeRequestParams(request);
			String channelCode = params.get("channelCode");	//平台编号
			String orderCode = params.get("orderCode");		//平台订单号
			String cinemaNo =  params.get("cinemaNo");			//影院编码
			String cinemaName = params.get("cinemaName");		//影院名称
			String exchangeAddr =params.get("exchangeAddr");	//取票地址
			String showTime = params.get("showTime");			//放映时间
			String filmNo = params.get("filmNo");
			String filmName = params.get("filmName");
			String showType = params.get("showType");
			String hallNo = params.get("hallNo");
			String hallName = params.get("hallName");
			String ticketCount = params.get("ticketCount");
			String seatName = params.get("seatName");
			String amount = params.get("amount");
			String printMode = params.get("printMode");
			String voucherCode = params.get("voucherCode");
			String printCode = params.get("printCode");
			String status = params.get("status");
			String smsInfo = params.get("smsInfo");
			String sign = params.get("sign");

			LOGGER.info("channelCode:{},orderCode:{},cinemaNo:{},cinemaName:{},exchangeAddr:{},showTime:{},filmNo:{},filmName:{},showType:{},hallNo:{},hallName:{},ticketCount:{},seatName:{},amount:{},printMode:{},voucherCode:{},printCode:{},status:{},smsInfo:{},sign:{}",channelCode,orderCode,cinemaNo,cinemaName,exchangeAddr,showTime,filmNo,filmName,showType,hallNo,hallName,ticketCount,seatName,amount,printMode,voucherCode,printCode,status,smsInfo,sign);

			//验证签名
			/*
			Map<String, String> signParams = new HashMap<String, String>();
			signParams.put("channelCode",channelCode);
			signParams.put("orderCode", orderCode);
			signParams.put("cinemaNo", cinemaNo);
			signParams.put("cinemaName", cinemaName);
			signParams.put("exchangeAddr", exchangeAddr);
			signParams.put("showTime", showTime);
			signParams.put("filmNo", filmNo);
			signParams.put("filmName", filmName);
			signParams.put("showType", showType);
			signParams.put("hallNo", hallNo);
			signParams.put("hallName", hallName);
			signParams.put("ticketCount", ticketCount);
			signParams.put("seatName", seatName);
			signParams.put("amount", amount);
			signParams.put("printMode", printMode);
			signParams.put("voucherCode", voucherCode);
			signParams.put("printCode", printCode);
			signParams.put("status", status);
			signParams.put("smsInfo", smsInfo);

			//开始验证签名
			String signCheck = ZyClient.getKey(params, System.getProperty("zhongying.api.secretKey"));
			if( !sign.toLowerCase().equals(signCheck.toLowerCase()) ){
				LOGGER.info("签名错误，易票签名：{}，中影签名：{}", signCheck, sign);
				json.put("code","9003");
				json.put("msg","签名错误");
				return json;
			}
			*/

			//检查该订单号在易票系统是否已存在
			List<Tickets> ticketsList = ticketsService.findByOrderCode(orderCode);
			if( CollectionUtils.isEmpty(ticketsList) ){
				LOGGER.info("订单编号不存在，orderCode:{}", orderCode);
				json.put("code","7005");
				json.put("msg","订单不存在");
				return json;
			}

			String[] ticketSeats = seatName.split("[|]");
			int index = 0;
			if(CollectionUtils.isNotEmpty(ticketsList)){
				for (Tickets tickets : ticketsList) {
					tickets.setCinemaNo(cinemaNo);					//影院编码
					tickets.setPerformAddress(cinemaName);			//影院名称
					tickets.setPerformAddressDetail(exchangeAddr);	//取票机
					tickets.setPerformTime(format.parse(showTime));	//放映时间
					tickets.setFilmNo(filmNo);						//影片编码
					tickets.setName(filmName);						//影片名称
					tickets.setSceneNo(hallNo);						//影厅编码
					tickets.setScene(hallName);						//影厅名称
					tickets.setSeat(ticketSeats[index]);					//座位信息
					tickets.setOrderAmount(new BigDecimal(amount).divide(new BigDecimal("100")));	//订单金额（单位：元；两位小数）
					tickets.setPrintMode(printMode);				//短信类型  1：地面取票号；2：平台凭证号；3：混合方式
					tickets.setVoucherCode(voucherCode);			//凭证号
					tickets.setPrintCode(printCode);				//取票号
					tickets.setSmsInfo(smsInfo);					//短信内容
					tickets.setAgency(Agency.ZHONGYING.name());	//出票服务机构 ZHONGYING
					tickets = ticketsService.saveOrUpdate(tickets);
					index++;
				}
			}
			json.put("code","001");
			json.put("msg","操作成功。");
			LOGGER.info("中影异步回调信息处理结束,{}", json.toJSONString());
			return json;
		}catch (Exception e){
			json.put("code","9027");
			json.put("msg","系统繁忙,请稍候重试!");
			LOGGER.error("中影异步回调异常", e);
			return json;
		}
	}

	/**
	 * 确认订单
	 * @author wulinjie
	 * @param orderCode  订单编号
	 */
	public void confirmOrderStatus(String orderCode, List<Tickets> ticketsList) throws Exception{
		Date currDate = new Date();
		@SuppressWarnings("unchecked")
		Collection<Long> ticketsIds = CollectionUtils.collect(ticketsList, TransformerUtils.invokerTransformer("getId"));
		//1.轮询未成功出票的电影票
		if( ZyClient.STATUS_FAILED.equals(ticketsList.get(0).getOrderStatus()) || ZyClient.STATUS_PAID.equals(ticketsList.get(0).getOrderStatus()) || ZyClient.STATUS_CANCEL.equals(ticketsList.get(0).getOrderStatus())){
			JSONObject json = ZyClient.queryOrderDetail(orderCode,"","","","");
			String code = json.getString("code");
			if(ZyClient.CODE_SUCCESS.equals(code)){
				JSONObject order = json.getJSONArray("orders").getJSONObject(0);
				for (Tickets tickets : ticketsList) {
					tickets = updateTickets(order,tickets);
					//如果订单已退票或者已取消，则修改电影票兑换状态
					if( ZyClient.STATUS_CANCEL.equals(tickets.getOrderStatus()) || ZyClient.STATUS_DRAWBACK.equals(tickets.getOrderStatus()) ){
						tickets = resetTickets(tickets);
					}
					//如果超过20分钟还未出票成功，则修改电影票兑换状态
					if( ZyClient.STATUS_FAILED.equals(tickets.getOrderStatus()) && currDate.after(tickets.getOrderTimeout()) ){
						tickets = resetTickets(tickets);
					}
					if( ZyClient.STATUS_PRINTED.equals(tickets.getOrderStatus()) && StringUtils.isEmpty(tickets.getPrintCode()) ){
						continue;
					}
					ticketsService.saveOrUpdate(tickets);
				}
			}
		}
		
		//2.确认未支付的订单
		if( ZyClient.STATUS_UNPAID.equals(ticketsList.get(0).getOrderStatus()) ){
			JSONObject json = ZyClient.confirmOrderStatus(orderCode);
			String code = json.getString("code");
			if(ZyClient.CODE_SUCCESS.equals(code) || ZyClient.CODE_PENDING.equals(code) || ZyClient.CODE_REPEAT_FORBIDDEN.equals(code) ){
				JSONObject orderDetail = json.getJSONObject("disysorder");
				for (Tickets tickets : ticketsList) {
					tickets = updateTickets(orderDetail, tickets);
					if( !ZyClient.STATUS_PRINTED.equals(tickets.getOrderStatus()) ){		//如果未成功出票，设置轮询超时时间
						Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.MINUTE, 20);
						tickets.setOrderTimeout(calendar.getTime());
					}
					if( ZyClient.STATUS_PRINTED.equals(tickets.getOrderStatus()) && StringUtils.isEmpty(tickets.getPrintCode()) ){
						continue;
					}
					try {
						ticketsService.saveOrUpdate(tickets);
					} catch (Exception e) {
						LOGGER.error("保存电影票异常",e);
					}
				}

			}else{
				//如果报文code为失败，则记录设置订单超时时间
				for (Tickets tickets : ticketsList) {
					tickets = ticketsService.get(tickets.getId());
					tickets.setOrderStatus(ZyClient.STATUS_FAILED);
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.MINUTE, 20);
					tickets.setOrderTimeout(calendar.getTime());
					try {
						ticketsService.saveOrUpdate(tickets);
					} catch (Exception e) {
						LOGGER.error("保存电影票异常",e);
					}
				}
			}
		}

		//3.开始发送短信
		ticketsList = ticketsService.findByIds(new ArrayList<Long>(ticketsIds));
		if (ZyClient.STATUS_PRINTED.equals(ticketsList.get(0).getOrderStatus()) && StringUtils.isNotEmpty(ticketsList.get(0).getSmsInfo())) {
			sendOriginalSuccessMessage(ticketsList);//出票成功，发送出票成功短信
		}else if( ZyClient.STATUS_FAILED.equals(ticketsList.get(0).getOrderStatus()) && currDate.after(ticketsList.get(0).getOrderTimeout()) ){
			sendFailedMessage(ticketsList);			//20分钟后还未出票，发送出票失败短信
		}else if( ZyClient.STATUS_CANCEL.equals(ticketsList.get(0).getOrderStatus()) || ZyClient.STATUS_DRAWBACK.equals(ticketsList.get(0).getOrderStatus()) ){
			sendFailedMessage(ticketsList);			//已取消或已撤单，立即发送出票失败短信
		}
	}

	/**
	 * 取消订单
	 * @param orderCode 订单号
	 * @return
	 */
	public void cancelOrder(String orderCode){
		LOGGER.info("取消中影订单,orderCode:{}", orderCode);
		List<Tickets> ticketsList = ticketsService.findByOrderCode(orderCode);
		if( CollectionUtils.isEmpty(ticketsList) ){
			LOGGER.info("中影订单不存在,orderCode:{}", orderCode);
		}
		JSONObject json = ZyClient.cancelOrder(orderCode);
		String code = json.getString("code");
		if(ZyClient.CODE_SUCCESS.equals(code)){
			for (Tickets tickets : ticketsList) {
				tickets.setOrderStatus(ZyClient.STATUS_CANCEL);
				tickets = resetTickets(tickets);
				ticketsService.saveOrUpdate(tickets);
			}
		}
	}

	/**
	 * 对http params 解码
	 * @param request
	 * @return
	 */
	public Map<String,String> decodeRequestParams(HttpServletRequest request){
		Map<String, String> params = new HashMap<String, String>();
		try{
			Enumeration<String> paramNames =  request.getParameterNames();
			while (paramNames.hasMoreElements()){
				String paramName = paramNames.nextElement();
				String paramValue = request.getParameter(paramName);
				params.put(paramName,  URLDecoder.decode(paramValue,"utf-8"));
			}
		}catch (Exception e){
			LOGGER.error("解码request参数异常", e);
		}
		return params;
	}

	/**
	 * 更新订单信息
	 * @author wulinjie
	 * @param json
	 * @param tickets
	 */
	private Tickets updateTickets(JSONObject json, Tickets tickets) throws Exception{
		//String orderCode = json.getString("orderCode");		//平台订单号
		String orderTime = json.getString("orderTime");		//下单时间
		String cinemaNo =  json.getString("cinemaNo");			//影院编码
		String cinemaName = json.getString("cinemaName");		//影院名称
		String exchangeAddr =json.getString("exchangeAddr");	//取票地址
		String showTime = json.getString("showTime");			//放映时间
		String filmNo = json.getString("filmNo");				//影片编码
		String filmName = json.getString("filmName");			//影片名称
		//String showType = json.getString("showType");			//影片放映类型：2D,3D,IMAX2D，IMAX3D，DMAX2D，DMAX3D,其他
		String hallNo = json.getString("hallNo");				//影厅编码
		String hallName = json.getString("hallName");			//影厅名称
		//TODO 后期需要中影添加一个确认订单接口的座位信息
		//String seatName = json.getString("seatName");			//座位信息
		String amount = json.getString("amount");				//订单金额（单位：元；两位小数）
		String printMode = json.getString("printMode");		//短信类型
		String goodsType = json.getString("goodsType");		//商品类型
		String voucherCode = json.getString("voucherCode");	//凭证号
		String printCode = json.getString("printCode");		//取票号
		String status = json.getString("status");				//订单状态

		tickets = ticketsService.get(tickets.getId());
		tickets.setOrderCreatedDate( StringUtils.isNotEmpty(orderTime)?format.parse(orderTime) : null);	//下单时间
		tickets.setCinemaNo(cinemaNo);							//影院编码
		tickets.setPerformAddress(cinemaName);					//影院名称
		tickets.setPerformTime(format.parse(showTime));			//放映时间
		tickets.setPerformAddressDetail(exchangeAddr);			//详细地址
		tickets.setFilmNo(filmNo);								//影片编码
		tickets.setName(filmName);								//影片名称
		//tickets.setLanguage(showType);							//影片放映类型：2D,3D,IMAX2D，IMAX3D，DMAX2D，DMAX3D,其他
		tickets.setSceneNo(hallNo);								//影厅编码
		tickets.setScene(hallName);								//影厅名称
		tickets.setOrderAmount(new BigDecimal(amount).divide(new BigDecimal("100") ));			//订单金额（单位：元；两位小数）
		tickets.setPrintMode(printMode);						//短信类型  1：地面取票号；2：平台凭证号；3：混合方式
		tickets.setVoucherCode(voucherCode);					//凭证号
		tickets.setGoodsType(goodsType);						//商品类型
		tickets.setPrintCode(printCode);						//取票号
		tickets.setOrderStatus(status);							//订单状态（1：未支付 2：已取消 3：已支付 4：出票成功 5：出票失败 6：已退票）
		tickets.setAgency(Agency.ZHONGYING.name());			//出票服务机构 ZHONGYING
		return tickets;
	}

	/**
	 * 清除座位信息
	 * @author wulinjie
	 * @param tickets  电影票
	 * @return
	 */
	private Tickets resetTickets(Tickets tickets){
		tickets.setFilmNo(null);
		tickets.setName(null);
		tickets.setPerformTime(null);
		tickets.setPerformAddress(null);
		tickets.setPerformAddressDetail(null);
		tickets.setCinemaNo(null);
		tickets.setSceneNo(null);
		tickets.setScene(null);
		tickets.setSeat(null);
		tickets.setLanguage(null);
		tickets.setStatus(Status.未兑换.name());			//初始化为"未兑换"
		tickets.setOrderCode(null);							//清除订单号
		tickets.setOrderAmount(null);
		tickets.setPrintMode(null);
		tickets.setGoodsType(null);
		tickets.setVoucherCode(null);
		tickets.setPrintCode(null);
		tickets.setSmsInfo(null);
		tickets.setAgency(null);
		return tickets;
	}

	/**
	 * 通知客户出票成功
	 * @author wulinjie
	 * @param ticketsList
	 */
	/*private void sendSuccessMessage(List<Tickets> ticketsList){
		LOGGER.info("中影出票成功，开始发送短信......");
		Tickets tickets = ticketsList.get(0);
		String voucherCode = tickets.getVoucherCode();	//凭证号
		String printCode = tickets.getPrintCode();		//取票号
		String printMode = tickets.getPrintMode();		//取票方式
		if( StringUtils.isNotEmpty(printMode) && StringUtils.isNotEmpty(printCode)){
			String[] qphCode =printCode.split(",");
			if("1".equals(printMode)){		//地面取票号
				printCode = "取票号:" + qphCode[0] + (!printCode.contains(",") ? "" : ",验证码:" + qphCode[1]);
			}else if("2".equals(printMode)){	//平台凭证号
				printCode = "凭证号:" + voucherCode;
			}else if("3".equals(printMode)){	//混合方式
				printCode = "凭证号:" + voucherCode + ",取票号:" + qphCode[0] + (!printCode.contains(",") ? "" : ",验证码:" + qphCode[1]);
			}

		}

		StringBuffer msg = new StringBuffer();
		msg.append("您已成功预订影片").append("《").append(tickets.getName()).append("》,").append(format.format(tickets.getPerformTime())).append(",")
				.append(tickets.getPerformAddress()).append(",").append(tickets.getScene()).append(",").append(ticketsList.size()).append("张（").append(tickets.getSeat()).append("）,")
				.append(printCode).append(",").append("请开场前至电影院取票机或前台取票。如有疑问请拨打客服热线：400-6196-828");
		User user = customerService.findOne(tickets.getUserId());
		messageService.sendShortMessage(user.getMobile(), msg.toString(), Company.易联天下);
	}*/

	/**
	 * 通知客户出票成功(中影原始短信)
	 * @author wulinjie
	 * @param ticketsList
	 */
	private void sendOriginalSuccessMessage(List<Tickets> ticketsList){
		LOGGER.info("中影出票成功，开始发送短信......");
		Tickets tickets = ticketsList.get(0);
		String smsInfo = tickets.getSmsInfo();
		String flag = StringUtils.substring(smsInfo, smsInfo.length() - 1);
		if("。".equals(flag)){
			smsInfo += "如有疑问请拨打客服热线：400-6196-828";
		}else{
			smsInfo += "。如有疑问请拨打客服热线：400-6196-828";
		}
		User user = customerService.findOne(tickets.getUserId());
		messageService.sendShortMessage(user.getMobile(), smsInfo, Company.易联天下);
	}

	/**
	 * 通知客户出票失败
	 * @author wulinjie
	 * @param ticketsList
	 */
	private void sendFailedMessage(List<Tickets> ticketsList){
		Tickets tickets = ticketsList.get(0);
		//TODO 根据影片编码修改
		StringBuffer msg = new StringBuffer();
		msg.append("非常抱歉，您的影片《叶问三》座位未成功锁定，请再次选座。 如有疑问请拨打客服热线：400-6196-828");
		User user = customerService.findOne(tickets.getUserId());
		messageService.sendShortMessage(user.getMobile(), msg.toString(), Company.易联天下);

	}

}
