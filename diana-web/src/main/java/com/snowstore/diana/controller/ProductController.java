package com.snowstore.diana.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.snowstore.diana.domain.Product.Type;
import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.tools.generic.NumberTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.foxinmy.weixin4j.api.BaseApi;
import com.foxinmy.weixin4j.mp.api.OauthApi;
import com.foxinmy.weixin4j.util.Weixin4jConfigUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.snowstore.diana.common.DianaConstants;
import com.snowstore.diana.common.DianaConstants.DianaDevice;
import com.snowstore.diana.common.DianaConstants.DianaPlatform;
import com.snowstore.diana.config.WordConfig;
import com.snowstore.diana.domain.ConcertSeat;
import com.snowstore.diana.domain.Order.Status;
import com.snowstore.diana.domain.PayInfo.PayType;
import com.snowstore.diana.domain.Product;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.service.userDetails.UserDetailsImpl;
import com.snowstore.diana.utils.MatrixToImageWriter;
import com.snowstore.diana.vo.ConcertVo;
import com.snowstore.diana.vo.GroupRuleVo;
import com.snowstore.diana.vo.ImgVo;
import com.snowstore.diana.vo.PageTables;
import com.snowstore.diana.vo.ProductVo;
import com.snowstore.diana.vo.Result;
import com.snowstore.diana.vo.TicketAreaVo;
import com.snowstore.diana.vo.TicketOrderVo;
import com.snowstore.diana.vo.TicketTimeVo;
import com.snowstore.log.annotation.UserLog;

