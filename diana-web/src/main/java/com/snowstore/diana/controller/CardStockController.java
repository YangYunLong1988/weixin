package com.snowstore.diana.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snowstore.diana.common.Constants;
import com.snowstore.diana.domain.CardStock;
import com.snowstore.diana.domain.CardStock.Status;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.service.CardStockService;
import com.snowstore.diana.service.CustomerService;
import com.snowstore.diana.service.UnionUserService;
import com.snowstore.diana.vo.Result;
import com.snowstore.diana.vo.TicketBuyCodeVo;
import com.snowstore.log.annotation.UserLog;

/**
 * 卡券 Controller
 * 
 * @author XieZG
 * @Date:2015年11月16日下午1:49:45
 */
@Controller
@RequestMapping("/cardstock")
public class CardStockController {
	private static final String CARD_TIMES = "card_times";
	private static final String CARD_RECOVRE = "card_recover";
	private static final int CARD_EXPIRY = 1800;
	private static final String CARD_TIP = "您输入错误次数超限，请在30分钟后重试。";
	@Autowired
	private CardStockService cardStockService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private UnionUserService unionUserService;

	@RequestMapping("verify")
	@ResponseBody
	@UserLog(remark = "【卡券】卡券验证")
	public Result<TicketBuyCodeVo> verifyCardStock(String code) {
		Result<TicketBuyCodeVo> result = new Result<TicketBuyCodeVo>(Result.Type.FAILURE);
		CardStock cardStock = cardStockService.findByCode(code);
		if (cardStock == null) {
			result.addMessage("没有这个代金券!");
		} else if (cardStock.getStatus().equals(Status.已使用.name())) {
			result.addMessage("代金券已使用!");
		} else {
			TicketBuyCodeVo vo = new TicketBuyCodeVo();
			vo.setCode(code);
			vo.setMoney(cardStock.getAmount().intValue());
			// 有礼品是A类，无礼品是B类
			if (cardStock.getGift() > 0) {
				vo.setType(Constants.CARD_STOCK_A);
			} else {
				vo.setType(Constants.CARD_STOCK_B);
			}
			result.setType(Result.Type.SUCCESS);
			result.setData(vo);
		}
		return result;
	}

	@RequestMapping("bind")
	@ResponseBody
	@UserLog(remark = "【卡券】卡券绑定")
	public Result<TicketBuyCodeVo> bindCardStock(String code, String moreCode, HttpServletRequest req, HttpServletResponse res) {
		Cookie recover = getCookie(req, CARD_RECOVRE);
		if (recover != null) {
			try {
				long timestamp = Long.parseLong(recover.getValue());
				if (System.currentTimeMillis() < timestamp) {
					return new Result<TicketBuyCodeVo>(Result.Type.FAILURE, CARD_TIP);
				}
			} catch (Exception e) {
				// ignore
			}
		}
		User user = customerService.getCurrentUser();
		Long unionId = unionUserService.findOrAdd(user.getMobile());
		Result<TicketBuyCodeVo> result = new Result<TicketBuyCodeVo>(Result.Type.FAILURE);
		CardStock cardStock = cardStockService.findByCode(code);
		if (cardStock == null) {
			result.addMessage("没有这个代金券! " + markForWrongTime(req, res));
		} else if (cardStock.getStatus().equals(Status.已使用.name())) {
			result.addMessage("代金券已使用!");
		} else if (cardStock.getUserId() != null) {
			result.addMessage("代金券已被绑定!");
		} else {
			if (code.startsWith("C009")) {
				Boolean flag = cardStockService.checkCardStockAmount(unionId);
				if(!flag){
					result.addMessage("该类卡券使用超过30张，请联系客服。");
					return result;
				}
				if (moreCode == null || moreCode.length() == 0) {
					result.setType(Result.Type.SUCCESS);
					return result;
				}
				if (!cardStockService.checkC009(code, moreCode)) {
					result.addMessage("卡号不存在或者不匹配! " + markForWrongTime(req, res));
					return result;
				}
			}

			TicketBuyCodeVo vo = new TicketBuyCodeVo();
			vo.setCode(code);
			vo.setMoney(cardStock.getAmount().intValue());
			vo.setDesc(cardStock.getMemo());
			// 有礼品是A类，无礼品是B类
			if (cardStock.getGift() > 0) {
				vo.setType(Constants.CARD_STOCK_A);
			} else {
				vo.setType(Constants.CARD_STOCK_B);
			}
			cardStockService.bindCardStock(code, user.getId(), unionId);
			result.setType(Result.Type.SUCCESS);
			result.setData(vo);
			
		}
		return result;
	}

	@RequestMapping("list")
	@ResponseBody
	@UserLog(remark = "【卡券】返回该用户当前的卡券List")
	public Result<ArrayList<TicketBuyCodeVo>> bindCardStock() {
		Long userId = customerService.getCurrentUser().getId();
		Result<ArrayList<TicketBuyCodeVo>> result = new Result<ArrayList<TicketBuyCodeVo>>(Result.Type.SUCCESS);
		List<CardStock> cardStocks = cardStockService.findByUserId(userId);
		ArrayList<TicketBuyCodeVo> list = new ArrayList<TicketBuyCodeVo>();
		for (CardStock cardStock : cardStocks) {
			TicketBuyCodeVo vo = new TicketBuyCodeVo();
			vo.setCode(cardStock.getExchangeCode());
			vo.setMoney(cardStock.getAmount().intValue());
			vo.setDesc(cardStock.getMemo());
			// 有礼品是A类，无礼品是B类
			if (cardStock.getGift() > 0) {
				vo.setType(Constants.CARD_STOCK_A);
			} else {
				vo.setType(Constants.CARD_STOCK_B);
			}
			list.add(vo);
		}
		result.setData(list);
		return result;
	}

	private String markForWrongTime(HttpServletRequest req, HttpServletResponse res) {
		Cookie times = getCookie(req, CARD_TIMES);
		if (times == null) {
			times = new Cookie(CARD_TIMES, "1");
			res.addCookie(times);
			return "";
		} else {
			times.setValue("2");
			res.addCookie(times);
			long timestamp = System.currentTimeMillis() + CARD_EXPIRY * 1000;
			Cookie recover = new Cookie(CARD_RECOVRE, String.valueOf(timestamp));
			recover.setMaxAge(CARD_EXPIRY);
			res.addCookie(recover);
			return CARD_TIP;
		}
	}

	private Cookie getCookie(HttpServletRequest req, String name) {
		Cookie[] cookies = req.getCookies();
		for (Cookie cookie : cookies) {
			if (name.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}
}
