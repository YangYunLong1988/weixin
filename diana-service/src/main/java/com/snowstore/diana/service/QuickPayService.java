package com.snowstore.diana.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.foxinmy.weixin4j.exception.PayException;
import com.snowstore.diana.common.Constants;
import com.snowstore.diana.common.Constants.QuickpayRespcode;
import com.snowstore.diana.common.Constants.QuickpayStatus;
import com.snowstore.diana.config.JopClientProxy;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.PayInfo;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.domain.UserBankInfo;
import com.snowstore.diana.domain.UserBankInfo.Status;
import com.snowstore.diana.repository.PayInfoDao;
import com.snowstore.diana.repository.UserBankInfoDao;
import com.snowstore.diana.service.userDetails.UserDetailsImpl;
import com.snowstore.diana.utils.CommUtil;
import com.snowstore.diana.vo.Result;
import com.snowstore.poseidon.client.exception.BusinessException;
import com.snowstore.poseidon.client.vo.request.CustomerOrderReq;
import com.snowstore.poseidon.client.vo.request.OrderQuickPayReq;
import com.snowstore.poseidon.client.vo.request.OrderQuickPayVerifyReq;
import com.snowstore.poseidon.client.vo.request.QuickAuthenticationReq;
import com.snowstore.poseidon.client.vo.request.QuickVerificationReq;
import com.snowstore.poseidon.client.vo.request.SubbranchReq;
import com.snowstore.poseidon.client.vo.request.SupportBankReq;
import com.snowstore.poseidon.client.vo.response.BankInfo;
import com.snowstore.poseidon.client.vo.response.CustomerOrder;
import com.snowstore.poseidon.client.vo.response.CustomerOrderResp;
import com.snowstore.poseidon.client.vo.response.OrderQuickPayResp;
import com.snowstore.poseidon.client.vo.response.OrderQuickPayVerifyResp;
import com.snowstore.poseidon.client.vo.response.QuickAuthenticationResp;
import com.snowstore.poseidon.client.vo.response.QuickVerificationResp;
import com.snowstore.poseidon.client.vo.response.SubbranchResp;
import com.snowstore.poseidon.client.vo.response.SupportBankResp;

/**
 * 快捷支付服务类
 * 
 * @author XieZG
 * @Date:2016年2月16日下午4:27:52
 */
