package com.snowstore.diana.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snowstore.diana.common.Constants;
import com.snowstore.diana.common.DianaConstants;
import com.snowstore.diana.domain.Channel;
import com.snowstore.diana.domain.Logistics;
import com.snowstore.diana.domain.Logistics.LogisticsStatus;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Package;
import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.domain.UserGift;
import com.snowstore.diana.service.ChannelService;
import com.snowstore.diana.service.CustomerService;
import com.snowstore.diana.service.OrderService;
import com.snowstore.diana.service.PackageService;
import com.snowstore.diana.service.TicketsService;
import com.snowstore.diana.service.UserGiftService;
import com.snowstore.diana.service.logistics.LogisticsService;
import com.snowstore.diana.utils.SignUtil;
import com.snowstore.diana.vo.OrderResultVo;
import com.snowstore.diana.vo.OrderVo;
import com.snowstore.diana.vo.PageFormVo;
import com.snowstore.diana.vo.PageTables;
import com.snowstore.diana.vo.UserGiftVo;
import com.snowstore.log.annotation.UserLog;

@Controller
@RequestMapping(value = "/order")
public class OrderController {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private CustomerService customerService;

	@Autowired

	private OrderService orderService;
	@Autowired
	private TicketsService ticketsService;

	@Autowired
	private UserGiftService userGiftService;

	@Autowired
	private PackageService packageService;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private LogisticsService logisticsService;

	/** 跳转订单列表页面 */
	@RequestMapping("/orderList")
	@UserLog(remark = "【后台订单管理】订单-->跳转订单列表页面")
	public String orderList(HttpServletRequest request, String type, Model model) {
		// 后台管理员显示渠道查询
		User user = customerService.getCurrentUser();
		if (User.Role.ADMIN.name().equals(user.getRole())) {
			model.addAttribute("channelList", channelService.getTopChannelByCurrUser());
		}
		if (null != type && type.equals("vocal")) {
			return "vocalOrderList";
		}
		return "movieOrderList";
	}

	/** 获取订单数据 */
	@RequestMapping("/loadOrderList")
	@ResponseBody
	@UserLog(remark = "【后台订单管理】订单-->根据条件获取订单数据")
	public PageTables<OrderVo> loadOrderList(HttpServletRequest request, Integer draw, Integer start, Integer length, @RequestParam(defaultValue = "movie", value = "type") String type) {
		PageTables<OrderVo> pageTables = new PageTables<OrderVo>();

		try {
			OrderVo orderVo = createOrderVo(request, type);
			PageFormVo form = new PageFormVo();
			// 计算当前页
			form.setPage(start == 0 ? 1 : start / length + 1);
			form.setRows(length);
			Page<Order> page = orderService.findOrderList(orderVo, form);
			// 封装分页对象数据
			List<OrderVo> orderList = orderService.getOrderVo(page.getContent());
			pageTables.setDraw(draw);
			pageTables.setRecordsTotal(page.getTotalElements());
			pageTables.setRecordsFiltered(page.getTotalElements());
			pageTables.setData(orderList);
		} catch (Exception e) {
			LOGGER.error("获取订单异常", e);
		}

		return pageTables;
	}

	/**
	 * 创建订单查询VO
	 * 
	 * @param request
	 * @param type
	 * @return
	 */
	private OrderVo createOrderVo(HttpServletRequest request, String type) {
		String status = request.getParameter("status");
		String mobile = request.getParameter("mobile");
		String productName = request.getParameter("productName");
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		OrderVo orderVo = new OrderVo(mobile, productName, status, beginDate, endDate, type);
		String platform = request.getParameter("platform");
		if (StringUtils.isNotEmpty(platform)) {
			if (StringUtils.isNotEmpty(request.getParameter("subPlatform"))) {
				platform = request.getParameter("subPlatform");
			}
			orderVo.setPlatformId(Long.parseLong(platform));
		}
		return orderVo;
	}

