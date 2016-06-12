package com.snowstore.diana.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.snowstore.diana.common.DianaConstants.DianaPlatform;
import com.snowstore.log.annotation.UserLog;

@Controller
@RequestMapping("/activity")
public class ActivityController {
	
	/**
	 * 生财有鹿App跳转用
	 * 
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping("/jinlu")
	@UserLog(remark = "【活动】/activity/jinlu")
	public String jinlu(Model model, HttpServletRequest req) {
		String platform = DianaPlatform.PROMETHEUS.name();
		return "forward:/channel/entrance?platform="+platform;
	}

	@RequestMapping("/jinlu0")
	@UserLog(remark = "【活动】/activity/jinlu0")
	public String jinlu0(Model model, HttpServletRequest req) {
		String platform = "JINLU0";
		return "forward:/channel/entrance?platform="+platform;
	}

	/**
	 * 阮经天
	 * 
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping("/jinlucctv1")
	@UserLog(remark = "【活动】/activity/jinlucctv1")
	public String jinlucctv1(Model model, HttpServletRequest req) {
		String platform = "JINLUCCTV1";
		return "forward:/channel/entrance?platform="+platform;
	}

	/**
	 * 岳云鹏
	 * 
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping("/jinlucctv2")
	@UserLog(remark = "【活动】/activity/jinlucctv2")
	public String jinlucctv2(Model model, HttpServletRequest req) {
		String platform = "JINLUCCTV2";
		return "forward:/channel/entrance?platform="+platform;
	}

	/**
	 * 撒贝宁
	 * 
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping("/jinlucctv3")
	@UserLog(remark = "【活动】/activity/jinlucctv3")
	public String jinlucctv3(Model model, HttpServletRequest req) {
		String platform = "JINLUCCTV3";
		return "forward:/channel/entrance?platform="+platform;
	}

	/**
	 * 沙溢
	 * 
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping("/jinlucctv4")
	@UserLog(remark = "【活动】/activity/jinlucctv4")
	public String jinlucctv4(Model model, HttpServletRequest req) {
		String platform = "JINLUCCTV4";
		return "forward:/channel/entrance?platform="+platform;
	}

	/**
	 * 华少
	 * 
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping("/jinlucctv5")
	@UserLog(remark = "【活动】/activity/jinlucctv5")
	public String jinlucctv5(Model model, HttpServletRequest req) {
		String platform = "JINLUCCTV5";
		return "forward:/channel/entrance?platform="+platform;
	}

	/**
	 * 乐嘉
	 * 
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping("/jinlucctv6")
	@UserLog(remark = "【活动】/activity/jinlucctv6")
	public String jinlucctv6(Model model, HttpServletRequest req) {
		String platform = "JINLUCCTV6";
		return "forward:/channel/entrance?platform="+platform;
	}

	@RequestMapping("/yijia0")
	@UserLog(remark = "【活动】/activity/yijia0")
	public String yijia0(Model model, HttpServletRequest req) {
		String platform = DianaPlatform.JUPITER.name();
		return "forward:/channel/entrance?platform="+platform;
	}
}
