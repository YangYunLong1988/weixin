package com.snowstore.diana.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.snowstore.diana.service.userDetails.UserDetailsImpl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.snowstore.diana.common.DianaConstants;
import com.snowstore.diana.domain.Channel;
import com.snowstore.diana.domain.Product;
import com.snowstore.diana.domain.Product.Status;
import com.snowstore.diana.service.ChannelProductService;
import com.snowstore.diana.service.ChannelService;
import com.snowstore.diana.service.CustomerService;
import com.snowstore.diana.service.ProductService;
import com.snowstore.diana.service.UnionUserService;
import com.snowstore.diana.utils.SignUtil;
import com.snowstore.diana.vo.ProductVo;
import com.snowstore.log.annotation.UserLog;

@Controller
@RequestMapping("/channel")
public class ChannelController {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Autowired
	private ProductService productService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ChannelProductService channelProductService;
	@Autowired
	private UnionUserService unionUserService;
	@Autowired
	private ChannelService channelService;

	/**
	 * 渠道手机登陆入口
	 * 
	 * @param platform
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping("/entrance")
	@UserLog(remark = "【接口】渠道手机登陆入口")
		public String channelEntrance(String platform, String url, HttpServletRequest req, Model model) {
			String orginPlatform = (String) req.getSession().getAttribute(DianaConstants.SESSION_PLATFORM);
			if( StringUtils.isNotEmpty(orginPlatform) && StringUtils.isNotEmpty(platform) && !orginPlatform.equals(platform) ){
				UserDetailsImpl userDetails = customerService.getUserDetails();
				SecurityContextHolder.clearContext();
				if(null != userDetails){
					String mobile = userDetails.getMobile();
					customerService.pushUserToSecurity(null, mobile, platform);
				}
			}

			if( StringUtils.isEmpty(platform) && StringUtils.isNotEmpty(orginPlatform)){
				platform = orginPlatform;
			}else if(StringUtils.isEmpty(platform) &&  StringUtils.isEmpty(orginPlatform)){
				platform = DianaConstants.DianaPlatform.WAP.name();
			}
			req.getSession().setAttribute(DianaConstants.SESSION_PLATFORM, platform);

			PageRequest page = new PageRequest(0, 1000, new Sort(new Order(Direction.ASC, "status"), new Order(Direction.DESC, "sortNum"), new Order(Direction.DESC, "id")));
			List<Status> status = new ArrayList<Product.Status>();
			status.add(Product.Status.在售);
			List<ProductVo> list = productService.findByStatusAndProductType(page, status, channelProductService.getByChannelCode(platform));
			model.addAttribute("list", list);
			if (null != url) {
				return "redirect:" + url;
			}
			return "index";
	}

	@RequestMapping("/signEntrance")
	@UserLog(remark = "【接口】渠道接入验证接口")
	public String indexSign(String sign, HttpServletRequest req, Model model) throws Exception {
		req.getSession().invalidate();
		SecurityContextHolder.clearContext();
		String[] SIGN_KEYS = new String[] { "mobile", "platform", "timestamp", "url", "sign" };// 需要验证的参数
		Map<String, String> parameterMap = new HashMap<String, String>();
		for (String key : SIGN_KEYS) {
			String value = req.getParameter(key);
			if (value == null && !"url".equals(key)) {
				// 缺少签名必要参数
				model.addAttribute("message", "缺少签名必要参数");
				return "authorizeError";
			} else {
				parameterMap.put(key, req.getParameter(key));
			}

		}

		//校验接入渠道是否存在
		String orginPlatform = (String) req.getSession().getAttribute(DianaConstants.SESSION_PLATFORM);
		String platform = req.getParameter("platform");
		Channel channel;
		if (StringUtils.isEmpty(orginPlatform)) {
			channel = channelService.getChannelByCode(platform);
			if (null == channel) {
				model.addAttribute("message", "该渠道不存在");
				LOGGER.info("该渠道不存在:{}", platform);
				return "authorizeError";
			}
			orginPlatform = channel.getCode();
		}else{
			channel = channelService.getChannelByCode(orginPlatform);
		}

		parameterMap.remove("sign");
		String dianaSign = SignUtil.signature(channel.getSecretKey(), parameterMap);
		if (!dianaSign.toLowerCase().equals(sign)) {
			LOGGER.info("签名失败:{}", dianaSign.toLowerCase());
			model.addAttribute("message", "签名错误");
			return "authorizeError";
		}

		String timeOfRequest = req.getParameter("timestamp");
		if (!"pro".equals(System.getProperty("diana.env"))) {
			LOGGER.info("非生产环境跳过时间戳校验!");
		} else if (Math.abs(System.currentTimeMillis() - Long.parseLong(timeOfRequest)) >= 300000) {
			// 时间差超过六秒
			Date now = new Date();
			Date dateOfRequest = new Date(Long.parseLong(timeOfRequest));
			model.addAttribute("message", "请求时间与当前时间差过长");
			LOGGER.info("当前时间:{}", now.toString());
			LOGGER.info("请求时间:{}", dateOfRequest.toString());
			return "authorizeError";
		}

		req.getSession().setAttribute(DianaConstants.SESSION_PLATFORM, orginPlatform);
		customerService.pushUserToSecurity(null, req.getParameter("mobile"), orginPlatform);

		// 判断是否有unionId，没有就创建UnionId
		String mobile = req.getParameter("mobile");
		if (unionUserService.findByMobile(mobile) == null) {
			unionUserService.add(mobile);
		}

		String url = req.getParameter("url");
		if (null != url) {
			return "redirect:" + url;
		}
		return "forward:/channel/entrance";
	}
}
