package com.snowstore.diana.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.snowstore.diana.common.Calendars;
import com.snowstore.diana.domain.CardStock;
import com.snowstore.diana.domain.CardStock.Type;
import com.snowstore.diana.domain.Channel;
import com.snowstore.diana.service.CardStockService;
import com.snowstore.diana.service.ChannelService;
import com.snowstore.diana.service.CustomerService;
import com.snowstore.diana.service.StatisticsService;
import com.snowstore.diana.vo.PageFormVo;
import com.snowstore.diana.vo.PageTables;
import com.snowstore.diana.vo.Result;
import com.snowstore.diana.vo.StatisticsVo;
import com.snowstore.log.annotation.UserLog;

@Controller
public class indexController extends BaseController {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private CustomerService customerService;
	@Autowired
	private StatisticsService statisticsService;
	@Autowired
	private CardStockService cardStockService;
	@Autowired
	private ChannelService channelService;

	@RequestMapping("/login")
	@UserLog(remark = "安全-->返回登录页面")
	public String login(Model model, HttpServletRequest request) {
		request.getParameterMap();
		Object loginMessage = request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
		if (loginMessage != null) {
			model.addAttribute("loginMessage", loginMessage.toString());
		}
		return "login";
	}

	@RequestMapping("/")
	@UserLog(remark = "返回后台左侧主页index")
	public String index(Model model) {
		model.addAttribute("users", customerService.getCurrentUser());
		return "index";
	}

	@RequestMapping("/main")
	@UserLog(remark = "返回后台右侧主页main")
	public String main() {
		return "main";
	}

	@RequestMapping(value = "/loadStatisticsList")
	@ResponseBody
	@UserLog(remark = "查询各个渠道统计信息")
	public PageTables<StatisticsVo> loanStatisticsList(Date beginDate, Date endDate, Long channelId, Long subChannelId, Integer draw, Integer start, Integer length) {

		PageFormVo pageFormVo = new PageFormVo();
		pageFormVo.setPage(start / 10 + 1);
		pageFormVo.setRows(length);
		pageFormVo.setStart(start);
		pageFormVo.setLength(length);
		pageFormVo.setDraw(draw);
		pageFormVo.setSortSet(new Sort(Direction.DESC, new String[]{"dateStatistics","platform"}));

		Page<StatisticsVo> page = statisticsService.findAllByPage(beginDate, endDate, channelId, subChannelId, pageFormVo);

		// 封装分页对象数据
		PageTables<StatisticsVo> pageTables = new PageTables<StatisticsVo>();
		List<StatisticsVo> statisticsList = page.getContent();
		pageTables.setDraw(draw);
		pageTables.setRecordsTotal(page.getTotalElements());
		pageTables.setRecordsFiltered(page.getTotalElements());
		pageTables.setData(statisticsList);
		return pageTables;
	}

	@RequestMapping("/summaryStatisticsList")
	@ResponseBody
	@UserLog(remark = "汇总已成交金额")
	public Result<BigDecimal> summaryStatisticsList(Date beginDate, Date endDate,Long channelId,Long subChannelId) {
		BigDecimal summary = statisticsService.summaryStatistics(beginDate, endDate,channelId,subChannelId);
		return new Result<BigDecimal>(Result.Type.SUCCESS, "success", summary);
	}

	@RequestMapping("/getChannelList")
	@ResponseBody
	@UserLog(remark = "初始化渠道下拉框，获取渠道列表传至后台页面")
	public JSONObject getChannelList(){
		List<Channel> channelList = channelService.getTopChannelByCurrUser();
		JSONObject json = JSON.parseObject("{}");
		if( channelList != null ){
			json.put("type", "SUCCESS");
			json.put("data", channelList);
		}else{
			json.put("type", "FAILURE");
			json.put("data", null);
		}
		return json;
	}

