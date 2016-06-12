package com.snowstore.diana.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.foxinmy.weixin4j.exception.PayException;
import com.snowstore.diana.common.Constants;
import com.snowstore.diana.domain.C009Card;
import com.snowstore.diana.domain.CardStock;
import com.snowstore.diana.domain.CardStock.Status;
import com.snowstore.diana.domain.CardStock.Type;
import com.snowstore.diana.domain.Channel;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Product;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.domain.User.Role;
import com.snowstore.diana.domain.UserGift;
import com.snowstore.diana.repository.C009CardDao;
import com.snowstore.diana.repository.CardStockDao;
import com.snowstore.diana.repository.OrderDao;
import com.snowstore.diana.repository.UserDao;
import com.snowstore.diana.repository.UserGiftDao;
import com.snowstore.diana.utils.CommUtil;
import com.snowstore.diana.vo.Result;

/**
 * 卡劵 service
 * 
 * @author XieZG
 * @Date:2015年11月13日下午1:10:34
 */
@Service
@Transactional
public class CardStockService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CardStockService.class);
	@Autowired
	private CardStockDao cardStockDao;
	@Autowired
	private C009CardDao c009CardDao;
	@Autowired
	private OrderService orderService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserGiftDao userGiftDao;
	@Autowired
	private UnionUserService unionUserService;

	private final String A_CODE = "39";
	private final String B_CODE = "77";

	/**
	 * 生成卡劵
	 * 
	 * @author XieZG
	 * @Date:2015年11月13日下午1:53:18
	 * @param size
	 * @param amount
	 */
	@Transactional(readOnly = false)
	public CardStock createCardStock(BigDecimal amount, String type, Integer gift) {
		CardStock cardStock = new CardStock();
		cardStock.setType(type);
		cardStock.setAmount(amount);
		cardStock.setGift(gift);
		cardStock.setStatus(Status.未使用.name());
		cardStock.setExchangeCode(createExchangeCode(gift));
		cardStockDao.save(cardStock);
		return cardStock;
	}

	/**
	 * 生成兑换码
	 * 
	 * @author XieZG
	 * @Date:2015年11月17日下午2:53:55
	 * @return
	 */
	private String createExchangeCode(Integer gift) {
		StringBuffer code = new StringBuffer("D009");
		code.append(CommUtil.getRandom(4));
		// 有礼物是A类，无礼物是B类
		if (gift > 0) {
			code.append(A_CODE);
		} else {
			code.append(B_CODE);
		}
		code.append(CommUtil.getRandom(6));
		return code.toString();
	}

	/**
	 * 查询卡劵状态
	 * 
	 * @author XieZG
	 * @Date:2015年11月13日下午4:26:14
	 * @param exchangeCode
	 * @return CardStock.Status
	 */
	@Transactional(readOnly = false)
	public Status findStatusByCode(String exchangeCode) {
		Status status = null;
		CardStock cardStock = cardStockDao.findByExchangeCode(exchangeCode);
		if (cardStock != null) {
			status = Status.valueOf(cardStock.getStatus());
			LOGGER.info("兑换码" + exchangeCode + " 状态为：" + status.name());
		} else {
			LOGGER.info("兑换码" + exchangeCode + "不存在");
		}

		return status;
	}

	/**
	 * 校验卡券
	 * 
	 * @author XieZG
	 * @Date:2015年12月15日下午3:11:28
	 * @param exchangeCode
	 * @param userId
	 * @return flag
	 */
	public boolean verifyCardStock(HttpServletRequest req) {
		boolean flag = true;
		long userId = customerService.getCurrentUser().getId();
		String[] codes = req.getParameter("codes").split(Constants.Comma);
		for (String code : codes) {
			CardStock cardStock = findByCode(code);
			if (cardStock.getUserId().longValue() != userId) {
				flag = false;
				break;
			} else if (!cardStock.getStatus().equals(Status.未使用.name())) {
				flag = false;
				break;
			} else if (cardStock.getOrderId() != null) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	/**
	 * 根据兑换码查询卡券
	 * 
	 * @author XieZG
	 * @Date:2015年11月13日下午4:26:14
	 * @param exchangeCode
	 * @return CardStock.Status
	 */
	public CardStock findByCode(String exchangeCode) {
		CardStock cardStock = cardStockDao.findByExchangeCode(exchangeCode);
		return cardStock;
	}

	/**
	 * 根据用户ID查询卡券
	 * 
	 * @author XieZG
	 * @Date:2015年11月13日下午4:26:14
	 * @param exchangeCode
	 * @return CardStock.Status
	 */
	public List<CardStock> findByUserId(Long userId) {
		List<CardStock> cardStock = cardStockDao.findByUserIdAndStatusAndOrderIdIsNull(userId, Status.未使用.name());
		return cardStock;
	}

	/**
	 * 卡券支付
	 * 
	 * @author XieZG
	 * @Date:2015年12月16日上午10:48:11
	 * @param order
	 * @param req
	 * @param result
	 * @return 实付金额
	 * @throws PayException
	 */
	@Transactional(readOnly = false)
	public BigDecimal cardStockPay(Order order, HttpServletRequest req, Result<JSONObject> result) throws PayException {
		BigDecimal payAmount = order.getAmount();
		if (StringUtils.isNotBlank(req.getParameter("codes"))) {
			// 校验卡券
			if (verifyCardStock(req)) {
				String[] codes = req.getParameter("codes").split(Constants.Comma);
				List<CardStock> cardStocks = new ArrayList<CardStock>();
				Integer giftNum = 0;
				// 实付全额付款
				boolean isFull = false;
				// 计算支付金额
				for (String code : codes) {
					CardStock cardStock = findByCode(code);
					if (cardStock.getGift() == 1 && order.getGitfNum() == 0) {
						result.setType(Result.Type.FAILURE);
						result.addMessage("您选择的产品无礼品，请确认。");
						return null;
					} else if (cardStock.getGift() == 0 && order.getGitfNum() == 1) {
						result.setType(Result.Type.FAILURE);
						result.addMessage("该代金券无礼品，不可使用，请确认。");
						return null;
					}
					
					if (Product.Type.大轰炸.name().equals(order.getType())
							&& !Product.Type.大轰炸.name().equals(cardStock.getType())) {
						result.setType(Result.Type.FAILURE);
						result.addMessage("该代金券，不能支付大轰炸产品。");
						return null;
					}
					payAmount = payAmount.subtract(cardStock.getAmount());
					cardStocks.add(cardStock);
				}
				if (payAmount.compareTo(BigDecimal.ZERO) <= 0) {
					// 防止出现负数
					payAmount = new BigDecimal("0");
					// 全额支付
					isFull = true;
				}
				giftNum = useCardStock(cardStocks, order.getId(), isFull);
				order.setGitfNum(0);
				List<UserGift> gifts = userGiftDao.findByOrderId(order.getId());
				if (giftNum > 0) {
					// 礼品数量限制固定为1
					order.setGitfNum(1);
				} else {
					// 无礼品卡券撤销已选择的礼品
					for (UserGift userGift : gifts) {
						userGift.setOrder(null);
						userGiftDao.save(userGift);
					}
				}
				orderDao.save(order);

				// 全额付款时直接支付成功
				if (isFull) {
					orderService.orderPaySuccess(order.getId());
					JSONObject prePay = new JSONObject();
					prePay.put("orderId", order.getId());
					prePay.put("giftNum", order.getGitfNum());
					prePay.put("payType", Constants.CARDSTOCK_PAY_SUCCESS);
					result.setData(prePay);
					result.setType(Result.Type.SUCCESS);
					LOGGER.info("卡券支付成功");
				}
			} else {
				result.addMessage("卡券无效，下单失败！");
			}
		}
		return payAmount;
	}

	/**
	 * 使用卡劵
	 * 
	 * @author XieZG
	 * @Date:2015年11月16日上午9:53:59
	 * @param exchangeCode
	 * @return 返回礼品数量
	 */
	@Transactional(readOnly = false)
	public Integer useCardStock(List<CardStock> cardStocks, Long orderId, boolean isFull) {
		Integer giftNum = 0;
		for (CardStock cardStock : cardStocks) {
			// 总卡卷金额=订单金额，直接修改卡卷状态。
			if (isFull) {
				cardStock.setStatus(Status.已使用.name());
			}

			// 如果该订单已经绑定了卡券，则替换到已绑定的卡券(一个订单始终只有一个卡券)
			List<CardStock> alreadyBind = findByOrderId(orderId);
			if (!alreadyBind.isEmpty()) {
				for (CardStock stock : alreadyBind) {
					if (CardStock.Type.裸票抽奖券.name().equals(stock.getType())) {
						continue;
					}
					stock.setOrderId(null);
					stock.setStatus(Status.未使用.name());
					cardStockDao.save(stock);
				}
			}

			cardStock.setOrderId(orderId);
			giftNum += cardStock.getGift();
			cardStockDao.save(cardStock);
		}
		return giftNum;
	}

	/**
	 * 支付成功更新卡券状态
	 * 
	 * @author XieZG
	 * @Date:2015年12月15日下午3:46:50
	 * @param orderId
	 */
	@Transactional(readOnly = false)
	public void paySuccess(Long orderId) {
		Order order = orderDao.findOne(orderId);
		// TODO （混合支付）如果该订单超过15分钟才支付的，实际该订单已经撤单了，无法根据订单ID找到对应的卡券，后期需要优化
		List<CardStock> cardStocks = cardStockDao.findByOrderIdAndStatus(order.getId(), Status.未使用.name());
		for (CardStock cardStock : cardStocks) {
			cardStock.setStatus(Status.已使用.name());
		}
		cardStockDao.save(cardStocks);
	}

	/**
	 * 绑定卡劵
	 * 
	 * @author XieZG
	 * @Date:2015年11月16日上午9:53:59
	 * @param exchangeCode
	 * @return 返回礼品数量
	 */
	@Transactional(readOnly = false)
	public void bindCardStock(String exchangeCode, Long userId, Long unionId) {
		CardStock cardStock = cardStockDao.findByExchangeCode(exchangeCode);
		cardStock.setUserId(userId);
		cardStock.setUnionId(unionId);
		cardStockDao.save(cardStock);
	}

	/**
	 * 验证c009批次代金券
	 * 
	 * @param codeDiana
	 * @param code3th
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean checkC009(String codeDiana, String code3th) {
		C009Card card = c009CardDao.findByCodeDiana(codeDiana);
		if (card == null) {
			return false;
		}
		if (!card.getCode3th().equalsIgnoreCase(code3th)) {
			return false;
		}
		return true;
	}

	/**
	 * 根据订单ID，获取卡券列表
	 * 
	 * @author wulinjie
	 * @return 卡券列表
	 */
	public List<CardStock> findByOrderId(Long orderId) {
		return cardStockDao.findByOrderId(orderId);
	}

	/**
	 * 保存卡券信息
	 * 
	 * @author wulinjie
	 * @param cardStock
	 *            卡券信息
	 * @return
	 */
	public CardStock saveOrUpdate(CardStock cardStock) {
		return cardStockDao.save(cardStock);
	}

	/**
	 * 导入苏宁用户卡券，采用统一事务导入全部用户
	 * 
	 * @throws IOException
	 */

	@Transactional
	public void importUsersWithCard(String mobile,Properties prop,String memo) throws IOException {
			Pattern p = Pattern.compile("^1\\d{10}$");
			if ("importDate".equals(mobile) || "memo".equals(mobile)) {
				return;
			}
			LOGGER.info("用户：" + mobile);
			Matcher m = p.matcher(mobile);
			if (!m.matches()) {
				throw new RuntimeException("手机格式错");
			}
			Integer cardNum = Integer.valueOf(prop.getProperty(mobile));
			Channel channel = channelService.getChannelByCode("SNYG"); // 查询易加渠道
			User user = userDao.findByMobileAndPlatform(mobile, channel.getCode());
			if (null == user) {
				LOGGER.info("新用户,创建.....");
				user = new User();
				user.setMobile(mobile); // 手机
				user.setRole(Role.CUSTOMER.name()); // 角色
				user.setPlatform(channel.getCode()); // 渠道
				user.setChannel(channel.getCode()); // 子渠道
				user.setPlatformNo(channel.getPlatformNo());// 渠道编码
				user = userDao.save(user);
			} else {
				LOGGER.info("老用户,准备生成卡券.....");
			}
			Calendar calendar = Calendar.getInstance();
			Date start = DateUtils.truncate(calendar.getTime(), Calendar.DATE);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date end = DateUtils.truncate(calendar.getTime(), Calendar.DATE);
			List<CardStock> cards = cardStockDao.findByUserIdAndMemoAndCreatedDateBetween(user.getId(), memo, start, end);
			if (cards != null && cards.size() > 0) {
				LOGGER.info("卡券已经生成过了");
				throw new RuntimeException("卡券已经生成过了，请不要重复执行");
			}
			LOGGER.info("卡券：" + cardNum + "张");
			for (int i = 0; i < cardNum; i++) {
				CardStock card = createCardStock(new BigDecimal(100), Type.大轰炸.name(), 1);
				card.setUserId(user.getId());
				card.setUnionId(unionUserService.findOrAdd(user.getMobile()));
				if (null != memo) {
					card.setMemo(memo);
				}
				card = cardStockDao.save(card);
			}
		}
	
	/**检查C009卡券绑定 超过30张给客户提示
	 * @return
	 */
	public Boolean checkCardStockAmount(Long unionId){
		int size = cardStockDao.findByUnionIdAndExchangeCodeLike(unionId,"C009%").size();
		if(size >= 30){
			return false;
		}else{
			return true;
		}
	}

}
