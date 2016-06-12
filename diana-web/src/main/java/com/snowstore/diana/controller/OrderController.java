/**  
 * @Title: OrderController.java
 * @Package com.snowstore.diana.controller
 * @Description: (订单)
 * @author wangyunhao  
 * @date 2015年10月22日 上午10:01:06
 * @version V1.0  
 */
package com.snowstore.diana.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snowstore.diana.domain.Order;
import com.snowstore.diana.service.OrderService;
import com.snowstore.diana.vo.AddressVo;
import com.snowstore.diana.vo.Result;
import com.snowstore.diana.vo.TicketAreaVo;
import com.snowstore.diana.vo.TicketOrderVo;
import com.snowstore.diana.vo.TicketTimeVo;
import com.snowstore.log.annotation.UserLog;

/**
 * @ClassName: OrderController
 * @Description: (订单)
 * @author wangyunhao
 * @date 2015年10月22日 上午10:01:06
 */
@RequestMapping("/order/")
@Controller
public class OrderController {

	@Autowired
	private OrderService orderService;

	@RequestMapping("findorders")
	@UserLog(remark = "【前台订单】跳转至订单列表页面")
	public String findOrders(Model model) {
		model.addAttribute("orders", orderService.findOrders());
		model.addAttribute("date", new DateTool());
		return "order_list";
	}

	@RequestMapping("giftCard")
	@UserLog(remark = "【前台】跳转至礼品卡页面")
	public String giftCard(Model model) {
		return "gift_card";
	}

	@RequestMapping("findOrderDetail/{id}")
	@UserLog(remark = "【前台订单】跳转至订单详情页面")
	public String findOrderDetail(Model model, @PathVariable Long id) {
		Map<String, Object> detailMap = orderService.findOrderById(id);
		model.addAttribute("date", new DateTool());
		for (Map.Entry<String, Object> entry : detailMap.entrySet()) {
			model.addAttribute(entry.getKey(), entry.getValue());
		}
		return "forward:/movie/codeList?orderId=" + id;
	}

	/**
	 * 门票订单
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("orderTicket")
	@UserLog(remark = "【前台订单】暂无使用")
	public String orderTicket(Model model, long id) {
		model.addAttribute("id", id);
		return "order_ticket";
	}

	/**
	 * 获取订单门票
	 * 
	 * @param id
	 *            订单ID
	 * @return
	 */
	@RequestMapping("getOrderTicket")
	@ResponseBody
	@UserLog(remark = "【前台订单】暂无使用")
	public Result<TicketOrderVo> generateOrder(long id) {
		TicketOrderVo vo = new TicketOrderVo();
		vo.setProductId(id);
		vo.setName("2016年莫文蔚看看世界巡回演唱会—上海站演唱会");
		vo.setScene("虹口体育场（上海市）");
		TicketTimeVo timeVo = new TicketTimeVo();
		timeVo.setKey("0");
		timeVo.setValue("场次2015-11-11");
		timeVo.setValid(true);
		vo.setTime(timeVo);
		TicketAreaVo areaVo = new TicketAreaVo();
		areaVo.setKey("0");
		areaVo.setValue("A区");
		areaVo.setPrice(288);
		areaVo.setValid(true);
		vo.setArea(areaVo);
		vo.setCount(3);
		vo.setMoney(areaVo.getPrice() * vo.getCount());
		vo.setDelivery(true);
		AddressVo address = new AddressVo();
		address.setPerson("tom");
		address.setPhone("12345678901");
		address.setAddress("上海市长宁区天山路上海市长宁区天山路上海市长宁区天山路上海市长宁区天山路");
		vo.setDeliveryAddress(address);
		vo.setDiyAddress("上海市长宁区天山路");
		vo.setDiyTime("上午10点-下午5点30");
		Result<TicketOrderVo> result = new Result<>();
		result.setType(Result.Type.SUCCESS);
		result.setData(vo);
		return result;
	}

	/**
	 * 无礼品提交订单
	 * 
	 * @param productId
	 * @return
	 */
	@RequestMapping("generateOrder")
	@ResponseBody
	@UserLog(remark = "【前台订单】有礼品提交订单")
	public Result<Order> generateOrder(Long productId, HttpServletRequest req) {
		return orderService.generateOrder(productId, req);
	}

	/**
	 * 无礼品提交订单
	 * 
	 * @param productId
	 * @return
	 */
	@RequestMapping("generateOrderPage")
	@UserLog(remark = "【前台订单】无礼品提交订单")
	public String generateOrderPage(Long productId, HttpServletRequest req) {
		Result<Order> result = orderService.generateOrder(productId, req);
		if (result.success()) {
			// return "redirect:/product/buyTicket/pay/" +
			// result.getData().getId();
			return "redirect:/order/findOrderDetail/" + result.getData().getId();
		} else if (result.warning()) {
			return "redirect:/order/unpayOrder/" + result.getData().getId();
		} else {
			return "error502";
		}
	}

	@RequestMapping("unpayOrder/{orderId}")
	@UserLog(remark = "【前台订单】有未付款订单")
	public String unpayOrderDetail(HttpServletRequest req, Model model, @PathVariable Long orderId) {
		req.getSession().removeAttribute("ruleAmount");
		req.getSession().removeAttribute("ruleId");
		model.addAttribute("orderId", orderId);
		Order order = orderService.getUserOrderById(orderId);
		model.addAttribute("orderAmount", order.getAmount());
		model.addAttribute("pay", "0");
		model.addAttribute("orderTime", new DateTool().format("yyyy-MM-dd HH:mm:ss", order.getCreatedDate()));
		model.addAttribute("tips", "当前订单未支付,请先支付。");
		return "movie_code_list";
	}

	/**
	 * 前台触发撤单
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("cancle")
	@ResponseBody
	@UserLog(remark = "【前台订单】前台触发撤单")
	public Result<String> canceOrder(Long id) {
		Result<String> result = new Result<String>(Result.Type.FAILURE);
		if (null == id) {
			return result;
		}
		Order order = orderService.getUserOrderById(id);
		if (null == order) {
			return result;
		}
		if (Order.Status.待付款.name().equals(order.getStatus())) {
			order = orderService.cancleOrder(order);
			result.setType(Result.Type.SUCCESS);
		}else if(Order.Status.付款中.name().equals(order.getStatus())){
			result.addMessage("该订单正在支付中，请等待");
		}
		return result;
	}
}