	@RequestMapping("/getSubChannelList")
	@ResponseBody
	@UserLog(remark = "初始化子渠道下拉框，获取子渠道列表传至后台页面")
	public JSONObject getSubChannelList(Long channelId){
		if(null == channelId){
			return null;
		}
		Channel channel = channelService.get(channelId);
		List<Channel> subChannelList = channelService.getSubChannelByParent(channel.getCode());
		JSONObject json = JSON.parseObject("{}");
		if( subChannelList != null ){
			json.put("type", "SUCCESS");
			json.put("data", subChannelList);
		}else{
			json.put("type", "FAILURE");
			json.put("data", null);
		}
		return json;
	}

	@RequestMapping("/exportStatisticsExcel")
	@ResponseBody
	@UserLog(remark = "导出后台统计信息")
	public void exportStatisticsExcel(Date beginDate, Date endDate,  Long channelId, Long subChannelId , HttpServletRequest request, HttpServletResponse response) {
		try {
			PageFormVo pageFormVo = new PageFormVo();
			pageFormVo.setPage(1);
			pageFormVo.setRows(1048575);
			pageFormVo.setStart(0);
			pageFormVo.setLength(1048575);
			pageFormVo.setDraw(1);
			pageFormVo.setSortSet(new Sort(Direction.DESC, new String[]{"dateStatistics","platform"}));
			statisticsService.exportStatisitcs(beginDate, endDate, channelId, subChannelId, request, response,pageFormVo);
		} catch (Exception e) {
			LOGGER.error("导出统计信息异常!" + e.toString());
		}
	}

	/** 系统管理 */
	@RequestMapping("/systemManage")
	@UserLog(remark = "【按钮】点击系统管理按钮，返回该页面")
	public String systemManage() {
		return "systemManage";
	}

	/**
	 * 生成100张卡券(测试方法)
	 * 
	 * @author wangtf
	 * @Date:2015年11月16日上午10:23:01
	 */
	@RequestMapping("/initCardStock")
	@ResponseBody
	@UserLog(remark = "生成100张卡券(测试方法)")
	public JSONObject initCardStock(@RequestParam(value = "size") Integer size, @RequestParam(value = "gift") Integer gift) {
		JSONObject josn = new JSONObject();
		if (gift == 0 || gift == 1) {

		} else {
			josn.put("message", "礼品数量只能是0或1");
			return josn;
		}
		BigDecimal amount = new BigDecimal("100");
		LOGGER.info("生成" + size + "张卡券 start");
		int index = 0;
		boolean flag = true;
		while (flag) {
			try {
				CardStock cardStock = cardStockService.createCardStock(amount, Type.影视.name(), gift);
				josn.put(index + "", cardStock);
				index++;
			} catch (Exception e) {
				LOGGER.error("生成兑换码出错", e);
			}
			if (index >= size) {
				flag = false;
			}
		}
		LOGGER.info("生成" + size + "张卡券 end");
		return josn;
	}
	
	@RequestMapping("/importUsers")
	@ResponseBody
	public Result<String> importUsers(){
		Result<String> result = new Result<String>(Result.Type.SUCCESS);
		try {
			Properties prop = new Properties();
			InputStream is = new FileInputStream("/home/files/importUsers.properties");
			prop.load(new InputStreamReader(is, "UTF-8"));
			LOGGER.info("开始导入苏宁用户和卡券.........");
			prop.size();
			String importDate = prop.getProperty("importDate");
			String memo = prop.getProperty("memo");
			if (!Calendars.format(Calendars.getCurrentDate(), Calendars.YYYY_MM_DD).equals(importDate)) {
				throw new RuntimeException("导入日期不正确");
			}
			Iterator<Object> users = prop.keySet().iterator();
			while (users.hasNext()) {
				cardStockService.importUsersWithCard((String)users.next(),prop,memo);
			}
		} catch (FileNotFoundException f) {
			LOGGER.info("未找到指定文件");
			result.setType(Result.Type.FAILURE);
			result.addMessage("未找到指定文件");
		}catch (Exception e){
			result.setType(Result.Type.FAILURE);
			result.addMessage(e.getMessage());
			LOGGER.error("导入卡券异常", e);
		}
		return result;
	}
}
