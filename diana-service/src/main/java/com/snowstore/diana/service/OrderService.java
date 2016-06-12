package com.snowstore.diana.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.foxinmy.weixin4j.exception.PayException;
import com.foxinmy.weixin4j.model.WeixinPayAccount;
import com.foxinmy.weixin4j.payment.PayUtil;
import com.foxinmy.weixin4j.payment.mch.MchPayPackage;
import com.foxinmy.weixin4j.payment.mch.PrePay;
import com.foxinmy.weixin4j.type.TradeType;
import com.foxinmy.weixin4j.util.MapUtil;
import com.foxinmy.weixin4j.util.Weixin4jConfigUtil;
import com.snowstore.diana.common.Calendars;
import com.snowstore.diana.common.Constants;
import com.snowstore.diana.common.DianaConstants;
import com.snowstore.diana.common.DianaConstants.DianaPlatform;
import com.snowstore.diana.config.JopClientProxy;
import com.snowstore.diana.config.WordConfig;
import com.snowstore.diana.domain.CardStock;
import com.snowstore.diana.domain.CardStock.Status;
import com.snowstore.diana.domain.Channel;
import com.snowstore.diana.domain.GroupRule;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Order.NoxNotify;
import com.snowstore.diana.domain.PayInfo;
import com.snowstore.diana.domain.PayInfo.PayType;
import com.snowstore.diana.domain.PayInfo.Platform;
import com.snowstore.diana.domain.Product;
import com.snowstore.diana.domain.Product.Type;
import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.domain.Tickets.OrderPayStatus;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.domain.User.Role;
import com.snowstore.diana.domain.UserGift;
import com.snowstore.diana.exception.CardStockPayException;
import com.snowstore.diana.repository.GroupRuleDao;
import com.snowstore.diana.repository.OrderDao;
import com.snowstore.diana.repository.PayInfoDao;
import com.snowstore.diana.repository.ProductDao;
import com.snowstore.diana.repository.TicketsDao;
import com.snowstore.diana.repository.UserGiftDao;
import com.snowstore.diana.result.OrderExportVo;
import com.snowstore.diana.service.userDetails.UserDetailsImpl;
import com.snowstore.diana.utils.DateUtils;
import com.snowstore.diana.utils.ExcelUitl;
import com.snowstore.diana.vo.OrderResultVo;
import com.snowstore.diana.vo.OrderVo;
import com.snowstore.diana.vo.OrderWapper;
import com.snowstore.diana.vo.PageFormVo;
import com.snowstore.diana.vo.PageTables;
import com.snowstore.diana.vo.Result;
import com.snowstore.hera.connector.vo.nox.D99000001;
import com.snowstore.poseidon.client.JopClient;
import com.snowstore.poseidon.client.exception.BusinessException;
import com.snowstore.poseidon.client.utils.JsonUtil;
import com.snowstore.poseidon.client.vo.request.OrderCancelReq;
import com.snowstore.poseidon.client.vo.request.OrderReq;
import com.snowstore.poseidon.client.vo.response.OrderCancelResp;
import com.snowstore.poseidon.client.vo.response.OrderResp;
import com.zendaimoney.hera.connector.vo.Datagram;

@Service
@Transactional
public class OrderService {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private JopClient jopClient;

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private TicketsDao ticketsDao;

	@Autowired
	private TicketsService ticketsService;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private UserGiftDao userGiftDao;

	@Autowired
	private PayInfoDao payInfoDao;

	@Autowired
	private CardStockService cardStockService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private UnionUserService unionUserService;

	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> payCodeCache;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private GroupRuleDao groupRuleDao;

	@Autowired
	private NoxSender noxSender;

	@Autowired
	private QuickPayService quickPayService;

	@Autowired
	private JopClientProxy jopClientProxy;

	@Autowired
	private TicketCodeService ticketCodeService;

	@Autowired
	private MovieCodeServiceV2 movieCodeServiceV2;

	/**
	 * 生成 电影票
	 * 
	 * @param order
	 *            订单信息
	 * @param product
	 *            产品信息
	 * @param user
	 *            当前用户
	 * @param unionId
	 *            当前用户的unionId
	 */
	public List<Tickets> generateTickets(Order order, Product product, User user, Long unionId) {
		List<Tickets> result = new ArrayList<Tickets>();
		LOGGER.info("开始生成电影票......");
		for (int i = 0; i < product.getExchangeAmount(); i++) {
			Tickets tickets = new Tickets();
			tickets.setPrice(product.getPrice().divide(new BigDecimal(product.getExchangeAmount()), new MathContext(2)));
			tickets.setType(product.getType());
			tickets.setRefOrder(order.getId());
			tickets.setUserId(user.getId());
			tickets.setUnionId(unionId);
			tickets.setCode(generatorCode());
			tickets.setStatus(Tickets.Status.初始.name());
			tickets.setTimeOut(Calendars.StringToDate("2016-12-31"));
			tickets.setOrderPayStatus(OrderPayStatus.UNPAID.name()); // 火票订单支付状态(未支付)
			tickets = ticketsDao.save(tickets);
			result.add(tickets);
		}
		order.setTicketNum(product.getExchangeAmount());
		orderDao.save(order);
		LOGGER.info("生成电影票[{}]张", product.getExchangeAmount());
		return result;
	}

	/**
	 * 裸票电影票生产规则
	 * 
	 * @param map
	 */
	public List<Tickets> generateTickets(Long ruleId, String productType, BigDecimal price, Integer tickeNumber, Order order, Long unionId) {
		LOGGER.info("ruleId:{}", ruleId);
		List<Tickets> result = new ArrayList<Tickets>();
		LOGGER.info("开始生成电影票......");
		for (int i = 0; i < tickeNumber; i++) {
			Tickets tickets = new Tickets();
			tickets.setPrice(price.divide(new BigDecimal(tickeNumber), new MathContext(2)));
			tickets.setType(productType);
			tickets.setRefOrder(order.getId());
			tickets.setUserId(customerService.getCurrentUser().getId());
			tickets.setUnionId(unionId);
			tickets.setCode(generatorCode());
			tickets.setStatus(Tickets.Status.初始.name());
			tickets.setTimeOut(Calendars.StringToDate("2016-12-31"));
			tickets.setOrderPayStatus(OrderPayStatus.UNPAID.name()); // 火票订单支付状态(未支付)
			tickets = ticketsDao.save(tickets);
			result.add(tickets);
		}
		order.setTicketNum(tickeNumber);
		orderDao.save(order);
		LOGGER.info("生成电影票[{}]张", tickeNumber);
		return result;
	}

