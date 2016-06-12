package com.snowstore.diana.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snowstore.diana.result.GiftResult;
import com.snowstore.diana.service.GiftService;
import com.snowstore.diana.vo.PageFormVo;
import com.snowstore.diana.vo.PageTables;
import com.snowstore.log.annotation.UserLog;

@Controller
@RequestMapping("/gift")
public class GiftController {
	
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private GiftService giftService;
	
	/** 跳转礼品列表页面 */
	@RequestMapping("/giftList")
	@UserLog(remark = "【按钮】礼品-->礼品列表")
	public String giftList(HttpServletRequest request, String type, Model model) {
		return "giftList";
	}
	
	/** 获取礼品数据 */
	@RequestMapping("/loadGiftList")
	@ResponseBody
	@UserLog(remark = "【按钮】礼品-->查询获取礼品数据")
	public PageTables<GiftResult> loadGiftList(HttpServletRequest request, Integer draw, Integer start, Integer length) {
		PageTables<GiftResult> pageTables=null;
		try {
			//封装查询对象
			GiftResult queryGift = createQueryGift(request);
			PageFormVo form = new PageFormVo();
			// 计算当前页
			form.setPage(start == 0 ? 1 : start / length + 1);
			form.setRows(length);
			pageTables = giftService.findGiftList(form,queryGift);
			pageTables.setDraw(draw);
		} catch (Exception e) {
			LOGGER.error("获取订单异常", e);
		}
		return pageTables;
	}
	
	private GiftResult createQueryGift(HttpServletRequest request) {
		String id = request.getParameter("id");
		String mobile = request.getParameter("mobile");
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		GiftResult giftQuery = new GiftResult(mobile, id, beginDate, endDate);
		return giftQuery;
	}
	
	/** 礼品导出 */
	@RequestMapping("/exportGiftExcel")
	@ResponseBody
	@UserLog(remark = "【按钮】礼品-->礼品导出")
	public void exportGiftExcel(HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "movie", value = "type") String type) {
		//封装查询对象
		GiftResult queryGift = createQueryGift(request);
		giftService.exportGift(request, response,queryGift);
	}
}
