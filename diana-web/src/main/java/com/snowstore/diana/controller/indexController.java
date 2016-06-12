package com.snowstore.diana.controller;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.foxinmy.weixin4j.util.DigestUtil;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.util.Config;
import com.snowstore.diana.common.DianaConstants;
import com.snowstore.diana.common.DianaConstants.DianaPlatform;
import com.snowstore.diana.domain.Channel;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.service.ChannelService;
import com.snowstore.diana.service.CustomerService;
import com.snowstore.diana.vo.Result;
import com.snowstore.log.annotation.UserLog;

@Controller
public class indexController {
	private static final String[] SIGN_KEYS = new String[] { "mobile", "openId", "platform", "timestamp", "url" };
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private Config config;

	@RequestMapping("/authorizeSign")
	@UserLog(remark = "【安全】authorizeSign：签名验证接入的渠道")
	public String indexSign(Model model, HttpServletRequest req, String sign, String platform, String mobile) {
		JSONObject json = new JSONObject();
		json.putAll(req.getParameterMap());
		LOGGER.info("请求参数" + json.toJSONString());
		StringBuilder sb = new StringBuilder();
		Pattern p = Pattern.compile("^1\\d{10}$");
		Matcher m = p.matcher(mobile);
		if (null == mobile || !m.matches()) {
			model.addAttribute("message", "手机号码格式错");
			return "authorizeError";
		}

		for (String key : SIGN_KEYS) {
			String value = req.getParameter(key);
			if (value == null) {
				// 缺少签名必要参数
				model.addAttribute("message", "缺少签名必要参数");
				return "authorizeError";
			} else {
				sb.append(key).append("=").append(value);
			}
		}

		// 校验渠道是否存在
		String channelCode = req.getParameter("platform");
		Channel channel = channelService.getChannelByCode(channelCode);
		if (null == channel) {
			model.addAttribute("message", "该渠道不存在");
			return "authorizeError";
		}

		String signSecret = channel.getSecretKey();
		if (StringUtils.isEmpty(signSecret)) {
			// 缺少配置项
			LOGGER.info("缺少签名密钥配置项");
			model.addAttribute("message", "系统繁忙，请稍后再试");
			return "authorizeError";
		}
		sb.append("secret=").append(signSecret);
		String newSign = DigestUtil.MD5(sb.toString());
		if (!newSign.equals(sign)) {
			// 签名验证失败
			LOGGER.info("签名失败：" + newSign);
			model.addAttribute("message", "签名失败");
			return "authorizeError";
		}

		String redirect = null;
		req.getSession().setAttribute(DianaConstants.SESSION_PLATFORM, platform);
		if (DianaConstants.DianaPlatform.JUPITER.name().equals(platform)) {
			customerService.pushUserToSecurity(null, mobile, platform);
			customerService.setUserDevice(req, DianaConstants.DianaDevice.JUPITER.name());
		} else if (DianaConstants.DianaPlatform.PROMETHEUS.name().equals(platform)) {
			customerService.pushUserToSecurity(null, mobile, platform);
			customerService.setUserDevice(req, DianaConstants.DianaDevice.PROMETHEUS.name());
			redirect = req.getParameter("url");
		} else {
			return "authorizeError";
		}
		if (redirect == null || redirect.trim().length() == 0) {
			redirect = "forward:/activity/yijia0";
		}
		return redirect;
	}

	/**
	 * 判断当前用户平台
	 * 
	 * @return
	 */
	@RequestMapping("/getPlatform")
	@ResponseBody
	@UserLog(remark = "判断当前用户平台")
	public Result<String> getPlatform() {
		Result<String> result = new Result<>();
		result.setType(Result.Type.SUCCESS);
		String platform = DianaPlatform.WAP.name();
		User user = customerService.getCurrentUser();
		if (null != user) {
			platform = user.getPlatform();
		}
		result.setData(platform);
		return result;
	}

	/**
	 * 判断当前用户设备
	 * 
	 * @return
	 */
	@RequestMapping("/getDevice")
	@ResponseBody
	@UserLog(remark = "判断当前用户设备")
	public Result<JSONObject> getDevice(HttpServletRequest req) {
		JSONObject obj = new JSONObject();
		Result<JSONObject> result = new Result<>();
		result.setType(Result.Type.SUCCESS);
		obj.put("device", customerService.getUserDevice(req));
		String platform = (String) req.getSession().getAttribute(DianaConstants.SESSION_PLATFORM);
		if (StringUtils.isEmpty(platform)) {
			platform = DianaConstants.DianaPlatform.WAP.name();
		}
		obj.put("platform", platform);
		result.setData(obj);
		return result;
	}