	/**
	 * 礼品卡下订单(不生成易联订单）
	 * 
	 * @author XieZG
	 * @Date:2016年1月26日下午5:10:48
	 * @return
	 */
	public Result<Order> giftCardOrder(Long productId, HttpServletRequest req) {
		Result<Order> result = new Result<Order>(Result.Type.FAILURE);
		User user = customerService.getCurrentUser();
		Long unionId = unionUserService.findOrAdd(user.getMobile());
		Product product = productService.get(productId);
		List<Order> orders = orderDao.findByUserAndStatus(user, Order.Status.待付款.name());
		if (orders.size() > 1) {
			throw new RuntimeException("订单异常存在多个未支付订单");
		} else if (orders.size() == 1) {
			result.setData(orders.get(0));
			result.setType(Result.Type.SUCCESS);
			return result;
		}
		Order order = new Order();
		order.setAmount(product.getPrice());
		order.setUser(user);
		order.setStatus(Order.Status.待付款.name()); // 待付款
		order.setType(product.getType());
		order.setProductId(product.getId());
		order.setProductName(product.getName());
		order.setTicketNum(product.getExchangeAmount());
		order.setUnionId(unionId); // 设置unionId
		order.setGitfNum(product.getGiftNum());
		order = orderDao.save(order);
		result.setType(Result.Type.SUCCESS);
		result.setData(order);
		return result;
	}

	/**
	 * 下订单
	 * @author wulinjie,wangtengfei
	 * @param productId 产品ID
	 * @return 订单信息
	 */
	public Result<Order> generateOrder(Long productId, HttpServletRequest req) {
		Result<Order> result = new Result<Order>(Result.Type.FAILURE);
		User user = customerService.getCurrentUser();
		Long unionId = unionUserService.findOrAdd(user.getMobile());
		Product product = productService.get(productId);
		if (!Product.Status.在售.equals(product.getStatus())) {
			result.addMessage("剩余份额不足本次购买，敬请期待下期产品。");
			return result;
		}
		Object ruleObj = req.getSession().getAttribute("ruleId");
		GroupRule rule = null;
		BigDecimal orderAmount = product.getPrice();
		Integer giftNum = product.getGiftNum();
		if (!Product.Type.多张组合.name().equals(product.getType())) {

		} else if (null != ruleObj) {
			Long ruleId = (Long) ruleObj;
			rule = groupRuleDao.findOne(ruleId);
			orderAmount = rule.getPrice();
			giftNum = 0;
		}
		List<Order> orders = orderDao.findByUserAndStatus(user, Order.Status.待付款.name());
		if (orders.size() > 1) {
			throw new RuntimeException("订单异常存在多个未支付订单");
		} else if (orders.size() == 1) {
			result.setData(orders.get(0));
			result.setType(Result.Type.WARNING);
			result.addMessage("您有未支付订单，请先支付。");
			return result;
		}
		LOGGER.info("调用开放平台下单.....");
		BigDecimal availableAmount = product.getAvailableAmount().subtract(orderAmount);

		if (BigDecimal.ZERO.compareTo(availableAmount) > 0) {
			result.addMessage("剩余份额不足本次购买，敬请期待下期产品。");
			product.setStatus(Product.Status.售罄);
			productService.saveOrUpdate(product);
			return result;
		}
		product.setAvailableAmount(availableAmount);
		if (product.getAvailableAmount().compareTo(BigDecimal.valueOf(100)) < 0) {
			product.setStatus(Product.Status.售罄);
		}
		Order order = new Order();
		order.setAmount(orderAmount);
		order.setUser(user);
		order.setStatus(Order.Status.待付款.name()); // 待付款
		order.setType(product.getType());
		order.setProductId(productId);
		order.setProductName(product.getName());
		order.setUnionId(unionId); // 设置unionId
		order.setGitfNum(giftNum); // 影视类产品默认一份礼品
		order.setNoxNotify(NoxNotify.WAIT.name()); // 等待推送到分销系统
		order = orderDao.save(order);
		result.setType(Result.Type.SUCCESS);
		product = productDao.save(product);
		result.setData(order);
		if (null == ruleObj) {
			generateTickets(order, product, user, unionId);
			return result;
		}

		if (null == rule) {
			generateTickets(order, product, user, unionId);
			return result;
		} else {
			// 生成电影票
			generateTickets(rule.getId(), product.getType(), rule.getPrice(), rule.getTicketNumber(), order, unionId);
			return result;
		}
	}

	/**
	 * 生成订单（电影票转礼品）
	 * @author wulinjie
	 * @param productId  产品ID
	 * @param ticketsId  电影票ID
	 * @param req
	 * @return
	 */
	public Result<Order> generateOrderForGift(Long productId, Long ticketsId, HttpServletRequest req) {
		Result<Order> result = new Result<Order>(Result.Type.FAILURE);
		User user = customerService.getCurrentUser();
		Long unionId = unionUserService.findOrAdd(user.getMobile());
		Product product = productService.get(productId);
		Tickets tickets = ticketsService.get(ticketsId);
		if (!Product.Status.在售.equals(product.getStatus())) {
			result.addMessage("剩余份额不足本次购买，敬请期待下期产品。");
			return result;
		}
		BigDecimal orderAmount = product.getPrice();
		Integer giftNum = product.getGiftNum();
		//生成订单
		Order order = new Order();
		order.setAmount(orderAmount);
		order.setUser(user);
		order.setStatus(Order.Status.已付款.name()); // 已付款
		order.setType(product.getType());
		order.setProductId(productId);
		order.setProductName(product.getName());
		order.setUnionId(unionId); // 设置unionId
		order.setTicketNum(0);
		order.setGitfNum(giftNum); // 影视类产品默认一份礼品
		order.setNoxNotify(NoxNotify.WAIT.name()); // 等待推送到分销系统
		order = orderDao.save(order);
		//生成预付单
		createPrePayForGift(order, order.getAmount());
		//电影票关联子订单
		tickets.setSubOrder(order);
		ticketsService.saveOrUpdate(tickets);
		//返回
		result.setType(Result.Type.SUCCESS);
		result.setData(order);
		return result;
	}


