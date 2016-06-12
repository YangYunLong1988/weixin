package com.snowstore.diana.controller;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.snowstore.diana.common.Calendars;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Order.Status;
import com.snowstore.diana.domain.TicketCode;
import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.service.thirdapp.ZyClient;
import com.snowstore.diana.vo.MovieCodeVo;
import com.snowstore.diana.vo.MovieTicketVo;
import com.snowstore.diana.vo.Result;
import com.snowstore.log.annotation.UserLog;

@Controller
@RequestMapping("/movie")
public class MovieCodeController {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private TicketsService ticketsService;
	@Autowired
	private MovieCodeService movieCodeService;
	@Autowired
	private MovieCodeServiceV2 movieCodeServiceV2;
	@Autowired
	private OrderService orderService;
	@Autowired
	private TicketCodeService ticketCodeService;
	@Autowired
	private SettingsService settingsService;

	@RequestMapping("codeList")
	@UserLog(remark = "【电影】跳转至 电影兑换码页面")
	public String codeList(Model model, Long orderId, Long noCoupon) {
		model.addAttribute("orderId", orderId);
		Order order = orderService.getUserOrderById(orderId);
		if (null == order) {
			return "movie_code_list";
		}
		model.addAttribute("orderAmount", order.getAmount());
		if (Order.Status.待付款.toString().equals(order.getStatus())) {
			model.addAttribute("pay", "0");
			model.addAttribute("orderTime", new DateTool().format("yyyy-MM-dd HH:mm:ss", order.getCreatedDate()));
		} else if (Order.Status.已付款.toString().equals(order.getStatus())) {
			model.addAttribute("pay", "1");
		} else if (Order.Status.已撤单.toString().equals(order.getStatus())) {
			model.addAttribute("pay", "2");
		} else if (Order.Status.付款失败.toString().equals(order.getStatus())) {
			model.addAttribute("pay", "3");
		} else if (Order.Status.付款中.toString().equals(order.getStatus())) {
			model.addAttribute("pay", "4");
		}

		Settings settings = settingsService.getByKey("filmLogin");
		model.addAttribute("ticketConvertToGift", settingsService.getByKey("ticketConvertToGift").getValue());
		model.addAttribute("noCoupon", noCoupon == null ? 0 : noCoupon);
		model.addAttribute("filmLogin", settings.getValue());
		return "movie_code_list";
	}