	@RequestMapping("/login")
	@UserLog(remark = "跳转至登录页面")
	public String toLogin(Model model, Boolean checkValidateCode, HttpServletRequest req) {
		String returnUrl = (String) req.getSession().getAttribute("returnUrl");
		if (null == returnUrl || returnUrl.endsWith("generateOrder")) {
			returnUrl = "/channel/entrance";
		}
		model.addAttribute("returnUrl", returnUrl);
		if (null == checkValidateCode) {
			checkValidateCode = true;
		}
		model.addAttribute("checkValidateCode", checkValidateCode);
		return "login";
	}

	@RequestMapping("/getLoginPhoneCode")
	@ResponseBody
	@UserLog(remark = "登录获取手机校验码")
	public Result<String> getLoginPhoneCode(String mobile, String flag, String imgCode, Boolean checkValidateCode) {
		return customerService.sendVerifyCode(mobile, flag, imgCode, checkValidateCode);
	}

	@RequestMapping("/submitLogin")
	@ResponseBody
	@UserLog(remark = "登录信息提交")
	public Result<String> submitLogin(HttpServletRequest req, String mobile, String code, String imgCode, Boolean checkValidateCode) {
		HttpSession session = req.getSession();
		String platform = (String) session.getAttribute(DianaConstants.SESSION_PLATFORM);
		String channel = (String) session.getAttribute(DianaConstants.SESSION_CHANNEL);
		if (platform == null) {
			platform = DianaConstants.DianaPlatform.WAP.name();
		}
		if (channel == null) {
			channel = platform;
		}

		// 默认登陆结果 true
		Result<String> result = new Result<String>(Result.Type.SUCCESS);
		try {
			// 图片验证码
			String trueImageCode = (String) req.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
			if (!trueImageCode.equals(imgCode)) {
				LOGGER.warn(mobile + " 图片验证码错误！");
				result.setType(Result.Type.FAILURE);
				result.addMessage("图片验证码错误");
				return result;
			}
			// 手机验证码
			if (!customerService.validatePhoneCode(mobile, code)) {
				LOGGER.warn(mobile + " 手机验证码错误！");
				result.setType(Result.Type.FAILURE);
				result.addMessage("手机验证码错误");
				return result;
			}
			// 登录成功
			req.getSession().setAttribute(DianaConstants.SESSION_PLATFORM, platform);
			customerService.pushUserToSecurity(null, mobile, platform);
			LOGGER.info(mobile + " 登录成功！");
			result.addMessage("登录成功！");
		} catch (Exception e) {
			LOGGER.warn(mobile + " 登录失败！", e);
			result.setType(Result.Type.FAILURE);
			result.addMessage(e.getMessage());
		}
		return result;
	}

	/** 产生图片验证码 */
	@RequestMapping("/getValidateCode")
	@UserLog(remark = "产生图片验证码")
	public void getValidateCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setDateHeader("Expires", 0);
		// Set standard HTTP/1.1 no-cache headers.
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Set standard HTTP/1.0 no-cache header.
		response.setHeader("Pragma", "no-cache");
		// return a jpeg
		response.setContentType("image/jpeg");
		// create the text for the image
		String code = config.getProducerImpl().createText();
		// store the text in the session
		request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, code);
		// create the image with the text
		BufferedImage bi = config.getProducerImpl().createImage(code);
		LOGGER.info("图片验证码: " + code);
		OutputStream out = response.getOutputStream();

		// write the data out
		ImageIO.write(bi, "jpg", out);
		try {
			out.flush();
		} finally {
			out.close();
		}
	}

	@RequestMapping("/")
	@UserLog(remark = " / 请求")
	public String index(HttpServletRequest req) {
		String orginPlatform = (String) req.getSession().getAttribute(DianaConstants.SESSION_PLATFORM);
		if (null == orginPlatform) {
			req.getSession().setAttribute(DianaConstants.SESSION_PLATFORM, DianaPlatform.WAP.name());
		}
		return "forward:/channel/entrance";
	}

}
