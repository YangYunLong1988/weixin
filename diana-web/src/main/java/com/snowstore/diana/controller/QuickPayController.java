package com.snowstore.diana.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.snowstore.diana.common.Constants;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Order.Status;
import com.snowstore.diana.domain.UserBankInfo;
import com.snowstore.diana.service.OrderService;
import com.snowstore.diana.service.QuickPayService;
import com.snowstore.diana.vo.Result;

@Controller
@RequestMapping("/quickpay")
public class QuickPayController {
	@Autowired
	private QuickPayService quickPayService;
	@Autowired
	private OrderService orderService;

	/**
	 * 查询省市
	 * 
	 * @author XieZG
	 * @Date:2016年2月18日下午3:06:50
	 * @param province
	 * @return
	 */
	@RequestMapping("areas")
	@ResponseBody
	public Result<JSONObject> areas(String province) {
		Result<JSONObject> result = new Result<JSONObject>(Result.Type.SUCCESS);
		JSONObject jsonObject = new JSONObject();
		result.setData(jsonObject);
		if (StringUtils.isNotBlank(province)) {
			jsonObject.put("citys", Constants.areas.get(province));
		} else {
			jsonObject.put("provinces", Constants.areas.keySet());
		}
		return result;
	}

	/**
	 * 查询银行
	 * 
	 * @author XieZG
	 * @Date:2016年2月18日下午3:06:50
	 * @param province
	 * @return
	 */
	@RequestMapping("findBank")
	@ResponseBody
	public Result<JSONObject> findBank() {
		Result<JSONObject> result = new Result<JSONObject>(Result.Type.SUCCESS);
		JSONObject jsonObject = new JSONObject();
		result.setData(jsonObject);
		jsonObject.put("banks", quickPayService.findQuickPayBank());
		return result;
	}

	/**
	 * 查询分行
	 * 
	 * @author XieZG
	 * @Date:2016年2月18日下午3:06:50
	 * @param province
	 * @return
	 */
	@RequestMapping("findSubBranch")
	@ResponseBody
	public Result<JSONObject> findSubBranch(String bankCode, String cityName) {
		Result<JSONObject> result = new Result<JSONObject>(Result.Type.SUCCESS);
		JSONObject jsonObject = new JSONObject();
		result.setData(jsonObject);
		jsonObject.put("subBranchs", quickPayService.findSubBranch(cityName, bankCode));
		return result;
	}

	/**
	 * 绑定银行卡（绑定后还得校验短信才算绑定成功）
	 * 
	 * @author XieZG
	 * @Date:2016年2月18日下午3:06:50
	 * @param province
	 * @return
	 */
	@RequestMapping("bindBankCard")
	@ResponseBody
	public Result<JSONObject> bindBankCard(HttpServletRequest req) {
		Result<JSONObject> result = quickPayService.bindBank(req);
		return result;
	}

	/**
	 * 查询绑定的银行卡
	 * 
	 * @author XieZG
	 * @Date:2016年2月18日下午3:06:50
	 * @param province
	 * @return
	 */
	@RequestMapping("findBindBank")
	@ResponseBody
	public Result<JSONObject> findBindBank() {
		Result<JSONObject> result = new Result<JSONObject>(Result.Type.SUCCESS);
		JSONObject jsonObject = new JSONObject();
		result.setData(jsonObject);
		jsonObject.put("bankList", quickPayService.findBindBank());
		return result;
	}

	/**
	 * 绑定银行卡短信校验
	 * 
	 * @author XieZG
	 * @Date:2016年2月18日下午3:06:50
	 * @param province
	 * @return
	 */
	@RequestMapping(value="bindBankVerify")
	@ResponseBody
	public Result<JSONObject> bindBankVerify(long authSerialNumber, String validationCode) {
		Result<JSONObject> result = new Result<JSONObject>(Result.Type.FAILURE);
		String msg = quickPayService.bindBankVerify(authSerialNumber, validationCode);
		if (msg==null) {
			result.setType(Result.Type.SUCCESS);
		}else{
			result.addMessage(msg);
		}
		return result;
	}

	/**
	 * 发送快捷支付短信
	 * 
	 * @author XieZG
	 * @Date:2016年2月18日下午3:06:50
	 * @param province
	 * @return
	 */
	@RequestMapping("sendQuickPaySMS")
	@ResponseBody
	public Result<JSONObject> sendQuickPaySMS(Long orderId, Long authSerialNumber) {
		Result<JSONObject> result = new Result<JSONObject>(Result.Type.FAILURE);
		Order order = orderService.getUserOrderById(orderId);
		if(Status.已付款.name().equals(order.getStatus())||Status.付款中.name().equals(order.getStatus())||Status.付款失败.name().equals(order.getStatus())||Status.已撤单.name().equals(order.getStatus())){
			result.addMessage("该订单状态已为："+order.getStatus()+"，不能再支付");
			return result;
		}
		UserBankInfo bankInfo = quickPayService.findBingBankByAuthNum(authSerialNumber);
		String errorMsg=orderService.generateOrderFromAtlantisa(order, bankInfo.getAccountName(), bankInfo.getIdentificationNumber(),bankInfo.getIdentificationType());
		if(errorMsg!=null){
			result.addMessage(errorMsg);
			return result;
		}
		boolean flag = quickPayService.sendQuickPaySMS(order.getReferenceOrder(), authSerialNumber, result);
		if (flag) {
			result.setType(Result.Type.SUCCESS);
		}
		return result;
	}

}