	@RequestMapping("/loadOrderList/channel")
	@ResponseBody
	public Object loadOrderListChannel(HttpServletRequest req) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		// 校验请求参数
		if (verifyParam(req, result)) {
			return result;
		}else{
			customerService.pushUserToSecurity(customerService.getChannelUser(req.getParameter("platform")));
		}
		PageTables<OrderResultVo> data = null;
		try {
			String mobile = req.getParameter("mobile");
			String beginDate = req.getParameter("beginDate");
			String endDate = req.getParameter("endDate");
			Integer pageNo = Integer.parseInt(req.getParameter("pageNo")==null?"1":req.getParameter("pageNo"));
			Integer pageSize = Integer.parseInt(req.getParameter("pageSize")==null?"30":req.getParameter("pageSize"));
			String platform=req.getParameter("platform");
			// 一页显示30数据
			Integer length = pageSize;
			PageFormVo form = new PageFormVo();
			// 计算当前页
			form.setPage(pageNo);
			form.setRows(length);
			data = orderService.queryOrder(mobile, beginDate, endDate, platform, pageNo, pageSize);
			result.put("data", data);
			

		} catch (Exception e) {
			LOGGER.error("获取订单异常", e);
			result.put("status", Constants.STATUS_ERROR);
			result.put("message", "获取订单异常");
			return result;
		}
		result.put("status", Constants.STATUS_SUCCESS);
		result.put("message", "成功");
		result.put("pageNo", data.getDraw());
		result.put("size",data.getLength());
		result.put("data", data.getData());
		return result;
	}

	/**
	 * 校验请求参数 true 校验失败 false 校验成功
	 * 
	 * @author XieZG
	 * @Date:2016年1月5日上午11:23:57
	 * @param req
	 * @param result
	 * @return
	 * @throws Exception
	 */
	private boolean verifyParam(HttpServletRequest req, Map<String, Object> result) throws Exception {
		String[] SIGN_KEYS = new String[] { "mobile", "platform", "timestamp", "sign", "pageNo","pageSize","beginDate", "endDate" };// 需要验证的参数
		Map<String, String> parameterMap = new HashMap<String, String>();
		for (String key : SIGN_KEYS) {
			String value = req.getParameter(key);
			if (!StringUtils.isEmpty(value)) {
				parameterMap.put(key, req.getParameter(key));
			} else {
			}

		}
		String sign = parameterMap.get("sign");
		parameterMap.remove("sign");
		String dianaSign = SignUtil.signature(System.getProperty("diana.sign.secret"), parameterMap);
		if (!dianaSign.toLowerCase().equals(sign)) {
			LOGGER.info("签名失败:[{}]", dianaSign.toLowerCase());
			result.put("message", "签名错误");
			result.put("status", Constants.STATUS_ERROR);
			return true;
		}

		String timeOfRequest = req.getParameter("timestamp");
		if (!"pro".equals(System.getProperty("diana.env"))) {
			LOGGER.info("非生产环境跳过时间戳校验!");
		} else if (Math.abs(System.currentTimeMillis() - Long.parseLong(timeOfRequest)) >= 300000) {
			// 时间差超过六秒
			Date now = new Date();
			Date dateOfRequest = new Date(Long.parseLong(timeOfRequest));
			result.put("status", Constants.STATUS_ERROR);
			result.put("message", "请求时间与当前时间差过长");
			LOGGER.info("当前时间:[{}]", now.toString());
			LOGGER.info("请求时间:[{}]", dateOfRequest.toString());
			return true;
		}

		String orginPlatform = (String) req.getSession().getAttribute(DianaConstants.SESSION_PLATFORM);
		if (null == orginPlatform) {
			String platform = req.getParameter("platform");
			Channel channel = channelService.getChannelByCode(platform);
			if(null == channel){
				LOGGER.info("该渠道不存在:[{}]", platform);
				return true;
			}
			orginPlatform = channel.getCode();
			req.getSession().setAttribute(DianaConstants.SESSION_PLATFORM, orginPlatform);
		}
		return false;

	}

	/** 跳转订单出票记录页面 */
	@RequestMapping("/orderTicketList/{orderId}/{type}")
	@UserLog(remark = "【订单管理】查看电影出票")
	public String orderList(Model model, @PathVariable Long orderId, @PathVariable String type) {
		model.addAttribute("orderId", orderId);
		model.addAttribute("type", type);
		return "userTicketList";
	}

	/**
	 * 获取订单出票记录数据
	 * 
	 * @param request
	 * @param draw
	 * @param start
	 * @param length
	 * @param orderId
	 * @return
	 */
	@RequestMapping("/loadOrderTickets")
	@ResponseBody
	@UserLog(remark = "【后台订单列表按钮】加载电影出票信息")
	public PageTables<Tickets> loadOrderTickets(HttpServletRequest request, Integer draw, Integer start, Integer length, Long orderId) {

		PageFormVo form = new PageFormVo();
		PageTables<Tickets> pageTables = new PageTables<Tickets>();

		try {
			form.setPage(start == 0 ? 1 : start / length + 1);
			form.setRows(length<0?10:length);
			Page<Tickets> page = ticketsService.findByOrderId(orderId, Tickets.Status.已兑换.name(), form);
			List<Tickets> ticketList = page.getContent();
			pageTables.setDraw(draw);
			pageTables.setRecordsTotal(page.getTotalElements());
			pageTables.setRecordsFiltered(page.getTotalElements());
			pageTables.setData(ticketList);
		} catch (Exception e) {
			LOGGER.error("获取订单出票记录异常", e);
		}

		return pageTables;
	}

	/** 跳转订单礼品记录页面 */
	@RequestMapping("/orderGiftList/{orderId}")
	@UserLog(remark = "【后台订单列表按钮】跳转订单礼品记录页面")
	public String orderGiftList(Model model, @PathVariable Long orderId) {
		model.addAttribute("orderId", orderId);
		return "userGiftList";
	}

	/**
	 * 获取订单礼品记录数据
	 * 
	 * @param request
	 * @param draw
	 * @param start
	 * @param length
	 * @param orderId
	 * @return
	 */
	@RequestMapping("/loadOrderGift")
	@ResponseBody
	@UserLog(remark = "【后台订单列表按钮】返回订单礼品记录信息")
	public PageTables<UserGiftVo> loadOrderGift(HttpServletRequest request, Integer draw, Integer start, Integer length, Long orderId) {

		PageFormVo form = new PageFormVo();
		PageTables<UserGiftVo> pageTables = new PageTables<UserGiftVo>();
		try {
			// 计算当前页
			form.setPage(start == 0 ? 1 : start / length + 1);
			form.setRows(length);
			List<UserGift> userGiftList = userGiftService.findByOrderId(orderId);
			List<Order> suborderList=orderService.findBySubOrderService(orderId);
			for (Order tmp : suborderList) {
				if (!userGiftService.findByOrderId(tmp.getId()).isEmpty()) {
					userGiftList.add(userGiftService.findByOrderId(tmp.getId()).get(0));
				}
			}
			List<UserGiftVo> list = new ArrayList<UserGiftVo>();

			// 封闭Vo对象
			UserGiftVo userGiftVo = null;
			for (UserGift userGift : userGiftList) {
				userGiftVo = new UserGiftVo();
				Package pack = packageService.findById(userGift.getRefPackage());
				userGiftVo.setGiftName(userGift.getGiftName());// 礼品名称
				userGiftVo.setMobile(pack.getMobile());

				String stringAddress="";
				if(pack.getProvince()!=null){
					stringAddress=stringAddress+pack.getProvince()+' ';
				}
				if(pack.getCity()!=null){
					stringAddress=stringAddress+pack.getCity()+' ';
				}
				if(pack.getArea()!=null){
					stringAddress=stringAddress+pack.getArea()+' ';
				}
				if(pack.getAddress()!=null){
					stringAddress=stringAddress+pack.getAddress();
				}
				userGiftVo.setAddress(stringAddress);
				

				userGiftVo.setRecipients(pack.getRecipients());
				Logistics logistics = logisticsService.findOneByOrderId(Long.valueOf(orderId));
				if (logistics != null) {
					userGiftVo.setLogisticsCompany(logistics.getCompany());
					userGiftVo.setLogisticsSn(logistics.getSn());
					LogisticsStatus status = logistics.getStatus();
					userGiftVo.setLogisticsStatus(status == null ? null : status.name());
				}
				list.add(userGiftVo);
			}
			// 封装分页对象数据
			pageTables.setDraw(draw);
			pageTables.setRecordsTotal(Long.valueOf(list.size()));
			pageTables.setRecordsFiltered(Long.valueOf(list.size()));
			pageTables.setData(list);
		} catch (Exception e) {
			LOGGER.error("获取订单礼品记录信息异常", e);
		}

		return pageTables;
	}

	/** 订单导出 */
	@RequestMapping("exportOrderExcel")
	@ResponseBody
	@UserLog(remark = "【后台订单列表按钮】订单导出")
	public void exportOrderExcel(HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "movie", value = "type") String type) {
		OrderVo orderVo = createOrderVo(request, type);
		orderService.exportOrder(request, response, orderVo);
	}

}