	/**
	 * 获取电影票兑换码列表
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("getCodes")
	@ResponseBody
	@UserLog(remark = "【电影】加载显示电影兑换码")
	public Result<LinkedList<MovieCodeVo>> getCodes(Long orderId) {
		LinkedList<MovieCodeVo> list = new LinkedList<>();
		Result<LinkedList<MovieCodeVo>> result = new Result<LinkedList<MovieCodeVo>>();
		List<Tickets> tickets = ticketsService.findTicketByOrderId(orderId);
		tickets = (List<Tickets>)CollectionUtils.select(tickets, new org.apache.commons.collections.Predicate() {
			public boolean evaluate(Object arg0) {
				Tickets ti = (Tickets) arg0;
				if (null == ti.getSubOrder()) {//没有生成子订单
					return true;
				} else {// 生成子订单
					return null == ti.getSubOrder().getUserGift();
				}
			}
		});
		if (tickets != null && tickets.size() > 0) {
			Iterator<Tickets> it = tickets.iterator();
			while (it.hasNext()) {
				Tickets ticket = it.next();
				// 检查未兑换的电影票是否已经出票
				MovieCodeVo vo = new MovieCodeVo();
				vo.setId(ticket.getId());
				vo.setCode(ticket.getCode());
				if (Tickets.Status.已兑换.name().equals(ticket.getStatus())) {
					vo.setUsed(Boolean.TRUE);
					MovieTicketVo tvo = new MovieTicketVo();
					tvo.setId(ticket.getOrderCode());
					tvo.setMovie(ticket.getName());
					if (null != ticket.getOrderStatus() && !ZyClient.STATUS_PRINTED.equals(ticket.getOrderStatus())) {
						tvo.setSuccess(false);
						tvo.setTime("您的座位正在努力锁定中.....");
					} else if (null != ticket.getPerformTime()) {
						tvo.setTime("场次：" + new DateTool().format("yyyy-MM-dd HH:mm:ss", ticket.getPerformTime()));
						tvo.setSuccess(true);
					} else {
						tvo.setTime("");
					}
					Order order = orderService.getUserOrderById(orderId);
					if (Order.Status.已付款.name().equals(order.getStatus())) {
						TicketCode tc = ticketCodeService.getTicketCodeService(ticket.getId());
						if (tc != null) {
							tvo.setTicket3rdCode(tc.getTicketCode());
						} else {
							ticketCodeService.updateTicketCodeByTicketService(ticket);// 重新获取兑换码
							tc = ticketCodeService.getTicketCodeService(ticket.getId());
							tvo.setTicket3rdCode(tc != null ? tc.getTicketCode() : "&nbsp;");
						}
					} else {
						tvo.setTicket3rdCode("支付后显示兑换码。");
					}
					tvo.setTicket3rd(ticket.getAgency());
					vo.setTicket(tvo);
				} else {
					vo.setUsed(Boolean.FALSE);
				}
				vo.setFromShared(Boolean.FALSE);
				vo.setTimeout(ticket.getTimeOut() == null ? 0 : ticket.getTimeOut().getTime());
				vo.setMemo(ticket.getConsume());
				list.add(vo);
			}
		}
		result.setType(Result.Type.SUCCESS);
		result.setData(list);
		return result;
	}

	/**
	 * 电影票信息
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("ticket")
	@UserLog(remark = "【电影】跳转至电影票信息页面")
	public String ticket(Model model, long id) {
		model.addAttribute("id", id);
		return "movie_ticket";
	}

	/**
	 * 获取电影票信息
	 * 
	 * @author wulinjie
	 * @param id
	 * @return
	 */
	@RequestMapping("getTicket")
	@ResponseBody
	@UserLog(remark = "【电影】加载电影票信息")
	public Result<MovieTicketVo> getTicket(long id) {
		Result<MovieTicketVo> result = new Result<>();
		Tickets tickets = ticketsService.getUserTicketsById(id);
		if (null == tickets) {
			result.setType(Result.Type.FAILURE);
			result.addMessage("未找到相关信息。");
			return result;
		}
		String voucherCode = tickets.getVoucherCode(); // 凭证号
		String printCode = tickets.getPrintCode(); // 取票号
		String printMode = tickets.getPrintMode(); // 取票方式
		if (StringUtils.isNotEmpty(printMode) && StringUtils.isNotEmpty(printCode)) {
			String[] qphCode = printCode.split(",");
			if ("1".equals(printMode)) { // 地面取票号
				printCode = "<div><span>取票号</span>" + qphCode[0] + (!printCode.contains(",") ? "" : "</div><div><span>验证码</span>" + qphCode[1]) + "</div>";
			} else if ("2".equals(printMode)) { // 平台凭证号
				printCode = "<div><span>凭证号</span>" + voucherCode + "</div>";
			} else if ("3".equals(printMode)) { // 混合方式
				printCode = "<div><span>凭证号</span>" + voucherCode + "</div><div><span>取票号</span>" + qphCode[0] + (!printCode.contains(",") ? "" : "</div><div><span>验证码</span>" + qphCode[1]) + "</div>";
			}
		}
		if (StringUtils.isEmpty(tickets.getSeat())) { // 如果电影票座位未同步，则调用同步方法
			movieCodeService.sychOrderResult();
		}
		MovieTicketVo vo = new MovieTicketVo();
		vo.setId(tickets.getOrderCode());
		vo.setMovie(tickets.getName());
		vo.setType(tickets.getLanguage());
		vo.setCinema(tickets.getPerformAddress());
		vo.setScene(tickets.getScene());
		vo.setSeat(tickets.getSeat());
		vo.setTime(Calendars.format(tickets.getPerformTime(), "yyyy-MM-dd HH:mm:ss"));
		vo.setTicketMachine("A4");
		vo.setTicketCode("54E678");
		vo.setTicketCaptcha("OK");
		vo.setChecked(null != printCode);
		vo.setSuccess(null != printCode);
		vo.setTicketExtractInfo(printCode);
		vo.setPerformAddressDetail(tickets.getPerformAddressDetail());
		vo.setSmsInfo(tickets.getSmsInfo());

		result.setType(Result.Type.SUCCESS);
		result.setData(vo);
		return result;
	}

