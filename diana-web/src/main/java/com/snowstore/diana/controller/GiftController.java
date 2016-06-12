/**  
 * @Title: GiftController.java
 * @Package com.snowstore.diana.controller
 * @Description: (礼物)
 * @author wangyunhao  
 * @date 2015年10月22日 下午4:04:12
 * @version V1.0  
 */
package com.snowstore.diana.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.snowstore.diana.domain.*;
import com.snowstore.diana.domain.Package;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.jaxb.XmlJaxbAnnotationIntrospector;
import com.snowstore.diana.domain.Order.Status;
import com.snowstore.diana.service.CustomerService;
import com.snowstore.diana.service.GiftCodeService;
import com.snowstore.diana.service.OrderService;
import com.snowstore.diana.service.PackageService;
import com.snowstore.diana.service.UnionUserService;
import com.snowstore.diana.service.UserGiftService;
import com.snowstore.diana.service.logistics.LogisticsService;
import com.snowstore.diana.vo.AddressVo;
import com.snowstore.diana.vo.GiftVo;
import com.snowstore.diana.vo.Result;
import com.snowstore.diana.vo.logistics.Response;
import com.snowstore.diana.vo.logistics.UpdateInfos;
import com.snowstore.log.annotation.UserLog;

@Controller
@RequestMapping("/gift/")
public class GiftController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserGiftService userGiftService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private LogisticsService logisticsService;

	@Autowired
	private PackageService packageService;

	@Autowired
	private UnionUserService unionUserService;
	@Value("${fawang.endpoint.security:123321}")
	private String fawangEndpointSecurity;

	@Autowired
	private GiftCodeService giftCodeService;

	/**
	 * 礼物列表
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("list")
	@UserLog(remark = "【前台礼品】获取订单号，跳转到礼品列表页面")
	public String list(Model model, Long orderId) {
		model.addAttribute("orderId", orderId);
		return "gift_list";
	}

	/**
	 * 礼物详情
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("detail")
	@UserLog(remark = "【前台礼品】获取礼品序号，跳转至礼品详情页")
	public String detail(Model model, int index) {
		model.addAttribute("index", index);
		return "gift_detail";
	}

	/**
	 * 获取礼物列表
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("getList")
	@ResponseBody
	@UserLog(remark = "【前台礼品】获取礼物列表")
	public Result<LinkedList<GiftVo>> getList(Long orderId) {
		LinkedList<GiftVo> userGifts = userGiftService.findByUserId(customerService.getCurrentUser().getId());
		Order order = orderService.getUserOrderById(orderId);
		List<UserGift> orderGift = userGiftService.findByOrderId(orderId);
		if (Order.DataType.OFFLINE.name().equals(order.getDataType())) {

		} else if (null == order.getGitfNum() || order.getGitfNum() == 0) {
			LOGGER.info("该订单没有礼品。");
		} else {
			int gifts = order.getGitfNum() - (orderGift == null ? 0 : orderGift.size());
			for (int i = 0; i < gifts; i++) {
				GiftVo vo = new GiftVo();
				vo.setId(0);
				vo.setSelected(false);
				userGifts.add(vo);
			}
		}
		Result<LinkedList<GiftVo>> result = new Result<LinkedList<GiftVo>>();
		result.setType(Result.Type.SUCCESS);
		result.setData(userGifts);
		return result;
	}

	/**
	 * 选择礼物
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("choose")
	@UserLog(remark = "【前台礼品】下单未领取礼品，之后才领取，跳转至选礼品页")
	public String choose(Model model, long id, Long orderId) {
		Order order = orderService.getUserOrderById(orderId);
		if (null == order || order.getGitfNum() == null || order.getGitfNum() == 0) {
			return "forward:/movie/codeList?orderId=" + orderId;
		}
		model.addAttribute("id", id);
		model.addAttribute("orderId", orderId);
		return "gift_choose";
	}

	/**
	 * 礼物收货地址
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("address")
	@UserLog(remark = "【前台礼品】跳转至礼品收货地址页面")
	public String address(Model model, long id, int index, Long orderId) {
		model.addAttribute("id", id);
		model.addAttribute("index", index);
		model.addAttribute("orderId", orderId);
		return "gift_address";
	}

	/**
	 * mm14礼物详情
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("mm14")
	@UserLog(remark = "【前台礼品】跳转至mm14礼品详细信息页面")
	public String mm14(Model model, long orderId, long productId, int index, String giftNo) {
		model.addAttribute("orderId", orderId);
		model.addAttribute("productId", productId);
		model.addAttribute("index", index);
		if (giftNo == null) {
			giftNo = "";
		}
		model.addAttribute("giftNo", giftNo);
		if (orderId == 0) {
			List<Order> orders = orderService.findByUserAndStatus(customerService.getCurrentUser(), Status.待付款.name());
			if (orders.size() > 1) {
				throw new RuntimeException("订单异常存在多个未支付订单");
			} else if (orders.size() == 1) {
				List<UserGift> gifts = userGiftService.findByOrderId(orders.get(0).getId());
				if (!gifts.isEmpty()) {
					model.addAttribute("orderId", orders.get(0).getId());
					model.addAttribute("giftId", gifts.get(0).getId());
				}
			}
		}
		return "gift_mm14";
	}

	/**
	 * mm57礼物详情
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("mm57")
	@UserLog(remark = "【前台礼品】跳转至mm57礼品详细信息页面")
	public String mm57(Model model, long orderId, long productId, int index) {
		model.addAttribute("orderId", orderId);
		model.addAttribute("productId", productId);
		model.addAttribute("index", index);
		Order order = orderService.getUserOrderById(orderId);
		if (null != order && Order.Status.已付款.name().equals(order.getStatus())) {
			GiftCode code = giftCodeService.getGiftCodeByOrderId(orderId, GiftCode.RedeemCodeType.DQ.name());
			if (null != code) {
				model.addAttribute("giftNo1", code.getRedeemCode().split(",")[0]);
				model.addAttribute("giftNo2", code.getRedeemCode().split(",")[1]);
				model.addAttribute("giftNo3", code.getRedeemCode().split(",")[2]);
				model.addAttribute("giftNo4", code.getRedeemCode().split(",")[3]);
				model.addAttribute("giftNo5", code.getRedeemCode().split(",")[4]);
				String[] redeemCodePw = code.getRedeemCodePassword().split(",");
				model.addAttribute("tokenNo1", "密码:" + redeemCodePw[0]);
				model.addAttribute("tokenNo2", "密码:" + redeemCodePw[1]);
				model.addAttribute("tokenNo3", "密码:" + redeemCodePw[2]);
				model.addAttribute("tokenNo4", "密码:" + redeemCodePw[3]);
				model.addAttribute("tokenNo5", "密码:" + redeemCodePw[4]);
			} else {
				model.addAttribute("enableTimes", true);
				model.addAttribute("giftNo1", "努力生成中...");
				model.addAttribute("giftNo2", "努力生成中...");
				model.addAttribute("giftNo3", "努力生成中...");
				model.addAttribute("giftNo4", "努力生成中...");
				model.addAttribute("giftNo5", "努力生成中...");
				model.addAttribute("tokenNo1", "");
				model.addAttribute("tokenNo2", "");
				model.addAttribute("tokenNo3", "");
				model.addAttribute("tokenNo4", "");
				model.addAttribute("tokenNo5", "");
			}
		}

		if (orderId == 0) {
			List<Order> orders = orderService.findByUserAndStatus(customerService.getCurrentUser(), Status.待付款.name());
			if (orders.size() > 1) {
				throw new RuntimeException("订单异常存在多个未支付订单");
			} else if (orders.size() == 1) {
				List<UserGift> gifts = userGiftService.findByOrderId(orders.get(0).getId());
				if (!gifts.isEmpty()) {
					model.addAttribute("orderId", orders.get(0).getId());
					model.addAttribute("giftId", gifts.get(0).getId());
				}
			}
		}
		return "gift_mm57";
	}

	/**
	 * 获取礼物地址
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("getOne")
	@ResponseBody
	@UserLog(remark = "【前台订单】点击礼物获取礼物地址")
	public Result<GiftVo> getOne(long id, long orderId) {
		Result<GiftVo> result = new Result<GiftVo>();
		GiftVo vo = userGiftService.get(id);
		if (id <= 0) {
			List<UserGift> userGifts = userGiftService.findByOrderId(orderId);
			if (userGifts.isEmpty()) {
				Order order = orderService.getUserOrderById(orderId);
				if (order.getGitfNum() == null || order.getGitfNum() == 0) {
					result.setType(Result.Type.FAILURE);
					result.addMessage("该订单没有礼品！");
					return result;
				}
			} else {
				vo = userGiftService.get(userGifts.get(0).getId());
			}
		} else if (null == vo) {
			result.setType(Result.Type.FAILURE);
			result.addMessage("该订单没有礼品！");
			return result;
		}
		result.setType(Result.Type.SUCCESS);
		if (null == vo) {
			vo = new GiftVo();
		}
		result.setData(vo);
		return result;
	}

	/**
	 * 查询订单礼品
	 * 
	 * @param orderId
	 * @return
	 */
	@RequestMapping("lookForGift")
	@ResponseBody
	@UserLog(remark = "【前台订单】查看前台订单礼品")
	public Result<ArrayList<GiftVo>> lookForGift(long orderId) {
		Result<ArrayList<GiftVo>> result = new Result<ArrayList<GiftVo>>();
		ArrayList<GiftVo> listGiftVo = new ArrayList<GiftVo>();
		result.setType(Result.Type.SUCCESS);
		List<UserGift> userGifts = userGiftService.findByOrderId(orderId);
		Order order = orderService.getUserOrderById(orderId);
		if (null == order) {
			result.setType(Result.Type.FAILURE);
			result.addMessage("数据异常,稍后再试。");
		}
		if (userGifts.isEmpty()) {
			if (order.getGitfNum() == null || order.getGitfNum() == 0) {
				// result.setType(Result.Type.FAILURE);
				// result.addMessage("该订单没有礼品！");
				// return result;
				result.setData(listGiftVo);
				LOGGER.info("该订单没有礼品!");
			} else {
				GiftVo giftVoNot = new GiftVo();
				giftVoNot.setOrderId(orderId);
				listGiftVo.add(giftVoNot);
				result.setData(listGiftVo);
			}
		} else {
			listGiftVo.add(userGiftService.get(userGifts.get(0).getId()));
			result.setData(listGiftVo);
		}
		List<Order> subOrderList = this.orderService.findBySubOrderService(orderId);
		// 验证是否存在子订单(电影票兑换礼品)
		if (subOrderList != null && subOrderList.size() > 0) {
			for (Order subOrder : subOrderList) {
				List<UserGift> subUserGift = userGiftService.findByOrderId(subOrder.getId());
				if (subUserGift != null && subUserGift.size() > 0) {
					listGiftVo.add(userGiftService.get(subUserGift.get(0).getId()));
				}
			}
			result.setData(listGiftVo);
		}
		return result;
	}

	/**
	 * 获取上一次使用的地址
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("getLastAddress")
	@ResponseBody
	@UserLog(remark = "【前台订单】订单后选取礼品获取上一次使用的地址")
	public Result<AddressVo> getLastAddress() {
		// 如果历史地址，请用用户自身信息和空地址填充
		AddressVo vo = new AddressVo();
		Result<AddressVo> result = new Result<>();
		result.setType(Result.Type.SUCCESS);
		result.setData(vo);
		return result;
	}

	/**
	 * 
	 * 添加省份城市区域参数
	 * 
	 * @author mingzhi.dong
	 * @updateDate 2016年1月19日
	 */
	@RequestMapping("submitGift")
	@ResponseBody
	@UserLog(remark = "【前台订单】后选取礼品提交地址信息")
	public Result<Order> submitGift(Long orderId, String giftName, String gift, String person, String phone, String province, String city, String area, String address, String spuCode) {
		Result<Order> result = new Result<>();
		Order order = orderService.getUserOrderById(orderId);

		//校验电影票是否已兑换
		Tickets tickets = order.getGiftTickets();
		if (tickets != null && tickets.getStatus().equals(Tickets.Status.已兑换.name())) {
			LOGGER.info("电影票{}已兑换，无法转为礼品权益",tickets.getId());
			result.setType(Result.Type.FAILURE);
			result.addMessage("已兑换的电影票无法转为礼品权益");
			return result;
		}

		//校验该订单是否存在多个礼品
		UserGift userGift = null;
		List<UserGift> gifts = userGiftService.findByOrderId(order.getId());
		if (gifts.size() > 1) {
			userGift = gifts.get(0);
			LOGGER.error("异常信息，用户改订单存在多个礼品！！！！！！！！！！！！！！！！！！");
		} else if (gifts.size() == 1) {
			userGift = gifts.get(0);
			return new Result<>(Result.Type.FAILURE, "你已经选择过礼品了");
		} else {
			userGift = new UserGift();
		}
		userGift.setUserId(order.getUser().getId());
		userGift.setUnionId(unionUserService.findOrAdd(order.getUser().getMobile()));
		userGift.setRefGift(gift);
		if ("mm57".equals(gift)) {
			userGift.setStatus(UserGift.Status.未分配.name());
		}
		userGift.setGiftName(giftName);
		userGift.setSpuCode(spuCode);
		userGift.setOrder(order);

		Package pkg = packageService.findPackageByOrderId(order.getId());
		if (pkg == null) {
			pkg = new Package();
		}
		pkg.setAddress(address);
		pkg.setMobile(phone);
		pkg.setOrderId(orderId);
		pkg.setRecipients(person);
		// 添加省份城市区域信息 @updateDate 2016年1月19日
		pkg.setProvince(province);
		pkg.setCity(city);
		pkg.setArea(area);
		userGift = userGiftService.saveUserGift(userGift, pkg);

		// 获取原始订单信息
		if (order.getGiftTickets() != null) {
			order = orderService.get(order.getGiftTickets().getRefOrder());
		}
		result.setData(order);
		result.setType(Result.Type.SUCCESS);
		return result;
	}

	/**
	 * @return
	 */
	@RequestMapping("giftChoosePre")
	@UserLog(remark = "【前台订单】当场选礼品时，跳转至礼品页面")
	public String giftChoosePre(Model model, Long productId) {
		List<Order> orders = orderService.findByUserAndStatus(customerService.getCurrentUser(), Status.待付款.name());
		if (orders.size() > 1) {
			throw new RuntimeException("订单异常存在多个未支付订单");
		} else if (orders.size() == 1) {
			model.addAttribute("orderId", orders.get(0).getId());
			List<UserGift> gifts = userGiftService.findByOrderId(orders.get(0).getId());
			if (!gifts.isEmpty()) {
				model.addAttribute("giftId", gifts.get(0).getId());
			}
		}
		model.addAttribute("productId", productId);
		return "gift_choose_pre";
	}

	/**
	 * @return
	 */
	@RequestMapping("giftAddressPre")
	@UserLog(remark = "【前台订单】当场选礼品时，跳转填写地址页面")
	public String giftAddressPre(Model model, Long productId, Long giftIndex) {
		List<Order> orders = orderService.findByUserAndStatus(customerService.getCurrentUser(), Status.待付款.name());
		if (orders.size() > 1) {
			throw new RuntimeException("订单异常存在多个未支付订单");
		} else if (orders.size() == 1) {
			model.addAttribute("orderId", orders.get(0).getId());
			List<UserGift> gifts = userGiftService.findByOrderId(orders.get(0).getId());
			if (!gifts.isEmpty()) {
				model.addAttribute("giftId", gifts.get(0).getId());
			}
		}
		model.addAttribute("productId", productId);
		model.addAttribute("index", giftIndex);
		return "gift_address_pre";
	}

	/**
	 * 添加省份城市区域参数
	 * 
	 * @author mingzhi.dong
	 * @updateDate 2016年1月19日
	 * @param giftName礼品名称
	 * @param productId产品id
	 * @param gift
	 * @param person收货人
	 * @param phone
	 * @param address
	 * @return
	 */
	@RequestMapping("submitGiftPre")
	@ResponseBody
	@UserLog(remark = "【前台订单】当场选礼品时，提交礼品地址信息")
	public Result<Order> submitGiftPre(HttpServletRequest req, String giftName, long productId, String gift, String person, String phone, String province, String city, String area, String address, String spuCode) {
		Result<Order> result = new Result<Order>(Result.Type.SUCCESS);
		Order order = null;
		List<Order> orders = orderService.findByUserAndStatus(customerService.getCurrentUser(), Status.待付款.name());
		if (orders.size() > 1) {
			throw new RuntimeException("订单异常存在多个未支付订单");
		} else if (orders.size() == 1) {
			order = orders.get(0);
		}
		if (null == order) {
			order = orderService.generateOrder(productId, req).getData();
		}
		UserGift userGift = null;
		List<UserGift> gifts = userGiftService.findByOrderId(order.getId());
		if (gifts.size() > 1) {
			userGift = gifts.get(0);
			LOGGER.error("异常信息，用户改订单存在多个礼品！！！！！！！！！！！！！！！！！！");
		} else if (gifts.size() == 1) {
			userGift = gifts.get(0);
		} else {
			userGift = new UserGift();
		}
		result.setData(order);
		User user = customerService.getCurrentUser();
		userGift.setUserId(user.getId());
		userGift.setUnionId(unionUserService.findOrAdd(user.getMobile()));
		userGift.setRefGift(gift);
		if ("mm57".equals(gift)) {
			userGift.setStatus(UserGift.Status.未分配.name());
		}
		userGift.setGiftName(giftName);
		userGift.setSpuCode(spuCode);
		userGift.setOrder(orderService.getUserOrderById(result.getData().getId()));
		Package pkg = packageService.findPackageByOrderId(order.getId());
		if (pkg == null) {
			pkg = new Package();
		}
		pkg.setAddress(address);
		pkg.setMobile(phone);
		pkg.setOrderId(result.getData().getId());
		pkg.setRecipients(person);
		// 添加省份城市区域信息 @updateDate 2016年1月19日
		pkg.setProvince(province);
		pkg.setCity(city);
		pkg.setArea(area);
		userGift = userGiftService.saveUserGift(userGift, pkg);
		return result;
	}

	@RequestMapping("syncLogsticsStatus")
	@ResponseBody
	@UserLog(remark = "【前台订单】同步物流状态")
	public void syncLogsticsStatus(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setCharacterEncoding("GBK");
		res.setHeader("Content-type", "text/plain;charset=GBK");
		String logistics_interface = req.getParameter("logistics_interface");
		String data_digest = req.getParameter("data_digest");
		LOGGER.info("发网状态同步source:logistics_interface=[" + logistics_interface + "]data_digest=[" + data_digest + "]");
		Response response = new Response();
		// 验证格式
		if (StringUtils.isBlank(logistics_interface) || StringUtils.isBlank(data_digest)) {
			response.setSuccess(Boolean.FALSE);
			response.setReason("S01[非法的XML格式]");
			res.getWriter().write(response.toString());
			return;
		}
		logistics_interface = StringEscapeUtils.unescapeHtml4(logistics_interface);
		LOGGER.info("html4unescaped:" + logistics_interface);
		// 验证签名
		String digest = DigestUtils.md5Hex(logistics_interface + fawangEndpointSecurity).toUpperCase();
		LOGGER.info("计算出的签名" + digest);
		if (!StringUtils.equals(digest, data_digest)) {
			response.setSuccess(Boolean.FALSE);
			response.setReason("S02[非法的数字签名]");
			res.getWriter().write(response.toString());
			return;
		}
		XmlMapper xmlMapper = new XmlMapper();
		AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
		AnnotationIntrospector secondary = new XmlJaxbAnnotationIntrospector(xmlMapper.getTypeFactory());
		AnnotationIntrospector pair = AnnotationIntrospector.pair(primary, secondary);
		xmlMapper.setAnnotationIntrospector(pair);
		try {
			UpdateInfos updateInfos = xmlMapper.readValue(logistics_interface, UpdateInfos.class);
			logisticsService.updateInfos(updateInfos);
			res.getWriter().write(new com.snowstore.diana.vo.logistics.Response().toString());
		} catch (NullPointerException e) {
			LOGGER.warn(e.getMessage(), e);
			response.setSuccess(Boolean.FALSE);
			response.setReason("S01[非法的XML格式]");
			res.getWriter().write(response.toString());
		} catch (JsonParseException e) {
			LOGGER.warn(e.getMessage(), e);
			response.setSuccess(Boolean.FALSE);
			response.setReason("S01[非法的XML格式]");
			res.getWriter().write(response.toString());
		} catch (JsonMappingException e) {
			LOGGER.warn(e.getMessage(), e);
			response.setSuccess(Boolean.FALSE);
			response.setReason("S01[非法的XML格式]");
			res.getWriter().write(response.toString());
		} catch (IOException e) {
			LOGGER.warn(e.getMessage(), e);
			response.setSuccess(Boolean.FALSE);
			response.setReason("S07[系统异常，请重试]");
			res.getWriter().write(response.toString());
		} catch (RuntimeException e) {
			LOGGER.warn(e.getMessage(), e);
			response.setSuccess(Boolean.FALSE);
			response.setReason(e.getMessage());
			res.getWriter().write(response.toString());
		}
	}

	/**
	 * 异步获取兑换码
	 * 
	 * @author mingzhi.dong
	 * @date 2016年3月21日
	 * @param orderId
	 * @param redeemCodeType
	 * @return
	 */
	@RequestMapping("/asyncGiftCode")
	@ResponseBody
	@UserLog(remark = "【前台礼品】异步获取mm14和mm57兑换码")
	public JSONObject asyncGiftCode(Long orderId, String redeemCodeType) {
		JSONObject json = JSON.parseObject("{}");
		json.put("success", false);
		Long userId = customerService.getCurrentUser().getId();
		UserGift userGift = this.userGiftService.getUserGiftService(userId, orderId, redeemCodeType);
		if (null != userGift) {
			if ("mm57".equals(redeemCodeType)) {// DQ卷
				if (userGift.getStatus().equals(UserGift.Status.已分配.name())) {
					GiftCode code = giftCodeService.getGiftCodeByOrderId(orderId, GiftCode.RedeemCodeType.DQ.name());
					json.put("success", true);
					String[] redeemCodeNum = code.getRedeemCode().split(",");
					json.put("giftNo1", redeemCodeNum[0]);
					json.put("giftNo2", redeemCodeNum[1]);
					json.put("giftNo3", redeemCodeNum[2]);
					json.put("giftNo4", redeemCodeNum[3]);
					json.put("giftNo5", redeemCodeNum[4]);
					String[] redeemCodePw = code.getRedeemCodePassword().split(",");
					json.put("tokenNo1", "密码:" + redeemCodePw[0]);
					json.put("tokenNo2", "密码:" + redeemCodePw[1]);
					json.put("tokenNo3", "密码:" + redeemCodePw[2]);
					json.put("tokenNo4", "密码:" + redeemCodePw[3]);
					json.put("tokenNo5", "密码:" + redeemCodePw[4]);
				}
			} else if ("mm14".equals(redeemCodeType)) {// 贝思客
				if (null != userGift.getBestCakeCode()) {
					json.put("success", true);
					json.put("bestCakeCode", userGift.getBestCakeCode());
				}
			}
		}
		return json;
	}
}