	/**
	 * @param orderId 订单id
	 * @param count 份数
	 * @param seatId 门票类型 席位类型ID
	 * @param req
	 * @return
	 */
	@Transactional(readOnly = false)
	public Result<JSONObject> productBuy(Long orderId, Integer count, Long seatId, HttpServletRequest req, int payType) {
		LOGGER.info("开始支付订单,orderId:{}", orderId);
		Result<JSONObject> result = new Result<JSONObject>(Result.Type.FAILURE);
		Order order = orderDao.findOne(orderId);

		if (!Order.Status.待付款.name().equals(order.getStatus())) {
			result.addMessage("订单状态为" + order.getStatus() + "不可进行支付");
			return result;
		}
		// ③请求支付
		try {
			UserDetailsImpl userDetails = customerService.getUserDetails();
			// 快捷支付不支持混合支付
			if (PayType.快捷支付.ordinal() == payType) {
				if (order.getReferenceOrder() == null) {
					result.addMessage("请先发送验证码！");
					return result;
				}
				result = quickPayService.quickPay(order, req, userDetails, order.getAmount());
			} else {
				BigDecimal payAmount = cardStockService.cardStockPay(order, req, result);
				if(!result.success()){
					return result;
				}
				// 非快捷支付解除易联订单关联，否则后台job同步订单到易联会不处理
				order.setReferenceOrder(null);
				orderDao.save(order);
				// 卡券流程
				if (order.getAmount().compareTo(payAmount) == 0) {
					payAmount = channelPaySpec(payAmount, order, req);// 乐天玛特优惠
					// payAmount = ruleAmountPay(payAmount, order, req); //未使用卡券
				}
				PayInfo payInfo = payInfoDao.findOne(orderId);
				if (payAmount.compareTo(BigDecimal.ZERO) > 0) {
					// 卡券抵扣后有剩余，根据具体支付类型支付
					// 微信支付。
					JSONObject prePay = createPrePay(order, req, userDetails, payAmount);
					LOGGER.info(prePay.toString());
					result.setData(prePay);
					result.setType(Result.Type.SUCCESS);
				} else if (null != payInfo) {
					// 卡券全额抵扣，取消 微信支付关联
					payInfo.setOrderId(null);
					payInfoDao.save(payInfo);
					result.setType(Result.Type.SUCCESS);
				}
			}
			// 返回信息增加订单类型
			result.getData().put("orderType", order.getType());
		} catch (CardStockPayException c) {
			result.addMessage(c.getMessage());
			throw new RuntimeException("生成预付单失败....", c);
			//return result;
		} catch (Exception e) {
			LOGGER.error("生成预付单失败....", e);
			throw new RuntimeException("生成预付单失败....", e);
		}
		return result;
	}

	/**
	 * 添加订单
	 * 
	 * @param order
	 * @return
	 */
	public Order add(Order order) {
		return orderDao.save(order);
	}

	public Iterable<Order> saveOrUpdate(List<Order> orders) {
		return orderDao.save(orders);
	}

	public void saveOrUpdate(Order order) {
		orderDao.save(order);
	}

	/***
	 * 获取订单信息(分页获取用户数据)
	 * 
	 * @param order
	 * 
	 * @param form
	 *            分页对象
	 * @return 用户信息列表
	 */
	public Page<Order> findOrderList(final OrderVo order, PageFormVo form) {
		return orderDao.findAll(buildSpecification(order), form);

	}