	@RequestMapping("/getLoginUrl")
	@ResponseBody
	@UserLog(remark = "【电影】返回电影票务服务机构URL")
	public JSONObject getLoginUrl(HttpServletRequest request, HttpSession session) {
		return movieCodeServiceV2.getLoginUrl(request, session);
	}

	@RequestMapping(value = "/getOrder", method = RequestMethod.GET)
	@UserLog(remark = "【接口】同步返回出票信息")
	public String getOrder(HttpServletRequest req, HttpSession session, Model model) {
		String orderId = (String) session.getAttribute("orderId");
		if (StringUtils.isEmpty(orderId)) {
			return "redirect:/order/findorders";
		}
		try {
			movieCodeServiceV2.sychOrder(req); // 开始同步订单和座位信息
			Order order = orderService.getUserOrderById(Long.parseLong(orderId));
			if (Status.待付款 == Status.valueOf(order.getStatus())) { // 如果是待付款订单，则跳转到易票付款页面
				return "redirect:/product/buyTicket/pay/" + orderId;
			} else if (Status.已付款 == Status.valueOf(order.getStatus())) {
				movieCodeServiceV2.confirmOrderStatus(order); // 确认订单信息
			}
			return "redirect:/movie/codeList?orderId=" + orderId;
		} catch (Exception e) {
			model.addAttribute("message", "同步座位信息出错");
			LOGGER.error("同步座位信息出错", e);
			return "error502";
		} finally {
			session.removeAttribute("orderId");
			session.removeAttribute("ticketsIds");
		}
	}

	@RequestMapping(value = { "/ysOrderCallBack" }, method = org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	@UserLog(remark = "【接口】异步返回出票信息")
	public JSONObject getOrderInfo(HttpServletRequest req, HttpSession session) {
		return movieCodeServiceV2.asychOrder(req);
	}

	/**
	 * 获取渠道兑换码
	 * 
	 * @author mingzhi.dong
	 * @date 2016年2月26日
	 * @param orderId
	 * @param ticketsIds
	 * @param type
	 * @return
	 */
	@RequestMapping("/exchange")
	@ResponseBody
	public Result<String> exchange(Long orderId, Long[] ticketsIds, String type) {
		try {
			Order order = orderService.getUserOrderById(orderId);

			for (Long ticketsId : ticketsIds) {
				Tickets tickets = ticketsService.get(ticketsId);
				if(tickets != null && tickets.hasGift()){
					return new Result<String>(Result.Type.WARNING, "该兑换码已转为礼品。");
				}
			}

			if (Order.Status.已付款.name().equals(order.getStatus())) {// 已付款
				return ticketCodeService.getCodeToTicketCodeService(Arrays.asList(ticketsIds), type, orderId);
			} else if (Order.Status.待付款.name().equals(order.getStatus())) {// 待付款,
				return new Result<String>(Result.Type.FAILURE, "请先完成支付。");
			} else if (Order.Status.付款中.name().equals(order.getStatus())) {// 付款中
				return new Result<String>(Result.Type.WARNING, "付款中请稍后获取兑换码。");
			} else if (Order.Status.付款失败.name().equals(order.getStatus())) {// 付款失败
				return new Result<String>(Result.Type.WARNING, "付款失败，无法获取兑换码。");
			} else {// 已撤单
				return new Result<String>(Result.Type.WARNING, "订单已失效。");
			}
		} catch (ObjectOptimisticLockingFailureException c) {
			LOGGER.error("获取渠道兑换码乐观锁....", c);
			return new Result<String>(Result.Type.WARNING, "目前操作的用户较多，我们会尽快处理，请稍候再试。");
		} catch (Exception e) {
			LOGGER.error("获取渠道兑换码异常....", e);
			return new Result<String>(Result.Type.WARNING, "目前操作的用户较多，我们会尽快处理，请稍候再试。");
		}
	}
}