@Service
@Transactional
public class QuickPayService {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuickPayService.class);
	@Autowired
	private JopClientProxy jopClient;
	@Autowired
	private SequenceService sequenceService;
	@Autowired
	private UserBankInfoDao quickAuthenticatedBankInfoDao;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserBankInfoDao bankInfoDao;
	@Autowired
	private PayInfoDao payInfoDao;
	@Autowired
	private UnionUserService unionUserService;

	public UserBankInfo findBingBankByAuthNum(Long authSerialNumber) {
		return bankInfoDao.findByAuthSerialNumber(authSerialNumber);
	}

	/**
	 * 查询当前用户已绑定银行卡
	 * 
	 * @author XieZG
	 * @Date:2016年2月19日下午4:00:26
	 * @return
	 */
	public List<UserBankInfo> findBindBank() {
		List<UserBankInfo> bankInfos = null;
		User user = customerService.getCurrentUser();
		bankInfos = quickAuthenticatedBankInfoDao.findByUnionUser(unionUserService.findOrAdd(user.getMobile()), Status.快捷认证成功.name());
		return bankInfos;
	}

	/**
	 * 获取支持快捷支付银行集合
	 * 
	 * @author XieZG
	 * @Date:2016年2月16日下午4:31:49
	 * @return
	 */
	public List<BankInfo> findQuickPayBank() {
		Collection<BankInfo> values= Constants.BANK_LIST.values();
		return new ArrayList<BankInfo>(values);
	}
	/**
	 * 项目启动初始化一次
	 * 然后每天凌晨更新一次
	 * @author XieZG
	 * @Date:2016年2月26日下午5:39:57
	 */
	@PostConstruct
	public void refreshQuickPayBank() {
		for (String code : Constants.BANK_CODE_LIST) {
			try {
				SupportBankReq bankReq = new SupportBankReq();
			    bankReq.setBusiType(Constants.QUICKPAY_BUSITYPE_QUICKPAY);//
				// 快捷支付类型
				bankReq.setBankCode(code);
				SupportBankResp bankResp = (SupportBankResp) jopClient.req(bankReq);
				if (bankResp != null && bankResp.getBankList() != null && bankResp.getBankList().size() > 0) {
					LOGGER.info("接收银行信息：" + ToStringBuilder.reflectionToString(bankResp.getBankList().get(0)));
					Constants.BANK_LIST.put(code, bankResp.getBankList().get(0));
				} else {
					Constants.BANK_LIST.remove(code);
					LOGGER.error("通过易联开放平台查不出银行信息！！！！！");
				}
			} catch (BusinessException e) {
				LOGGER.error("调用开放平台查询银行卡列表异常!");
			}
		}
	}

	/**
	 * 绑定银行卡
	 * 
	 * @author XieZG
	 * @Date:2016年2月17日下午3:55:51
	 */
	public Result<JSONObject> bindBank(HttpServletRequest req) {
		Result<JSONObject> result = new Result<JSONObject>(Result.Type.FAILURE);
		try {
			// 校验数据
			String accountNumber = req.getParameter("accountNumber");
			if (!CommUtil.isNum(accountNumber) || accountNumber.length() > 30) {
				result.addMessage("银行卡号不正确！");
				return result;
			}
			String phoneNumber = req.getParameter("phoneNumber");
			if (!CommUtil.isPhone(phoneNumber)) {
				result.addMessage("手机号不正确！");
				return result;
			}
			String identificationNumber = req.getParameter("IdentificationNumber");
			if (!CommUtil.IDCardValidate(identificationNumber)) {
				result.addMessage("身份证号不正确！");
				return result;
			}
			// 封装数据
			QuickAuthenticationReq authenticationReq = new QuickAuthenticationReq();
			authenticationReq.setPayChannel(Constants.QUICKPAY_PAYCHANNEL_ZHONGJIN);
			authenticationReq.setAuthSerialNumber(Long.valueOf(sequenceService.nextSequenceValue(Constants.SEQUENCE_AUTH_SERIAL_NUMBER)));
			authenticationReq.setAccountType(Constants.QUICKPAY_ACCOUNTTYPE_PERSONAL);
			authenticationReq.setBankCode(req.getParameter("bankCode"));
			authenticationReq.setAccountName(req.getParameter("accountName"));
			authenticationReq.setAccountNumber(accountNumber);
			authenticationReq.setProvince(req.getParameter("province"));
			authenticationReq.setCity(req.getParameter("city"));
			authenticationReq.setChName(req.getParameter("chName"));
			authenticationReq.setIdentificationType(Constants.QUICKPAY_IDTYPE_IDCARD);
			authenticationReq.setIdentificationNumber(identificationNumber);
			authenticationReq.setPhoneNumber(phoneNumber);
			authenticationReq.setCardType(Constants.QUICKPAY_CARDTYPE_DEBITCARD);
			QuickAuthenticationResp authenticationResp = (QuickAuthenticationResp) jopClient.req(authenticationReq);
			if (QuickpayRespcode.AUTHSMSSEND.equals(authenticationResp.getCode())) {
				LOGGER.info("认证成功，流水号" + authenticationReq.getAuthSerialNumber());
				result.setType(Result.Type.SUCCESS);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("authSerialNumber", authenticationReq.getAuthSerialNumber());
				result.setData(jsonObject);
				// 保存绑定银行卡信息到数据库（未绑定）
				UserBankInfo quickAuthenticatedBankInfo = new UserBankInfo();
				quickAuthenticatedBankInfo.setPayChannel(Constants.QUICKPAY_PAYCHANNEL_ZHONGJIN);
				quickAuthenticatedBankInfo.setAuthSerialNumber(authenticationReq.getAuthSerialNumber());
				quickAuthenticatedBankInfo.setAccountType(Constants.QUICKPAY_ACCOUNTTYPE_PERSONAL);
				quickAuthenticatedBankInfo.setBankCode(req.getParameter("bankCode"));
				quickAuthenticatedBankInfo.setAccountName(req.getParameter("accountName"));
				quickAuthenticatedBankInfo.setAccountNumber(req.getParameter("accountNumber"));
				quickAuthenticatedBankInfo.setProvince(req.getParameter("province"));
				quickAuthenticatedBankInfo.setCity(req.getParameter("city"));
				quickAuthenticatedBankInfo.setChName(req.getParameter("chName"));
				quickAuthenticatedBankInfo.setIdentificationType(Constants.QUICKPAY_IDTYPE_IDCARD);
				quickAuthenticatedBankInfo.setIdentificationNumber(req.getParameter("IdentificationNumber"));
				quickAuthenticatedBankInfo.setPhoneNumber(req.getParameter("phoneNumber"));
				quickAuthenticatedBankInfo.setCardType(Constants.QUICKPAY_CARDTYPE_DEBITCARD);
				quickAuthenticatedBankInfo.setStatus(UserBankInfo.Status.快捷认证中.name());
				quickAuthenticatedBankInfo.setSingleLimit(new BigDecimal(req.getParameter("singleLimit")));
				quickAuthenticatedBankInfo.setDayLimit(new BigDecimal(req.getParameter("dayLimit")));
				quickAuthenticatedBankInfo.setBankName(req.getParameter("bankName"));
				User user = customerService.getCurrentUser();
				quickAuthenticatedBankInfo.setUser(user);
				quickAuthenticatedBankInfo.setUnionId(unionUserService.findOrAdd(user.getMobile()));
				quickAuthenticatedBankInfoDao.save(quickAuthenticatedBankInfo);
			} else {
				LOGGER.info("绑定银行卡失败");
				result.setType(Result.Type.FAILURE);
				//999999特殊处理
				if ("999999".equals(authenticationResp.getCode())) {
					result.addMessage(authenticationResp.getMemo());
				}else{
					result.addMessage(Constants.JLFEX_ERROR.get(authenticationResp.getCode()));
				}
				
			}
		} catch (BusinessException e) {
			LOGGER.info("绑定银行卡失败");
			//999999特殊处理
			if ("999999".equals(e.getStatus())) {
				result.addMessage(e.getMemo());
			}else{
				result.addMessage(Constants.JLFEX_ERROR.get(e.getStatus()));
			}
			result.setType(Result.Type.FAILURE);
		}
		return result;
	}

	/**
	 * 绑定银行卡短信校验
	 * 
	 * @author XieZG
	 * @Date:2016年2月17日下午3:55:51
	 */
	public String bindBankVerify(long authSerialNumber, String validationCode) {
		String msg = "绑卡失败，请重新绑定";
		try {
			QuickVerificationReq quickVerificationReq = new QuickVerificationReq();
			quickVerificationReq.setPayChannel(Constants.QUICKPAY_PAYCHANNEL_ZHONGJIN);
			quickVerificationReq.setAuthSerialNumber(authSerialNumber);
			quickVerificationReq.setValidationCode(validationCode);
			QuickVerificationResp quickVerificationResp = (QuickVerificationResp) jopClient.req(quickVerificationReq);
			if (QuickpayRespcode.SUCCESS.equals(quickVerificationResp.getCode())) {
				LOGGER.info("认证成功，流水号" + quickVerificationResp.getAuthSerialNumber());
				// 修改数据库银行卡信息为绑定
				UserBankInfo quickAuthenticatedBankInfo = quickAuthenticatedBankInfoDao.findByAuthSerialNumber(authSerialNumber);
				quickAuthenticatedBankInfo.setStatus(UserBankInfo.Status.快捷认证成功.name());
				msg = null;
			} else {
				if ("100043".equals(quickVerificationResp.getCode())) {
					msg = quickVerificationResp.getMemo();
					if (msg.indexOf("短信") > -1) {
						msg = "短信验证码校验不正确，请重发验证码验证！";
					} else if ("OK.".equals(msg) || "返回失败".equals(msg)) {
						// 易连失败了memo里还写OK!!!
						msg = "绑卡失败，请重新绑定";
					}
				}else if ("999999".equals(quickVerificationResp.getCode())) {
					//999999特殊处理
					msg=quickVerificationResp.getMemo();
				} else {
					msg = Constants.JLFEX_ERROR.get(quickVerificationResp.getCode());
				}
			}
		} catch (BusinessException e) {
			LOGGER.info("绑定银行卡短信校验失败");
			// 快捷支付验证码校验错误后就得重新认证一次
			msg = Constants.JLFEX_ERROR.get(e.getStatus());
		}

		return msg;
	}

	/**
	 * 查询支行
	 * 
	 * @author XieZG
	 * @Date:2016年2月17日下午3:55:51
	 */
	public List<String> findSubBranch(String cityName, String bankCode) {
		List<String> subBranchs = null;
		try {
			SubbranchReq subbranchReq = new SubbranchReq();
			subbranchReq.setCityName(cityName);
			subbranchReq.setBankCode(bankCode);
			SubbranchResp subbranchResp = (SubbranchResp) jopClient.req(subbranchReq);
			if (subbranchResp != null) {
				subBranchs = subbranchResp.getSubbranchList();
			}
		} catch (BusinessException e) {
			LOGGER.error("调用开放平台绑定银行卡异常!");
		}
		return subBranchs;
	}

	/**
	 * 调用开放平台快捷支付接口
	 * 
	 * @author YangYL
	 */
	public boolean sendQuickPaySMS(String orderCode, Long authSerialNumber, Result<JSONObject> result) {
		boolean flag = false;
		try {
			OrderQuickPayReq quickPayReq = new OrderQuickPayReq();
			quickPayReq.setPayChannel(Constants.QUICKPAY_PAYCHANNEL_ZHONGJIN);
			quickPayReq.setOrderCode(orderCode);// 订单编号
			quickPayReq.setAuthSerialNumber(authSerialNumber);// 快捷认证业务流水号
			OrderQuickPayResp quickPayResp = (OrderQuickPayResp) jopClient.req(quickPayReq);
			if (QuickpayRespcode.PAYSMSSEND.equals(quickPayResp.getOperateCode())) {
				flag = true;
				LOGGER.info("发送支付验证码成功！订单状态：" + quickPayResp.getStatus() + "，支付状态：" + quickPayResp.getPayStatus());
			} else {
				if("100034".equals(quickPayResp.getOperateCode())){
					//特殊处理的响应码，有多种结果
					result.addMessage(quickPayResp.getMemo());
				}else{
					result.addMessage(Constants.JLFEX_ERROR.get(quickPayResp.getOperateCode()));
				}
			}
		} catch (BusinessException e) {
			LOGGER.info("发送支付验证码失败");
			if("100034".equals(e.getStatus())){
				//特殊处理的响应码，有多种结果
				result.addMessage(e.getMemo());
			}else{
				result.addMessage(Constants.JLFEX_ERROR.get(e.getStatus()));
			}
		}

		return flag;
	}

	/**
	 * 调用开放平台快捷支付手机验证接口
	 * 
	 * @author YangYL
	 */
	public String quickPayVerify(String orderCode, String validationCode, JSONObject prePay, PayInfo payInfo) {
		String payStatus = QuickpayStatus.PAYFAILURE;
		OrderQuickPayVerifyResp quickPayRespVerify = null;
		try {
			OrderQuickPayVerifyReq quickPayReqVerify = new OrderQuickPayVerifyReq();
			quickPayReqVerify.setPayChannel(Constants.QUICKPAY_PAYCHANNEL_ZHONGJIN);
			quickPayReqVerify.setOrderCode(orderCode);// 订单编号
			quickPayReqVerify.setValidationCode(validationCode);// 手机验证码
			payInfo.setReq(ToStringBuilder.reflectionToString(quickPayReqVerify));
			quickPayRespVerify = (OrderQuickPayVerifyResp) jopClient.req(quickPayReqVerify);
			if (quickPayRespVerify != null) {
				payInfo.setRes(ToStringBuilder.reflectionToString(quickPayRespVerify));
				payStatus = quickPayRespVerify.getPayStatus();
				prePay.put("msg", Constants.JLFEX_ERROR.get(quickPayRespVerify.getOperateCode()));
			}
		} catch (BusinessException e) {
			LOGGER.info("快捷支付验证失败");
			String msg = Constants.JLFEX_ERROR.get(e.getStatus());
			if ("100108".equals(e.getStatus())) {
				msg = "短信已失效，请重发验证码！";
			}
			prePay.put("msg", msg);
		}
		return payStatus;
	}

	/**
	 * 快捷支付
	 * 
	 * @author XieZG
	 * @Date:2016年2月19日下午3:04:29
	 * @param order
	 * @param req
	 * @param result
	 * @return
	 * @throws PayException
	 */
	@Transactional(readOnly = false)
	public Result<JSONObject> quickPay(Order order, HttpServletRequest req, UserDetailsImpl user, BigDecimal payAmount) throws PayException {
		Result<JSONObject> result = new Result<JSONObject>(Result.Type.FAILURE);
		JSONObject prePay = new JSONObject();
		result.setData(prePay);
		String validationCode = req.getParameter("validationCode");
		if (!CommUtil.isNum(validationCode)) {
			result.addMessage("验证码只能是6位数字！");
			return result;
		}

		PayInfo payInfo = payInfoDao.findByOrderId(order.getId());
		if (null == payInfo) {
			payInfo = new PayInfo();
			payInfo.setOrderId(order.getId());
			payInfo.setStatus(PayInfo.Status.未支付.name());
		}
		payInfo.setTradeNo(orderService.generatePayCode());
		payInfo.setPayPlatform(PayInfo.Platform.QUICK_PAY.name());
		String payStatus = quickPayVerify(order.getReferenceOrder(), validationCode, prePay, payInfo);
		if (QuickpayStatus.PAYSUCCESS.equals(payStatus)) {
			// 支付成功
			orderService.orderPaySuccess(order.getId());
			prePay.put("orderId", order.getId());
			prePay.put("giftNum", order.getGitfNum());
			prePay.put("payType", Constants.QUICK_PAY_SUCCESS);
			result.setType(Result.Type.SUCCESS);
			// 保存payInfo对象，否则后台支付金额会为0
			payInfo.setStatus(PayInfo.Status.支付成功.name());
			payInfo.setPayAmount(payAmount);
			payInfoDao.save(payInfo);

			LOGGER.info("订单：" + order.getId() + "快捷支付成功");
		} else if (QuickpayStatus.PAYIN.equals(payStatus)) {
			// 支付中
			order.setStatus(com.snowstore.diana.domain.Order.Status.付款中.name());
			orderService.saveOrUpdate(order);
			prePay.put("orderId", order.getId());
			prePay.put("giftNum", order.getGitfNum());
			prePay.put("payType", Constants.QUICK_PAY_IN);
			result.setType(Result.Type.SUCCESS);
			LOGGER.info("订单：" + order.getId() + "快捷支付中......");
		} else {
			prePay.put("orderId", order.getId());
			// 支付失败
			if ("OK.".equals(prePay.getString("msg")) || "返回失败".equals(prePay.getString("msg"))) {
				result.addMessage("支付失败");
			} else {
				result.addMessage("支付失败:" + prePay.getString("msg"));
			}

		}
		return result;
	}

	/***
	 * 查询订单状态
	 * 
	 * @author XieZG
	 * @Date:2016年2月22日下午1:32:25
	 * @param referenceOrders
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<CustomerOrder> queryOrder(String referenceOrders) {
		List<CustomerOrder> customerOrders = null;
		try {
			CustomerOrderReq customerOrderReq = new CustomerOrderReq();
			customerOrderReq.setOrderCodes(referenceOrders);

			CustomerOrderResp customerOrderResp = (CustomerOrderResp) jopClient.req(customerOrderReq);
			customerOrders = customerOrderResp.getContent();
		} catch (BusinessException e) {
			LOGGER.error("查询订单失败");
		}
		return customerOrders;
	}

}