@Controller
@RequestMapping("/product")
public class ProductController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private TicketsService ticketsService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private OauthApi oauthApi;

	@Autowired
	private GroupRuleService groupRuleService;
	
	@Autowired
	private ChannelProductService channelProductService;

	@RequestMapping("list")
	@UserLog(remark = "【产品】显示所有产品列表")
	public String list(Model model, HttpServletRequest req) {
		PageRequest page = new PageRequest(0, 1000, new Sort(new Order(Direction.ASC, "status"), new Order(Direction.DESC, "sortNum"), new Order(Direction.DESC, "id")));
		List<ProductVo> list = productService.findByStatus(page);
		String platform = (String) req.getSession().getAttribute(DianaConstants.SESSION_PLATFORM);
		if (null == platform) {
			platform = DianaPlatform.WAP.name();
		}
		User user = customerService.getCurrentUser();
		if (null != user) {
			platform = user.getPlatform();
		}
		Iterator<ProductVo> it = list.iterator();
		while (it.hasNext()) {
			ProductVo vo = it.next();
			if (DianaConstants.DianaPlatform.JUPITER.name().equals(platform)) {
				if (vo.getName().startsWith("百元玩电影")) {
					it.remove();
				}
			} else if (DianaConstants.DianaPlatform.PROMETHEUS.name().equals(platform)) {
				if (!vo.getName().startsWith("百元玩电影")) {
					it.remove();
				}
			} else if (DianaConstants.DianaPlatform.WAP.name().equals(platform)) {

			}
		}
		model.addAttribute("list", list);
		return "index";
	}

	/**
	 * 电影宝转化
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("movieTransform")
	public String movieTransform(Model model) {
		return "movie_transform";
	}

	/**
	 * 门票海报
	 * 
	 * @param model
	 * @param id
	 *            门票产品ID
	 * @return
	 */
	@RequestMapping("ticketPoster")
	public String ticketPoster(Model model, long id) {
		model.addAttribute("id", id);
		model.addAttribute("title", "莫文蔚演唱会");
		return "ticket_poster";
	}

	/**
	 * 门票选区域
	 * 
	 * @param model
	 * @param id
	 *            门票产品ID
	 * @return
	 */
	@RequestMapping("ticketArea")
	public String ticketArea(Model model, long id) {
		model.addAttribute("id", id);
		return "ticket_area";
	}

	/**
	 * 可选场次
	 * 
	 * @param id
	 *            门票产品ID
	 * @return
	 */
	@RequestMapping("getTicketTime")
	@ResponseBody
	public Result<LinkedList<TicketTimeVo>> getTicketTime(long id) {
		LinkedList<TicketTimeVo> list = new LinkedList<>();
		for (int i = 0; i < 7; i++) {
			TicketTimeVo vo = new TicketTimeVo();
			vo.setKey(i + "");
			vo.setValue("场次" + i);
			vo.setValid(i != 4);
			list.add(vo);
		}
		Result<LinkedList<TicketTimeVo>> result = new Result<>();
		result.setType(Result.Type.SUCCESS);
		result.setData(list);
		return result;
	}

	/**
	 * 可选区域
	 * 
	 * @param id
	 *            门票产品ID
	 * @param time
	 *            场次
	 * @return
	 */
	@RequestMapping("getTicketArea")
	@ResponseBody
	public Result<LinkedList<TicketAreaVo>> getTicketArea(long id, String time) {
		LinkedList<TicketAreaVo> list = new LinkedList<>();
		for (int i = 0; i < 7; i++) {
			TicketAreaVo vo = new TicketAreaVo();
			vo.setKey(i + "");
			vo.setValue("区域" + i);
			vo.setValid(i != 3);
			vo.setPrice(i + 100);
			list.add(vo);
		}
		Result<LinkedList<TicketAreaVo>> result = new Result<>();
		result.setType(Result.Type.SUCCESS);
		result.setData(list);
		return result;
	}

	/**
	 * 门票订单
	 * 
	 * @param model
	 * @param id
	 *            门票产品ID
	 * @param time
	 *            场次
	 * @param area
	 *            座位区域
	 * @return
	 */
	@RequestMapping("ticketOrder")
	public String ticketOrder(Model model, long id, String time, String area) {
		model.addAttribute("id", id);
		model.addAttribute("time", time);
		model.addAttribute("area", area);
		return "ticket_order";
	}

	/**
	 * 门票订单
	 * 
	 * @param model
	 * @param id
	 *            门票产品ID
	 * @param time
	 *            场次
	 * @param area
	 *            座位区域
	 * @param isDelivery
	 *            是否快递
	 * @param person
	 *            收件人
	 * @param phone
	 *            收件人手机
	 * @param address
	 *            收件地址
	 * @return
	 */
	@RequestMapping("ticketBuy")
	public String ticketBuy(Model model, long id, String time, String area, int isDelivery, String person, String phone, String address) {
		model.addAttribute("id", id);
		model.addAttribute("time", time);
		model.addAttribute("area", area);
		model.addAttribute("isDelivery", isDelivery);
		model.addAttribute("person", person);
		model.addAttribute("phone", phone);
		model.addAttribute("address", address);
		return "ticket_buy";
	}

	@RequestMapping("ticketBuyByQr/{orderId}")
	@UserLog(remark = "【支付】跳转至扫二维码支付页面")
	public String ticketBuyByQr(Model model, @PathVariable Long orderId, String qrcode) {
		model.addAttribute("qrcode", qrcode);
		model.addAttribute("orderId", orderId);
		return "ticket_buy_qrcode";
	}

	/**
	 * 进入快捷支付页面
	 * 
	 * @param orderId
	 * @param rep
	 */
	@RequestMapping("ticketBuyByQuick/{orderId}")
	public String ticketBuyByQuick(Model model, @PathVariable Long orderId) {
		model.addAttribute("orderId", orderId);
		com.snowstore.diana.domain.Order order = orderService.getUserOrderById(orderId);
		model.addAttribute("amout", order.getAmount());
		return "ticket_buy_quick";
	}

	/**
	 * 获取支付二维码
	 * 
	 * @param qrcode
	 * @param rep
	 */
	@RequestMapping("getQr")
	@UserLog(remark = "【支付】获取支付二维码")
	public void getQr(String qrcode, HttpServletResponse rep) {
		try {
			rep.setContentType("image/jpeg");
			OutputStream outputStream = rep.getOutputStream();
			;
			MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

			Map<EncodeHintType, String> hints = new HashMap<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			BitMatrix bitMatrix = multiFormatWriter.encode(qrcode, BarcodeFormat.QR_CODE, 400, 400, hints);
			MatrixToImageWriter.writeToStream(bitMatrix, "jpeg", outputStream);
			outputStream.flush();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
	}

	/**
	 * 生成订单
	 * 
	 * @param id
	 * @param time
	 * @param area
	 * @return
	 */
	@RequestMapping("generateOrder")
	@ResponseBody
	@UserLog(remark = "【订单】生成订单")
	public Result<TicketOrderVo> generateOrder(long orderId, HttpServletRequest req) {
		com.snowstore.diana.domain.Order order = orderService.getUserOrderById(orderId);
		BigDecimal ruleAmount = null;
		if (Product.Type.多张组合.name().equals(order.getType())) {
			ruleAmount = (BigDecimal) req.getSession().getAttribute("ruleAmount");
		}
		Product product = productService.get(order.getProductId());
		TicketOrderVo vo = new TicketOrderVo();
		vo.setProductId(product.getId());
		vo.setName(product.getName());
		vo.setLimit(order.getTicketNum());
		vo.setMovieTicket(true);
		vo.setTicketScale(order.getTicketNum());
		vo.setCount(order.getTicketNum());
		vo.setPrice(order.getAmount().doubleValue());
		vo.setGiftNum(order.getGitfNum());
		BigDecimal payAmount = order.getAmount();
		if (ruleAmount != null && order.getUser().getId().equals(customerService.getUserDetails().getId())) {
			payAmount = payAmount.subtract(ruleAmount);
		}

		UserDetailsImpl userDetails = customerService.getUserDetails();
		if (userDetails.getPlatform().equals(WordConfig.LTMT) && order.getUser().getId().equals(userDetails.getId())) {
			payAmount = payAmount.subtract(WordConfig.LTMT_M);
		}
		// 实际支付金额
		vo.setPayAmount(payAmount.doubleValue());
		Result<TicketOrderVo> result = new Result<>();
		result.setType(Result.Type.SUCCESS);
		result.setData(vo);
		return result;
	}

	@RequestMapping("generateOrderForGift")
	@ResponseBody
	@UserLog(remark = "【订单】生成电影转礼品权益订单")
	public Result<com.snowstore.diana.domain.Order> generateOrderForGift(HttpServletRequest req, Long ticketsId){
		Result<com.snowstore.diana.domain.Order> result = new Result<>(Result.Type.FAILURE);
		com.snowstore.diana.domain.Order order;
		Tickets tickets = ticketsService.getUserTicketsById(ticketsId);
		//判断是否存在该订单
		if( null == tickets ){
			LOGGER.error("异常信息，电影票{}不存在", ticketsId);
			result.addMessage("系统异常，请稍后再试");
			return result;
		}
		//判断是否存在子订单，防止重复生成
		if(tickets.getSubOrder()!=null){
			order = tickets.getSubOrder();
			result.setType(Result.Type.SUCCESS);
			result.setData(order);
			return result;
		}
		//开始电影转礼品权益
		List<Product> productList = productService.findByStatusAndType(Product.Status.在售, Type.电影转礼品);
		if(CollectionUtils.isEmpty(productList)){
			LOGGER.error("异常信息，不存在{}类型的产品", Type.电影转礼品.name());
			result.addMessage("系统异常，请稍后再试");
			return result;
		}
		order = orderService.generateOrderForGift(productList.get(0).getId(), ticketsId, req).getData();
		result.setType(Result.Type.SUCCESS);
		result.setData(order);
		return result;
	}


	/**
	 * 提交订单
	 * 
	 * @param id
	 * @param time
	 * @param area
	 * @return
	 */
	@RequestMapping("submitOrder")
	@ResponseBody
	@UserLog(remark = "【订单】提交订单，确认支付")
	public Result<JSONObject> submitOrder(HttpServletRequest req, long id, int count, Integer payType) {
		// 提交订单
		Result<JSONObject> result = orderService.productBuy(id, count, null, req, payType);
		LOGGER.info("返回预支付信息");
		return result;
	}

	@RequestMapping("{id}")
	@UserLog(remark = "【产品】跳转至产品详情页")
	public String product(@PathVariable Long id, Model model, HttpServletRequest req) {
		String openId = (String) req.getSession().getAttribute("openId");
		String userDevice = customerService.getUserDevice(req);
		LOGGER.info("用户设备：" + userDevice);
		// 非微信端直接跳转到产品详情
		if (!DianaDevice.JUPITER.name().equals(userDevice)) {
			return "redirect:/product/authorize/" + id;
		} else if (StringUtils.isNotEmpty(openId)) {
			LOGGER.info("用户openId：" + openId);
			return "redirect:/product/authorize/" + id;
		}
		LOGGER.info("微信请求准备请求授权......");
		// 如果是微信端，网页授权后跳转到产品详情
		String appId = BaseApi.DEFAULT_WEIXIN_ACCOUNT.getId();
		String redirectUri = Weixin4jConfigUtil.getValue("user_oauth_redirect_uri");
		String url = oauthApi.getAuthorizeURL(appId, "http://piao.jlfex.com" + redirectUri, System.getProperty("diana.domain") + "/product/authorize/" + id, "snsapi_base");
		return "redirect:" + url;
	}

	@RequestMapping("/authorize/{id}")
	@UserLog(remark = "【产品】加载产品详情页信息")
	public String prductAuthorize(@PathVariable Long id, Model model, String openId, HttpServletRequest req) {
		model.addAttribute("domain", System.getProperty("diana.domain"));
		if (null != openId) {
			req.getSession().setAttribute("openId", openId);
		}
		Product product = productService.get(id);
		model.addAttribute("product", product);
		if (Product.Type.百元玩电影.name().equals(product.getType())) {
			return "movie_100_new";
		} else if (Product.Type.壹票玩电影.name().equals(product.getType())) {
			return "movie_1";
		} else if (Product.Type.单张贵族.name().equals(product.getType())) {
			return "single_ticket";
		} else {
			return "movie_1";
		}
	}

	@RequestMapping("moviePurchase")
	@ResponseBody
	@UserLog(remark = "【产品】无人使用")
	public Result<JSONObject> moviePurchase(Long productId, Integer num, BigDecimal amount, HttpServletRequest req) {
		return orderService.productBuy(productId, num, null, req, PayType.微信.ordinal());
	}

	@RequestMapping("/chooseSeat/{id}")
	public String chooseSeat(@PathVariable Long id, Model model) {
		Product product = productService.get(id);
		model.addAttribute("productId", product.getId());
		model.addAttribute("concert", productService.getConcert(id));
		return "concertSeat";
	}

	@RequestMapping("/orderVerify/{id}")
	public String orderVerify(@PathVariable Long id, Model model, Long seat) {
		Product product = productService.get(id);
		model.addAttribute("productId", product.getId());
		ConcertVo vo = productService.getConcert(id);
		for (ConcertSeat concertSeat : vo.getSeats()) {
			if (concertSeat.getId().equals(seat)) {
				model.addAttribute("seat", concertSeat);
				break;
			}
		}
		model.addAttribute("concert", vo.getConcert());
		return "orderVerify";
	}

	/**
	 * 电影票支付页面
	 * 
	 * @param productId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/buyTicket/pay/{orderId}")
	@UserLog(remark = "【产品】点击立即购买，跳转至支付页面")
	public String buyTicket(@PathVariable Long orderId, String openId, Model model, HttpServletRequest req) {
		String userDevice = customerService.getUserDevice(req);
		if (null != openId) {
			req.getSession().setAttribute("openId", openId);
		}

		if (DianaDevice.JUPITER.name().equals(userDevice) && StringUtils.isEmpty(openId)) {
			LOGGER.info("微信请求准备请求授权......");
			String appId = BaseApi.DEFAULT_WEIXIN_ACCOUNT.getId();
			String redirectUri = Weixin4jConfigUtil.getValue("user_oauth_redirect_uri");
			String url = oauthApi.getAuthorizeURL(appId, "http://piao.jlfex.com" + redirectUri, System.getProperty("diana.domain") + "/product/buyTicket/pay/" + orderId, "snsapi_base");
			return "redirect:" + url;
		}
		model.addAttribute("orderId", orderId);
		NumberTool numberTool = new NumberTool();
		model.addAttribute("numberTool", numberTool);
		return "ticket_buy";
	}

	/**
	 * 演唱会支付页面
	 * 
	 * @param productId
	 * @param model
	 * @return
	 */
	@RequestMapping("/buyConcert/{productId}")
	public String buyConcert(@PathVariable Long productId, Model model) {
		model.addAttribute("product", productService.get(productId));
		return "buyConcert";
	}

	/**
	 * 查看协议
	 * 
	 * @param productId
	 * @param model
	 * @return
	 */
	@RequestMapping("/lookupProtocol")
	@UserLog(remark = "【产品】跳转至查看协议页面")
	public String lookupProtocol(Model model, Long productId) {
		return "protocol_1";
	}

	/**
	 * 查看活动规则
	 * 
	 * @param productId
	 * @param model
	 * @return
	 */
	@RequestMapping("/lookupRule")
	@UserLog(remark = "【产品】 跳转至查看活动规则页面")
	public String lookupRule(Model model) {
		return "protocol_2";
	}

	/**
	 * 怎么玩
	 * 
	 * @return
	 */
	@RequestMapping("/howToPlay")
	@UserLog(remark = "【产品】跳转至怎么玩")
	public String howToPlay(Model model) {
		return "how_to_play";
	}

	/**
	 * 玩什么
	 * 
	 * @return
	 */
	@RequestMapping("/whatToPlay")
	@UserLog(remark = "【产品】跳转至玩什么页面")
	public String whatToPlay(Model model) {
		return "what_to_play";
	}

	/**
	 * 玩什么
	 * 
	 * @return
	 */
	@RequestMapping("/mm56")
	@UserLog(remark = "【产品】跳转至mm56玩什么")
	public String mm56(Model model) {
		return "gift_mm56";
	}

	/**
	 * 裸票组合入口
	 * 
	 * @param productId
	 * @param model
	 * @return
	 */
	@RequestMapping("/ticket_combo")
	@UserLog(remark = "【产品】裸票组合入口")
	public String ticketCombo(Model model, HttpServletRequest req) {
		PageTables<GroupRuleVo> page = null;
		try {
			page = groupRuleService.findPageGroupRuleService(new PageRequest(0, 100, new Sort(new Order(Direction.ASC, "ticketNumber"))));
			model.addAttribute("list", page.getData());
		} catch (Exception e) {
			LOGGER.error("查询裸票规则出错", e);
		}
		return "ticket_combo";
	}

	@RequestMapping("/ruleAmount")
	@ResponseBody
	@UserLog(remark = "【产品】抽奖")
	public Result<BigDecimal> generateRuleAmount(Long ruleId, HttpServletRequest req) {
		Result<BigDecimal> result = new Result<BigDecimal>(Result.Type.SUCCESS);
		List<com.snowstore.diana.domain.Order> orders = orderService.findByUserAndStatus(customerService.getCurrentUser(), Status.待付款.name());
		if (orders.size() > 1) {
			throw new RuntimeException("订单异常存在多个未支付订单");
		} else if (orders.size() == 1) {
			result.setData(new BigDecimal(orders.get(0).getId()));
			result.setType(Result.Type.WARNING);
			result.addMessage("您有未支付订单，请先支付。");
			return result;
		}
		BigDecimal ruleAmount = orderService.generateRuleAmount(ruleId);
		req.getSession().setAttribute("ruleAmount", ruleAmount);
		req.getSession().setAttribute("ruleId", ruleId);
		result.setData(ruleAmount);
		return result;
	}

	@RequestMapping("/combo")
	@ResponseBody
	@UserLog(remark = "【产品】点击购买某个组合套餐")
	public Result<Product> combo(HttpServletRequest req, long ruleId) {
		Result<Product> result = new Result<Product>(Result.Type.SUCCESS);
		PageRequest pageRequest = new PageRequest(0, 1, new Sort(new Order(Direction.ASC, "status"), new Order(Direction.DESC, "sortNum"), new Order(Direction.DESC, "id")));
		Page<Product> page = productService.loadByType(Arrays.asList(Product.Type.多张组合.name()), pageRequest);
		if (page.getContent().isEmpty()) {
			result.setType(Result.Type.FAILURE);
			result.addMessage("产品已售罄，敬请期待下期....");
			return result;
		}
		result.setData(page.getContent().get(0));
		orderService.generateRuleAmount(ruleId);
		req.getSession().setAttribute("ruleAmount", new BigDecimal(0));
		req.getSession().setAttribute("ruleId", ruleId);
		return result;
	}

	/**
	 * 礼品卡介绍入口
	 * 
	 * @param productId
	 * @param model
	 * @return
	 */
	@RequestMapping("/gift_card_introduce")
	@UserLog(remark = "礼品卡介绍入口")
	public String giftCardIntroduce(Model model) {
		return "gift_card_introduce";
	}

	/**
	 * 单身贵族 for 返利网
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/single")
	@UserLog(remark = "【产品】单身贵族 for返利网")
	public String singleForFanli(Model model) {
		PageRequest pageRequest = new PageRequest(0, 1, new Sort(new Order(Direction.ASC, "status"), new Order(Direction.DESC, "sortNum"), new Order(Direction.ASC, "id")));
		Page<Product> page = productService.loadByType(Arrays.asList(Product.Type.单张贵族.name()), pageRequest);
		if (page.getContent().isEmpty()) {
			return "/index";
		}
		Product product = page.getContent().get(0);
		if (product.getIsAvailable()) {
			return "redirect:/product/" + product.getId();
		} else {
			return "/index";
		}
	}

	/**
	 * 单身贵族
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/danshen")
	@ResponseBody
	@UserLog(remark = "【产品】点击单张购买")
	public Result<Product> singleForInternal(Model model) {
		Result<Product> result = new Result<Product>(Result.Type.FAILURE);
		PageRequest pageRequest = new PageRequest(0, 1, new Sort(new Order(Direction.ASC, "status"), new Order(Direction.DESC, "sortNum"), new Order(Direction.ASC, "id")));
		Page<Product> page = productService.loadByType(Arrays.asList(Product.Type.单张贵族.name()), pageRequest);
		if (page.getContent().isEmpty()) {
			result.addMessage("产品已售罄，敬请期待下期");
			return result;
		}
		Product product = page.getContent().get(0);
		if (product.getIsAvailable()) {
			result.setData(product);
			result.setType(Result.Type.SUCCESS);
			return result;
		} else {
			result.addMessage("产品已售罄，敬请期待下期");
			return result;
		}
	}

	/**
	 * 绑定银行卡
	 * 
	 * @param orderId
	 * @param model
	 * @return
	 */
	@RequestMapping("/bindBankCard/{orderId}")
	public String bindBankCard(Model model, @PathVariable Long orderId) {
		model.addAttribute("orderId", orderId);
		return "bind_bank_card";
	}
	
	@RequestMapping("/11636912")
	public String redirect(){
		PageRequest page = new PageRequest(0, 1000, new Sort(new Order(Direction.ASC, "status"), new Order(Direction.DESC, "sortNum"), new Order(Direction.DESC, "id")));
		List<com.snowstore.diana.domain.Product.Status> status = new ArrayList<Product.Status>();
		status.add(Product.Status.在售);
		status.add(Product.Status.售罄);
		List<ProductVo> list = productService.findByStatusAndProductType(page, status, Arrays.asList(Product.Type.百元玩电影.name()));
		return "redirect:/product/"+list.get(0).getProductId();
	}
	@RequestMapping("/productDetail")
	public String productDetail(String productType,HttpServletRequest req){
		String platform = (String) req.getSession().getAttribute(DianaConstants.SESSION_PLATFORM);
		PageRequest page = new PageRequest(0, 1000, new Sort(new Order(Direction.ASC, "status"), new Order(Direction.DESC, "sortNum"), new Order(Direction.DESC, "id")));
		List<com.snowstore.diana.domain.Product.Status> status = new ArrayList<Product.Status>();
		status.add(Product.Status.在售);
		status.add(Product.Status.售罄);
		if(StringUtils.isEmpty(productType)){
			productType = channelProductService.getByChannelCode(platform).get(0);
		}
		List<ProductVo> list = productService.findByStatusAndProductType(page, status, Arrays.asList(productType));
		return "redirect:/product/"+list.get(0).getProductId();
	}
	@Resource(name = "redisTemplate")
	private ValueOperations<Long, ImgVo> titleImg;
	
	@RequestMapping("/getProductTitleImg/{procutId}")
	public void getProductTitleImg(HttpServletResponse response, @PathVariable Long procutId){
		ImgVo vo = titleImg.get(procutId);
		if(vo != null){
		
		}
		vo = productService.initProductImage(procutId);
		try {
			if ("png".equals(vo.getSuffix())) {
				response.setContentType("image/" + vo.getSuffix());
			} else {
				response.setContentType("image/jpeg");
			}
			if (vo.getContent() != null) {
				response.getOutputStream().write(vo.getContent());
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