	/**
	 * 构建查询对象
	 * 
	 * @author XieZG
	 * @Date:2016年1月6日下午2:50:02
	 * @param order
	 * @return
	 */
	private Specification<Order> buildSpecification(final OrderVo order) {
		return new Specification<Order>() {

			@Override
			public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				List<Channel> channelList = new ArrayList<Channel>();
				List<String> channelCodeList = new ArrayList<String>();
				if (order.getPlatformId() != null) {
					channelList = channelService.getSubChannelByParentIdRecursively(order.getPlatformId());
				} else {
					User user = customerService.getCurrentUser();
					if (Role.CHANNEL == Role.valueOf(user.getRole())) {
						Channel channel = channelService.getChannelByCode(user.getPlatform());
						channelList = channelService.getSubChannelByParentIdRecursively(channel.getId());
					} else {
						channelList = channelService.getAllChannel();
					}
				}

				if (!channelList.isEmpty()) {
					for (Channel channel : channelList) {
						channelCodeList.add(channel.getCode());
					}
				}

				// 查询当前平台的数据
				list.add(root.<User> get("user").<String> get("platform").in(channelCodeList));
				list.add(cb.notEqual(root.<String> get("type"), Product.Type.电影转礼品.name()));
				
				if (order != null) {
					// 按照姓名模糊查询
					if (StringUtils.isNotEmpty(order.getProductName())) {
						list.add(cb.like(root.<String> get("productName"), '%' + order.getProductName() + '%'));
					}else{
						list.add(cb.notEqual(root.<String> get("type"), Product.Type.电影转礼品.name()));
					}
					// 电话号码
					if (StringUtils.isNotEmpty(order.getMobile())) {
						list.add(cb.like(root.<User> get("user").<String> get("mobile"), '%' + order.getMobile() + '%'));
					}
					// 订单状态
					if (StringUtils.isNotEmpty(order.getStatus()) && !order.getStatus().equals("全部")) {
						list.add(cb.equal(root.<String> get("status"), order.getStatus()));
					}
					// 按照时间段查询
					if (order.getBeginDate() != null && order.getEndDate() != null) {
						list.add(cb.greaterThanOrEqualTo(root.<Date> get("createdDate"), order.getBeginDate()));
						list.add(cb.lessThanOrEqualTo(root.<Date> get("createdDate"), order.getEndDate()));
					}
				}

				Predicate[] predicate = new Predicate[list.size()];
				return cb.and(list.toArray(predicate));
			}
		};
	}

	/**
	 * 
	 * 统计用户下单购买次数
	 * 
	 * @param user
	 * @return
	 */
	public Long findByUserOrderCount(User user) {
		return orderDao.findByUserOrderCount(user, Order.Status.已付款.name());
	}

	/**
	 * 统计用户消费总金额
	 * 
	 * @param user
	 * @return
	 */
	public BigDecimal findByUserOrderTotalAmount(User user) {
		return orderDao.findByUserOrderTotalAmount(user, Order.Status.已付款.name());
	}

	/**
	 * 查询用户的订单
	 * 
	 * @param user
	 *            用户对象
	 * @param form
	 *            分页对象
	 * @return
	 */
	public Page<Order> findByUserOrder(User user, String type, PageFormVo form) {
		return orderDao.findByUserOrder(user, type, form);
	}

	/**
	 * 根据用户和订单状态，查询订单集合
	 * 
	 * @author wulinjie
	 * @param user
	 *            用户
	 * @param status
	 *            订单状态
	 * @return 订单集合
	 */
	public List<Order> findByUserAndStatus(User user, String status) {
		return orderDao.findByUserAndStatus(user, status);
	}

	/**
	 * 查询用户的订单 （无Type参数）
	 * 
	 * @param user
	 *            用户对象
	 * @param form
	 *            分页对象
	 * @return
	 */
	public Page<Order> findByUserOrder(User user, PageFormVo form) {
		return orderDao.findByUserOrder(user, form);
	}

	/**
	 * 导出订单数据
	 * 
	 * @param request
	 * @param response
	 * @param order
	 *            查询条件
	 * @return
	 * @throws Exception
	 */
	public void exportOrder(final HttpServletRequest request, HttpServletResponse response, final OrderVo order) {
		try {
			Map<String, String> headers = new LinkedHashMap<String, String>();
			headers.put("id", "编号");
			headers.put("tradeNo", "商户订单号");
			headers.put("mobile", "绑定手机");
			headers.put("platform", "所属渠道");
			headers.put("productName", "购买产品");
			headers.put("amount", "订单金额");
			headers.put("payAmount", "支付金额");
			headers.put("payPlatform", "支付类型");
			headers.put("cardStoneAmount", "代金券金额");
			headers.put("memo", "代金券类型");
			headers.put("status", "订单状态");
			headers.put("createdDate", "提交时间");
			headers.put("ticketNum", "票数");
			headers.put("giftNum", "领取礼品");
			List<OrderExportVo> list = queryOrder(order.getMobile(), order.getBeginDate(), order.getEndDate(), order.getProductName(), order.getStatus(), order.getPlatformId());
			ExcelUitl.exportExcel("订单信息" + DateUtils.dateToString(new Date(), "yyMMdd") + ".xlsx", headers, list, request, response, "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			LOGGER.error("订单导出异常 ", e);
		}

	}

	/** 产生电影票兑换码 */
	public String generatorCode() {
		DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		String currentDate = dateFormat.format(new Date());
		StringBuilder code = new StringBuilder();
		String random = RandomStringUtils.randomAlphanumeric(4).toUpperCase();
		code.append("Y").append(currentDate.substring(1, currentDate.length())).append(random);
		LOGGER.info("电影票兑换码:{}", code.toString());
		return code.toString();
	}

	/**
	 * @Title: findOders
	 * @Description: (查询我的订单信息)
	 * @author wangyunhao
	 * @date 2015年10月21日 上午11:17:30
	 * @return Result<ArrayList<Order>> 返回类型
	 */
	public List<OrderWapper> findOrders() {
		LOGGER.info("开始查询我的订单信息......");

		List<Order> orders = null;
		List<OrderWapper> result = new ArrayList<OrderWapper>();
		List<String> status = new ArrayList<String>();
		status.add(Order.Status.待付款.name());
		status.add(Order.Status.已付款.name());
		status.add(Order.Status.付款中.name());
		status.add(Order.Status.付款失败.name());

		User user = customerService.getCurrentUser();
		if (DianaPlatform.WAP.name().equals(user.getPlatform())) {
			Long unionId = unionUserService.findOrAdd(user.getMobile());
			orders = orderDao.findByUnionIdAndStatusInOrderByCreatedDateDesc(unionId, status);
		} else {
			List<Long> userIdList = customerService.loadByPlatformNoCorrespond();
			orders = orderDao.findByUserInAndStatusInOrderByCreatedDateDesc(userIdList, status);
		}

		if (!orders.isEmpty()) {
			for (Order order : orders) {
				if(Product.Type.电影转礼品.name().equals(order.getType())){
					continue;
				}
				OrderWapper wapper = new OrderWapper();
				if (Order.Status.待付款.toString().equals(order.getStatus()) || Order.Status.付款中.toString().equals(order.getStatus()) || Order.Status.已付款.toString().equals(order.getStatus())) {
					List<UserGift> usergift = userGiftDao.findByOrderId(order.getId());
					if (order.getGitfNum() == null || order.getGitfNum().equals(0)) {
						wapper.setPickallgiftornot(false);
					} else if (usergift.isEmpty()) {
						wapper.setPickallgiftornot(true);// 订单有礼品但是没有领取：true;
					} else {
						wapper.setPickallgiftornot(false);
					}
					
					if (wapper.getPickallgiftornot() != true) {
						List<Tickets> userTickets = ticketsService.findByOrderId(order.getId());
						for (int i = 0; i < userTickets.size(); i++) {
							if (!("已兑换".equals(userTickets.get(i).getStatus()))) {
								if ((userTickets.get(i).getSubOrder() != null)) {
									List<UserGift> usergiftTrans = userGiftDao.findByOrderId(userTickets.get(i).getSubOrder().getId());
									if (usergiftTrans.isEmpty()) {// 1.ticket状态不是已兑换，2.并且有suborder内容，3.且如果对应的订单也没有礼品，则红点提示
										wapper.setPickallgiftornot(true);
										break;
									} else {// ticket状态不是已兑换，但是有礼品，不提示
										wapper.setPickallgiftornot(false);
										continue;
									}
								} else {// 没有子订单，并且Ticket的状态为非“已兑换”
									wapper.setPickallgiftornot(true);
									break;
								}
							}
						}
					}

				}

				wapper.setOrder(order);
				result.add(wapper);
			}
		}
		return result;
	}

	/**
	 * @Title: findOrderById
	 * @Description: (根据订单id查询订单详细信息)
	 * @author wangyunhao
	 * @date 2015年10月21日 下午5:29:38
	 * @return Order 返回类型
	 */
	public Map<String, Object> findOrderById(Long id) {
		LOGGER.info("开始查询订单详细信息......");
		Map<String, Object> orderDetailMaps = new HashMap<String, Object>();
		try {
			Order order = getUserOrderById(id);// 订单信息
			if (null == order) {
				throw new RuntimeException("查询订单详细信息失败！");
			}
			// Package packageDelivery =
			// packageService.findPackageByOrderId(id);// 配送信息
			List<Tickets> tickets = ticketsService.findTicketByOrderId(id);// 票据信息
			Tickets ticketsConcert = null;
			if (order != null && "演唱会".equals(order.getType())) {
				ticketsConcert = tickets.get(0);
			}
			orderDetailMaps.put("order", order);
			// orderDetailMaps.put("packageDelivery", packageDelivery);
			orderDetailMaps.put("tickets", tickets);
			orderDetailMaps.put("ticketsConcert", ticketsConcert);
		} catch (Exception e) {
			LOGGER.error("查询订单详细信息失败！" + e);
		}
		return orderDetailMaps;
	}

	/**
	 * 生成预付单
	 * 
	 * @param order
	 * @param req
	 * @return
	 * @throws PayException
	 */
	private JSONObject createPrePay(Order order, HttpServletRequest req, UserDetailsImpl user, BigDecimal payAmount) throws PayException {
		String userDevice = customerService.getUserDevice(req);
		if (DianaConstants.DianaDevice.JUPITER.name().equals(userDevice)) {
			return weixnJsPay(order, req, user, TradeType.JSAPI, payAmount);
		} else if (DianaConstants.DianaDevice.PROMETHEUS.name().equals(userDevice)) {
			return weixinAppPay(order, req, user, payAmount);
		} else {
			return weixnJsPay(order, req, user, TradeType.NATIVE, payAmount);
		}

	}

	/**
	 * 生成预付单（电影票转礼品）
	 * @author wulinjie
	 * @param order		订单信息
	 * @param payAmount	支付金额 默认0
	 * @return
	 */
	private PayInfo createPrePayForGift(Order order, BigDecimal payAmount){
		PayInfo payInfo = payInfoDao.findByOrderId(order.getId());
		if (null == payInfo) {
			payInfo = new PayInfo();
			payInfo.setOrderId(order.getId());
			payInfo.setStatus(PayInfo.Status.未支付.name());
		}else if (PayInfo.Status.支付成功.name().equals(payInfo.getStatus())) {
			throw new RuntimeException("该订单已经支付成功，不可重复支付。");
		}
		payInfo.setTradeNo(generatePayCode());
		payInfo.setPayPlatform(Platform.OMIT_PAY.name());
		payInfo.setPayAmount(payAmount);
		payInfo = payInfoDao.save(payInfo);
		return payInfo;
	}

	/**
	 * 支持微信公众号支付 扫码支付一
	 * 
	 * @param order
	 * @param req
	 * @param user
	 * @param tradeType
	 * @return
	 * @throws PayException
	 */
	private JSONObject weixnJsPay(Order order, HttpServletRequest req, UserDetailsImpl user, TradeType tradeType, BigDecimal payAmount) throws PayException {
		if (TradeType.JSAPI.equals(tradeType) || TradeType.NATIVE.equals(tradeType)) {
			//
		} else {
			throw new RuntimeException("本接口只支持 微信公众号支付和扫码支付");
		}
		PayInfo payInfo = payInfoDao.findByOrderId(order.getId());
		if (null == payInfo) {
			payInfo = new PayInfo();
			payInfo.setOrderId(order.getId());
			payInfo.setStatus(PayInfo.Status.未支付.name());
		} else if (PayInfo.Status.支付成功.name().equals(payInfo.getStatus())) {
			throw new RuntimeException("该订单已经支付成功，不可重复支付。");
		}
		payInfo.setTradeNo(generatePayCode());
		payInfo.setPayPlatform(PayInfo.Platform.WEIXIN_PAY_JS_API.name());
		WeixinPayAccount weixinAccount = JSON.parseObject(Weixin4jConfigUtil.getValue("account"), WeixinPayAccount.class);
		String openId = (String) req.getSession().getAttribute("openId");
		MchPayPackage payPackage = new MchPayPackage(weixinAccount, openId, order.getProductName(), "附加数据", payInfo.getTradeNo(), payAmount.doubleValue(), System.getProperty("diana.domain") + "/weixin/notify", req.getRemoteHost(), tradeType);
		PrePay prePay = PayUtil.createPrePay(payPackage, weixinAccount.getPaySignKey());
		payInfo.setReq(MapUtil.toJoinString(payPackage, false, false, null));
		payInfo.setRes(MapUtil.toJoinString(prePay, false, false, null));
		payInfo.setPayAmount(payAmount);
		payInfoDao.save(payInfo);
		long timeStamp = new Date().getTime();
		JSONObject obj = new JSONObject();
		obj.put("appId", "wx7cad5afccc10fa51");
		obj.put("nonceStr", "e61463f8efa94090b1f366cccfbbb444");
		obj.put("package", "prepay_id=" + prePay.getPrepayId());
		obj.put("signType", "MD5");
		obj.put("timeStamp", timeStamp + "");
		String paySign = PayUtil.paysignMd5(obj, weixinAccount.getPaySignKey());// 微信支付KEY
		obj.put("paySign", paySign);
		obj.put("orderId", order.getId());
		obj.put("codeUrl", prePay.getCodeUrl());
		return obj;

	}

	/**
	 * app支付需要提供app支付的账号
	 * 
	 * @param order
	 * @param req
	 * @param user
	 * @return
	 * @throws PayException
	 */
	private JSONObject weixinAppPay(Order order, HttpServletRequest req, UserDetailsImpl user, BigDecimal payAmount) throws PayException {
		PayInfo payInfo = payInfoDao.findByOrderId(order.getId());
		if (null == payInfo) {
			payInfo = new PayInfo();
			payInfo.setOrderId(order.getId());
			payInfo.setTradeNo(generatePayCode());
			payInfo.setStatus(PayInfo.Status.未支付.name());
		} else if (PayInfo.Status.支付成功.name().equals(payInfo.getStatus())) {
			throw new RuntimeException("该订单已经支付成功，不可重复支付。");
		}
		payInfo.setPayPlatform(PayInfo.Platform.WEIXIN_PAY_APP.name());
		WeixinPayAccount weixinAccount = new WeixinPayAccount("wx739f4b27565b1447", null, "liasdf98q3i4hasudyf8q3hruasdpf8h", "1280515201");
		MchPayPackage payPackage = new MchPayPackage(weixinAccount, null, order.getProductName(), "附加数据", payInfo.getTradeNo(), payAmount.doubleValue(), System.getProperty("diana.domain") + "/weixin/notify", req.getRemoteHost(), TradeType.APP);
		PrePay prePay = PayUtil.createPrePay(payPackage, weixinAccount.getPaySignKey());
		payInfo.setReq(MapUtil.toJoinString(payPackage, false, false, null));
		payInfo.setRes(MapUtil.toJoinString(prePay, false, false, null));
		payInfo.setPayAmount(payAmount);
		payInfoDao.save(payInfo);
		long timeStamp = new Date().getTime() / 1000;
		JSONObject obj = new JSONObject();
		obj.put("appid", weixinAccount.getId());
		obj.put("noncestr", "e61463f8efa94090b1f366cccfbbb444");
		obj.put("package", "Sign=WXPay");
		obj.put("partnerid", weixinAccount.getMchId());
		obj.put("prepayid", prePay.getPrepayId());
		obj.put("timestamp", timeStamp + "");
		String paySign = PayUtil.paysignMd5(obj, weixinAccount.getPaySignKey());// 微信支付KEY
		obj.put("sign", paySign);
		obj.put("orderId", order.getId() + "");
		return obj;
	}

	/**
	 * 封装订单Vo对象
	 * 
	 * @param orderList
	 * @return
	 */
	public List<OrderVo> getOrderVo(List<Order> orderList) {
		List<OrderVo> orderVoList = new ArrayList<OrderVo>();
		try {
			OrderVo orderVo = null;
			for (Order order : orderList) {
				orderVo = new OrderVo();
				BeanUtils.copyProperties(orderVo, order);
				orderVo.setMobile(order.getUser().getMobile());
				orderVo.setPlatform(channelService.getChannelByCode(order.getUser().getPlatform()).getName());
				orderVo.setAlreadyConversion(order.getTicketNum() == null ? 0 : order.getTicketNum());
				PayInfo payInfo = null;

				payInfo = payInfoDao.findByOrderId(order.getId());
				// 卡券全额支付不生成PayInfo对象
				if (payInfo == null) {
					orderVo.setPayAmount(BigDecimal.ZERO);
				} else if (PayInfo.Status.支付成功.name().equals(payInfo.getStatus())) {
					orderVo.setTradeNo(payInfo.getTradeNo());
					orderVo.setPayAmount(payInfo.getPayAmount());
				} else {
					orderVo.setPayAmount(BigDecimal.ZERO);
				}
				
				//已付款订单设置支付类型
				if(com.snowstore.diana.domain.Order.Status.已付款.name().equals(order.getStatus())){
					if(payInfo==null||!com.snowstore.diana.domain.PayInfo.Status.支付成功.name().equals(payInfo.getStatus())){
						//卡券全额支付
						orderVo.setPayType("卡券全额抵扣");
					} else if (Platform.WEIXIN_PAY_APP.name().equals(payInfo.getPayPlatform()) || Platform.WEIXIN_PAY_JS_API.name().equals(payInfo.getPayPlatform())) {
						// 微信支付
						orderVo.setPayType("微信支付");
					}else if(Platform.QUICK_PAY.name().equals(payInfo.getPayPlatform())){
						//快捷支付
						orderVo.setPayType("快捷支付");
					}
				}
				// 目前只允许使用一张卡券
				List<CardStock> cardStocks = cardStockService.findByOrderId(order.getId());
				CardStock cardStock = cardStocks.isEmpty() ? null : cardStocks.get(0);
				if (cardStocks.size() > 0) {
					String memo = cardStocks.get(0).getMemo();
					orderVo.setCardStockMemo(memo == null ? "代金券" : memo);
				}
				// 历史订单没有payAmount属性,统一设置与订单金额相等
				if (orderVo.getPayAmount() == null) {
					orderVo.setPayAmount(orderVo.getAmount());
				}

				if (order.getStatus().equals("已付款")) {
					orderVo.setCardStockAmount(null == cardStock ? BigDecimal.ZERO : cardStock.getAmount());
				} else {
					orderVo.setCardStockAmount(BigDecimal.ZERO);
				}

				// 查询订单关联的礼品信息
				List<UserGift> list = userGiftDao.findByOrderId(order.getId());
				orderVo.setGift(list == null ? 0 : list.size());
				orderVoList.add(orderVo);
			}
		} catch (Exception e) {
			LOGGER.error("封装订单VO对象异常", e);
		}
		return orderVoList;
	}

	/**
	 * 根据id加载订单
	 * 
	 * @param id
	 * @return
	 */
	public Order getUserOrderById(Long id) {
		Long unionId = unionUserService.findOrAdd(customerService.getUserDetails().getMobile());
		return orderDao.findByUnionIdAndId(unionId, id);
	}

	public Order get(Long id) {
		return orderDao.findOne(id);
	}

	/**
	 * 支付成功更新订单状态
	 * 
	 * @param orderId
	 * @return
	 */
	public Order orderPaySuccess(Long orderId) {
		Order order = orderDao.findOne(orderId);

		order.setStatus(Order.Status.已付款.name());
		//String messageString = "恭喜您成为电影投资人，获得《叶问三》观影权益，更多礼品、院线不断更新中，敬请关注。客服电话：400-6196-828, 我们将竭诚为您服务。";
		//messageService.paySuccessMessageNotify(orderId, messageString);// 短信通知用户
		ticketCodeService.asyncUpdateTicketCodeService(orderId); // 获取兑换码
		movieCodeServiceV2.confirmOrderStatus(order); // 确认订单
		return order;
	}

	/**
	 * 查询支付成功未通知开放平台的订单
	 * 
	 * @param notify
	 *            订单标识
	 * @return
	 */
	public List<Long> findByNotify(String notify, String status) {
		return orderDao.findByNotify(notify, status);
	}

	/**
	 * 根据订单状态查询订单
	 * 
	 * @param status
	 * @return
	 */
	public List<Order> findByStatus(String status) {
		return orderDao.findByStatus(status);
	}

	/**
	 * 调用开放平台撤单
	 * 
	 * @param order
	 * @return
	 */
	public Order cancleOrder(Order order) {
		LOGGER.info("开始取消订单：" + order.getId());
		// 通知开放平台撤单
		OrderCancelReq req = new OrderCancelReq();
		req.setOrderCode(order.getReferenceOrder());
		OrderCancelResp resp = null;

		try {
			if (null != order.getReferenceOrder()) {
				resp = jopClient.req(req);
				LOGGER.info("撤销订单响应:" + JsonUtil.objectToJson(resp));
			}
		} catch (BusinessException e) {
			// 该订单已处于已撤销状态，不能进行撤单操作！
			// 因为属于撤单操作，可以忽略该异常
			LOGGER.error("调用开放平台撤单异常,订单ID：" + order.getReferenceOrder(), e);
		}
		// 释放卡券
		releaseOrderAndCardStock(order);
		LOGGER.info("成功撤销订单[{}]", order.getId());
		return order;
	}

	/**
	 * 释放订单和抵扣券
	 * 
	 * @author wulinjie
	 * @param order
	 *            订单信息
	 */
	private void releaseOrderAndCardStock(Order order) {
		// 修改订单状态
		order.setStatus(Order.Status.已撤单.name());
		order = orderDao.save(order);

		// 释放抵扣券
		List<CardStock> cardStocks = cardStockService.findByOrderId(order.getId());
		if (!cardStocks.isEmpty()) {
			for (CardStock cardStock : cardStocks) {
				if (CardStock.Type.裸票抽奖券.name().equals(cardStock.getType())) {
					continue;
				}
				cardStock.setStatus(Status.未使用.name());
				cardStock.setOrderId(null);
				cardStockService.saveOrUpdate(cardStock);
			}
		}

		// 更新理财产品可用额度
		Product product = productService.get(order.getProductId());
		product.setAvailableAmount(product.getAvailableAmount().add(order.getAmount()));
		if (Product.Status.售罄.equals(product.getStatus()) && product.getIsAvailable()) {
			product.setStatus(Product.Status.在售);
		}
		productService.saveOrUpdate(product);
	}

	/**
	 * 开放平台下单（自动补单）
	 * @param order
	 */
	public Order generateOrderFromAtlantisa(Order order) {
		Product product = productDao.findOne(order.getProductId());
		OrderReq req = new OrderReq();
		req.setName("票务系统");
		req.setCertiNum("412828199009124214");
		req.setCertiType("身份证");
		req.setAddress(order.getId() + "");
		req.setFinanceProductId(product.getReferenceProduct() + "");
		req.setOrderAmt(order.getAmount());
		req.setProductName(order.getProductName());
		// 下单获取返回参数
		OrderResp orderResp = null;
		try {
			orderResp = jopClient.req(req);
			if (orderResp != null) {
				LOGGER.info("下单接收报文：" + JSONObject.toJSON(orderResp));
			}
		} catch (BusinessException e) {
			LOGGER.error("下单失败", e);
			throw new RuntimeException("下单失败", e);
		}
		order.setReferenceOrder(orderResp.getOrderCode());
		return orderDao.save(order);
	}

	/**
	 * 开放平台下单(快捷支付)
	 * @param order
	 */
	public String generateOrderFromAtlantisa(Order order, String name, String certiNum, String certiType) {
		String errorMsg = null;
		Product product = productDao.findOne(order.getProductId());
		OrderReq req = new OrderReq();
		req.setName(name);
		req.setCertiNum(certiNum);
		req.setCertiType("身份证");
		req.setFinanceProductId(product.getReferenceProduct() + "");
		req.setOrderAmt(order.getAmount());
		req.setProductName(order.getProductName());
		// 下单获取返回参数
		OrderResp orderResp = null;
		try {
			orderResp = (OrderResp) jopClientProxy.req(req);
			order.setReferenceOrder(orderResp.getOrderCode());
			orderDao.save(order);
		} catch (BusinessException e) {
			LOGGER.error("下单失败:" + e);
			errorMsg = Constants.JLFEX_ERROR.get(e.getStatus());
		}

		return errorMsg;
	}

	public List<Long> getUnSynchronizeOrder() {
		return orderDao.findIdByStatusAndReferenceOrderIsNullAndTypeNot(Order.Status.已付款.name(), Type.电影转礼品.name());
	}

	/**
	 * 生成支付code
	 * 
	 * @return
	 */
	public String generatePayCode() {
		Random random = new Random();
		StringBuilder payCode = new StringBuilder(Calendars.format(new Date(), "yyyyMMddHHmmsss"));
		if (!"pro".equals(System.getProperty("diana.env"))) {
			payCode.append(39);
		} else {
			payCode.append(77);
		}
		payCode.append(random.nextInt(1000000));

		String cacheCode = payCodeCache.get(payCode);
		if (null == cacheCode) {
			payCodeCache.set(payCode.toString(), payCode.toString(), 1, TimeUnit.SECONDS);
			return payCode.toString();
		} else {
			return generatePayCode();
		}
	}

	/**
	 * 查询待发货订单
	 * 
	 * @return
	 */
	public Page<Order> findUndeliveredOrders(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return orderDao.findUndeliveredOrders(DateUtils.getCurrenDate(), pageRequest);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public PageTables<OrderResultVo> queryOrder(String mobile, String beginDate, String endDate, String platform, Integer pageNo, Integer pageSize) {
		PageTables<OrderResultVo> page = new PageTables<OrderResultVo>();
		Query query;
		StringBuilder builder = new StringBuilder();
		builder.append("select ");
		builder.append("t.id,");
		builder.append("u.mobile,");
		builder.append("t.amount,");
		builder.append("t.product_name,");
		builder.append("t.status,");
		builder.append("t.created_date,");
		builder.append("t.ticket_num, ");
		builder.append("t.type, ");
		builder.append("case when (t.amount - nvl(c.amount, 0))<0 then 0 else t.amount - nvl(c.amount, 0) end  as pay_amount ");
		builder.append("from diana_order t ");
		builder.append("left join diana_user u on u.id=t.user_id ");
		builder.append("left join diana_card_stock c on c.order_id=t.id ");
		builder.append("where t.status='已付款' ");
		builder.append("and u.platform='").append(platform).append("'");
		builder.append("and t.product_name!='电影转礼品'");
		if (!StringUtils.isEmpty(mobile)) {
			builder.append(" and u.mobile='").append(mobile).append("'");
		}
		if (!StringUtils.isEmpty(beginDate)) {
			builder.append("and to_char(t.created_date,'yyyy-MM-dd') >= '").append(Calendars.format(new Date(Long.valueOf(beginDate)), "yyyy-MM-dd")).append("' ");
		}
		if (!StringUtils.isEmpty(endDate)) {
			builder.append("and to_char(t.created_date,'yyyy-MM-dd') <='").append(Calendars.format(new Date(Long.valueOf(endDate)), "yyyy-MM-dd")).append("'");
		}
		builder.append("order by t.id desc");

		LOGGER.info(builder.toString());

		query = entityManager.createNativeQuery(builder.toString(), OrderResultVo.class);
		query.setFirstResult((pageNo - 1) * pageSize);
		query.setMaxResults(pageNo * pageSize);
		page.setData(query.getResultList());
		page.setDraw(pageNo);
		page.setLength(page.getData().size());
		return page;
	}

	/**
	 * 生成裸票抽奖金额
	 * 
	 * @param ruleId
	 * @return
	 */
	public BigDecimal generateRuleAmount(Long ruleId) {
		GroupRule rule = groupRuleDao.findOne(ruleId);
		Random random = new Random();
		Integer randomAmount = random.nextInt(rule.getMaxMoney().subtract(rule.getMinMoney()).intValue());
		return rule.getMinMoney().add(BigDecimal.valueOf(randomAmount));
	}

	/**
	 * 获取等待推送的订单集合(最多10条)（改为最多100条--by XieZG）
	 * 
	 * @author wulinjie
	 * @return 待推送的订单
	 */
	public List<Order> findTop100ByNoxNotify(NoxNotify noxNotify, Order.Status status, Type type) {
		return orderDao.findTop100ByNoxNotifyAndStatusAndTypeNot(noxNotify.name(), status.name(), type.name(), new Sort("createdDate"));
	}

	/**
	 * 推送订单到分销系统
	 * 
	 * @author wulinjie
	 * @param orders
	 *            待推送的订单
	 */
	public void pushOrdersToNox(List<Order> orders) {
		LOGGER.info("待推送订单数量：{}", orders != null ? orders.size() : 0);
		int success = 0;
		if (!orders.isEmpty()) {
			D99000001 orderMsg = new D99000001();
			orderMsg.setPlatform("yipiao");
			List<com.snowstore.hera.connector.vo.nox.vo.Order> orderList = new ArrayList<com.snowstore.hera.connector.vo.nox.vo.Order>();
			for (Order order : orders) {
				User user = order.getUser();
				// 订单详情
				com.snowstore.hera.connector.vo.nox.vo.Order noxOrder = new com.snowstore.hera.connector.vo.nox.vo.Order();
				noxOrder.setChannelCode(user.getPlatform());
				noxOrder.setOrderNo(order.getId().toString());
				noxOrder.setMobile(user.getMobile());
				noxOrder.setAmount(order.getAmount());
				noxOrder.setProductType(order.getType());
				noxOrder.setCreatedDate(order.getCreatedDate());
				noxOrder.setTicketNum(order.getTicketNum().longValue());
				orderList.add(noxOrder);
				// 更新推送状态
				order.setNoxNotify(NoxNotify.SUCCESS.name());
				add(order);
				success++;
			}
			orderMsg.setOrders(orderList);
			LOGGER.info("发送报文：{}", JSON.toJSONString(orderMsg));
			Datagram res = noxSender.sendOrders(orderMsg);
			LOGGER.info("接收报文：{}", JSON.toJSONString(res));
			if (!"000000".equals(res.getDatagramBody().getOperateCode())) {
				LOGGER.error("订单推送失败，{}", res.getDatagramBody().getMemo());
				throw new RuntimeException("订单推送失败，" + res.getDatagramBody().getMemo());
			}
			LOGGER.info("订单推送成功，成功推送订单数量：{}", success);
		}
	}

	/**
	 * 裸票金额抵扣
	 * 
	 * @param payAmount
	 * @param order
	 * @param req
	 * @return
	 */
	public BigDecimal ruleAmountPay(BigDecimal payAmount, Order order, HttpServletRequest req) {
		BigDecimal ruleAmount = null;
		UserDetailsImpl userDetails = customerService.getUserDetails();
		if (Product.Type.多张组合.name().equals(order.getType())) {
			ruleAmount = (BigDecimal) req.getSession().getAttribute("ruleAmount");
		}
		// 裸票生成抵扣卡券
		if (null != ruleAmount && order.getUser().getId().equals(userDetails.getId())) {
			CardStock cardStock = cardStockService.createCardStock(ruleAmount, order.getType(), 0);
			cardStock.setUserId(userDetails.getId());
			cardStock.setUnionId(userDetails.getUnionUserId());
			cardStock.setOrderId(order.getId());
			cardStock.setExchangeCode(null);
			cardStock.setMemo("裸票抽奖抵扣");
			cardStock.setStatus(CardStock.Status.已使用.name());
			cardStock.setType(CardStock.Type.裸票抽奖券.name());
			cardStockService.saveOrUpdate(cardStock);
			payAmount = payAmount.subtract(ruleAmount);
		}
		return payAmount;
	}

	/**
	 * 乐天玛特渠道统一5块钱优惠
	 * 
	 * @param payAmount
	 * @param order
	 * @param req
	 * @return
	 */
	public BigDecimal channelPaySpec(BigDecimal payAmount, Order order, HttpServletRequest req) {
		UserDetailsImpl userDetails = customerService.getUserDetails();
		// 乐天玛特
		if (userDetails.getPlatform().equals(WordConfig.LTMT) && order.getUser().getId().equals(userDetails.getId())) {
			CardStock cardStock = cardStockService.createCardStock(WordConfig.LTMT_M, order.getType(), 0);
			cardStock.setUserId(userDetails.getId());
			cardStock.setUnionId(userDetails.getUnionUserId());
			cardStock.setOrderId(order.getId());
			cardStock.setExchangeCode(null);
			cardStock.setMemo("乐天玛特优惠抵扣");
			cardStock.setStatus(CardStock.Status.已使用.name());
			cardStock.setType(CardStock.Type.乐天玛特抵扣券.name());
			cardStockService.saveOrUpdate(cardStock);
			payAmount = payAmount.subtract(WordConfig.LTMT_M);
		}
		return payAmount;
	}

	public List<String> getChannelList(Long channelId) {
		List<Channel> channelList = new ArrayList<Channel>();
		List<String> channelCodeList = new ArrayList<String>();
		if (channelId != null) {
			channelList = channelService.getSubChannelByParentIdRecursively(channelId);
		} else {
			User user = customerService.getCurrentUser();
			if (Role.CHANNEL == Role.valueOf(user.getRole())) {
				Channel channel = channelService.getChannelByCode(user.getPlatform());
				channelList = channelService.getSubChannelByParentIdRecursively(channel.getId());
			} else {
				channelList = channelService.getAllChannel();
			}
		}
		if (!channelList.isEmpty()) {
			for (Channel channel : channelList) {
				channelCodeList.add(channel.getCode());
			}
		}
		return channelCodeList;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<OrderExportVo> queryOrder(String mobile, Date beginDate, Date endDate, String productName, String status, Long channelId) {
		List<OrderExportVo> list = new ArrayList<OrderExportVo>();
		Query query;
		StringBuilder builder = new StringBuilder();
		builder.append("select ");
		builder.append("o.id,");
		builder.append("p.trade_no,");
		builder.append("u.mobile,");
		builder.append("ch.name as platform,");
		builder.append("o.product_name,");
		builder.append("o.type, ");
		builder.append("o.amount,");
		builder.append("p.pay_amount,");
		builder.append("c.amount as card_stone_amount,");
		builder.append("decode(p.pay_platform, null, '卡券全额抵扣', 'QUICK_PAY', '快捷支付', 'WEIXIN_PAY_APP', '微信app支付', 'WEIXIN_PAY_JS_API', '微信公众号支付') as pay_platform, ");
		builder.append("c.memo,");
		builder.append("o.status,");
		builder.append("o.created_date,");
		builder.append("o.ticket_num, ");
		builder.append("decode(ug.id, null, 0, 1) gift_num ");
		builder.append("from diana_order o ");
		builder.append("left join diana_pay_info p on o.id = p.order_id  and p.status='支付成功' ");
		builder.append("left join diana_card_stock c on o.id=c.order_id ");
		builder.append("left join diana_user u on u.id=o.user_id ");
		builder.append("left join diana_user_gift ug on ug.order_id=o.id ");
		builder.append(",diana_channel ch ");
		builder.append("where u.platform = ch.code ");
		builder.append("and o.type!='电影转礼品'");

		if (!StringUtils.isEmpty(status) && !"全部".equals(status)) {
			builder.append("and o.status='").append(status).append("'");
		}
		if (!StringUtils.isEmpty(productName)) {
			builder.append("and t.product_name='").append(productName).append("'");
		}
		if (null != channelId) {
			List<String> channels = getChannelList(channelId);
			builder.append("and u.platform in (");
			for (String channel : channels) {
				builder.append("'" + channel + "',");
			}
			builder.append("'");
			builder.append(channelService.get(channelId).getCode()).append("') ");
		}

		if (!StringUtils.isEmpty(mobile)) {
			builder.append(" and u.mobile='").append(mobile).append("'");
		}
		if (null != beginDate) {
			builder.append(" and to_char(o.created_date,'yyyy-MM-dd') >= '").append(Calendars.format(beginDate, "yyyy-MM-dd")).append("' ");
		}
		if (null != endDate) {
			builder.append(" and to_char(o.created_date,'yyyy-MM-dd') <='").append(Calendars.format(endDate, "yyyy-MM-dd")).append("'");
		}
		builder.append(" order by o.id desc");
		LOGGER.info(builder.toString());
		query = entityManager.createNativeQuery(builder.toString(), OrderExportVo.class);
		query.setFirstResult(0);
		query.setMaxResults(Integer.MAX_VALUE);
		list = query.getResultList();
		return list;
	}

	/**
	 * 查询出子订单(电影票兑换礼品)
	 * 
	 * @author mingzhi.dong
	 * @date 2016年3月31日
	 * @param orderId
	 * @return
	 */
	public List<Order> findBySubOrderService(Long orderId) {
		return this.orderDao.findBySubOrder(orderId);
	}
}
