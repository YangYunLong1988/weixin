package com.snowstore.diana.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snowstore.diana.domain.Logistics;
import com.snowstore.diana.domain.Logistics.LogisticsStatus;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Package;
import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.domain.UserGift;
import com.snowstore.diana.service.ApolloSender;
import com.snowstore.diana.service.ChannelService;
import com.snowstore.diana.service.CustomerService;
import com.snowstore.diana.service.OrderService;
import com.snowstore.diana.service.PackageService;
import com.snowstore.diana.service.TicketsService;
import com.snowstore.diana.service.UserGiftService;
import com.snowstore.diana.service.logistics.LogisticsService;
import com.snowstore.diana.utils.PwdGen;
import com.snowstore.diana.vo.OrderVo;
import com.snowstore.diana.vo.PageFormVo;
import com.snowstore.diana.vo.PageTables;
import com.snowstore.diana.vo.Result;
import com.snowstore.diana.vo.Result.Type;
import com.snowstore.diana.vo.UserGiftVo;
import com.snowstore.diana.vo.UserVo;
import com.snowstore.hera.connector.vo.mars.MarsMessage;
import com.snowstore.log.annotation.UserLog;

@Controller
@RequestMapping(value = "/user")
public class UserController {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Autowired
	private CustomerService customerService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private OrderService orderService;

	@Autowired
	private PackageService packageService;

	@Autowired
	private TicketsService ticketsService;

	@Autowired
	private UserGiftService userGiftService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private ApolloSender apolloSender;
	@Autowired
	private LogisticsService logisticsService;

	/** 跳转用户管理页面 */
	@RequestMapping(value = "/userList")
	@UserLog(remark = "【用户管理】跳转用户列表页面")
	public String userList(Model model) {
		// 后台管理员显示渠道查询
		User user = customerService.getCurrentUser();
		if (User.Role.ADMIN.name().equals(user.getRole())) {
			model.addAttribute("channelList", channelService.getTopChannelByCurrUser());
		}
		return "userList";
	}

	/**
	 * 加载用户数据
	 * 
	 * @param request
	 * @param draw
	 *            请求次数
	 * @param start
	 *            当前开始的记录数
	 * @param length
	 *            每页显示数量
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@RequestMapping(value = "/loadUserList")
	@ResponseBody
	@UserLog(remark = "【用户管理】跳转用户列表页面后，加载用户数据")
	public PageTables<UserVo> loadUserList(HttpServletRequest request, Integer draw, Integer start, Integer length) throws Exception {
		PageTables<UserVo> pageTables = null;
		try {
			UserVo userVo = createUserVo(request);
			PageFormVo form = new PageFormVo();
			// 计算当前页
			form.setPage(start == 0 ? 1 : start / length + 1);
			form.setRows(length);
			Page<User> page = customerService.findUserList(userVo, form);
			pageTables = customerService.getPageTables(page);
			pageTables.setDraw(draw);
		} catch (Exception e) {
			LOGGER.error("获取用户信息异常", e);
		}
		return pageTables;
	}

	/** 跳转用户订单列表页面 */
	@RequestMapping(value = "/userOrderList/{id}")
	@UserLog(remark = "【用户管理】跳转用户订单列表页面")
	public String userOrderList(@PathVariable Long id, Model model) {
		model.addAttribute("userid", id);
		return "userOrderList";
	}

	/**
	 * 根据订单类型 加载用户订单数据
	 * 
	 * @param request
	 * @param draw
	 *            请求次数
	 * @param start
	 *            当前开始的记录数
	 * @param length
	 *            每页显示数量
	 * @param type
	 *            订单类型
	 * @param id
	 *            用户id
	 * @return
	 */
	@RequestMapping(value = "/loadUserOrder")
	@ResponseBody
	@UserLog(remark = "【用户管理】跳转用户订单列表页面后，加载订单信息")
	public PageTables<OrderVo> loadUserOrder(HttpServletRequest request, Integer draw, Integer start, Integer length, @RequestParam(defaultValue = "movie", value = "type") String type, Long id) {

		PageFormVo form = new PageFormVo();
		PageTables<OrderVo> pageTables = new PageTables<OrderVo>();
		try {
			// 计算当前页
			form.setPage(start == 0 ? 1 : start / length + 1);
			form.setRows(length);

			Page<Order> page = orderService.findByUserOrder(customerService.loadById(id), form);
			List<OrderVo> orderList = orderService.getOrderVo(page.getContent());
			// 封装分页对象数据
			pageTables.setDraw(draw);
			pageTables.setRecordsTotal(page.getTotalElements());
			pageTables.setRecordsFiltered(page.getTotalElements());
			pageTables.setData(orderList);
		} catch (Exception e) {
			LOGGER.error("获取用户订单信息异常", e);
		}

		return pageTables;
	}

	/** 跳转用户收货信息页面 */
	@RequestMapping(value = "/userPackageList/{id}")
	@UserLog(remark = "【用户管理】跳转收货信息页面")
	public String userPackageList(@PathVariable Long id, Model model) {
		model.addAttribute("userid", id);
		return "userPackageList";
	}

	/**
	 * 加载用户收货地址数据
	 * 
	 * @param request
	 * @param draw
	 *            请求次数
	 * @param start
	 *            当前开始的记录数
	 * @param length
	 *            每页显示数量
	 * @return
	 */
	@RequestMapping(value = "/loadUserPackage")
	@ResponseBody
	@UserLog(remark = "【用户管理】跳转收货信息页面后，加载收货信息列表")
	public PageTables<Package> loadUserPackage(HttpServletRequest request, Integer draw, Integer start, Integer length, Long id) {

		PageFormVo form = new PageFormVo();
		PageTables<Package> pageTables = new PageTables<Package>();
		try {
			form.setPage(start == 0 ? 1 : start / length + 1);
			form.setRows(length);
			Page<Package> packagePage = packageService.findByUserPackage(customerService.loadById(id), form);

			pageTables.setDraw(draw);
			pageTables.setRecordsTotal(packagePage != null ? packagePage.getTotalElements() : 0L);
			pageTables.setRecordsFiltered(packagePage != null ? packagePage.getTotalElements() : 0L);
			pageTables.setData(packagePage.getContent());
		} catch (Exception e) {
			LOGGER.error("获取用户包裹地址异常", e);
		}
		return pageTables;
	}

	/** 跳转用户出票记录页面 */
	@RequestMapping(value = "/userTicketList/{userId}/{orderId}")
	@UserLog(remark = "【点击用户管理】存疑， TBD")
	public String userTicketList(@PathVariable Long userId, @PathVariable Long orderId, Model model) {
		model.addAttribute("userid", userId);
		model.addAttribute("orderId", orderId);
		return "userTicketList";
	}

	/**
	 * 加载用户出票记录数据
	 * 
	 * @param request
	 * @param draw
	 *            请求次数
	 * @param start
	 *            当前开始的记录数
	 * @param length
	 *            每页显示数量
	 * @param userId
	 *            用户ID
	 * @param orderId
	 *            订单ID
	 * @return
	 */
	@RequestMapping(value = "/loadUserTicket")
	@ResponseBody
	@UserLog(remark = "【点击用户管理】存疑， TBD")
	public PageTables<Tickets> loaduserTicket(HttpServletRequest request, Integer draw, Integer start, Integer length, Long userId, Long orderId) {

		PageFormVo form = new PageFormVo();
		PageTables<Tickets> pageTables = new PageTables<Tickets>();
		try {
			// 计算当前页
			form.setPage(start == 0 ? 1 : start / length + 1);
			form.setRows(length);
			// 查询用户已兑换的票据
			Page<Tickets> page = ticketsService.findTicketsByUser(userId, orderId, Tickets.Status.已兑换.name(), form);
			// 封装分页对象数据
			List<Tickets> ticketList = page.getContent();

			pageTables.setDraw(draw);
			pageTables.setRecordsTotal(page.getTotalElements());
			pageTables.setRecordsFiltered(page.getTotalElements());
			pageTables.setData(ticketList);
		} catch (Exception e) {
			LOGGER.error("获取用户出票记录信息异常", e);
		}
		return pageTables;
	}

	/** 跳转用户礼品记录页面 */
	@RequestMapping(value = "/userGiftList/{userId}/{orderId}")
	@UserLog(remark = "【点击用户管理】全部订单->跳转查看礼品页面")
	public String userGiftList(@PathVariable Long userId, @PathVariable Long orderId, Model model) {
		model.addAttribute("userid", userId);
		model.addAttribute("orderId", orderId);
		return "userGiftList";
	}

	/**
	 * 加载用户礼品记录数据
	 * 
	 * @param request
	 * @param draw
	 *            请求次数
	 * @param start
	 *            当前开始的记录数
	 * @param length
	 *            每页显示数量
	 * @param userId
	 *            用户ID
	 * @param orderId
	 *            订单ID
	 * @return
	 */
	@RequestMapping(value = "/loadUserGift")
	@ResponseBody
	@UserLog(remark = "【点击用户管理】全部订单->加载礼品页面信息")
	public PageTables<UserGiftVo> loadUserGift(HttpServletRequest request, Integer draw, Integer start, Integer length, Long userId, Long orderId) {

		PageFormVo form = new PageFormVo();

		PageTables<UserGiftVo> pageTables = new PageTables<UserGiftVo>();
		try {
			// 计算当前页
			form.setPage(start == 0 ? 1 : start / length + 1);
			form.setRows(length);
			List<UserGift> userGiftList = userGiftService.findByUserGift(userId, orderId);
			List<Order> suborderList=orderService.findBySubOrderService(orderId);
			for(Order tmp:suborderList){
				userGiftList.add(userGiftService.findByOrderId(tmp.getId()).get(0));
			}
			// 封闭Vo对象
			List<UserGiftVo> list = new ArrayList<UserGiftVo>();
			UserGiftVo userGiftVo = null;
			for (UserGift userGift : userGiftList) {
				userGiftVo = new UserGiftVo();
				Package pack = packageService.findById(userGift.getRefPackage());
				userGiftVo.setGiftName(userGift.getGiftName());// 礼品名称
				userGiftVo.setMobile(pack.getMobile());

				String stringAddress="";
				if(pack.getProvince()!=null){
					stringAddress=stringAddress+pack.getProvince()+' ';
				}
				if(pack.getCity()!=null){
					stringAddress=stringAddress+pack.getCity()+' ';
				}
				if(pack.getArea()!=null){
					stringAddress=stringAddress+pack.getArea()+' ';
				}
				if(pack.getAddress()!=null){
					stringAddress=stringAddress+pack.getAddress();
				}
				userGiftVo.setAddress(stringAddress);
				
				userGiftVo.setRecipients(pack.getRecipients());
				Logistics logistics = logisticsService.findOneByOrderId(Long.valueOf(orderId));
				
				if (logistics == null) {
					list.add(userGiftVo);
					continue;
				}
					
				userGiftVo.setLogisticsCompany(logistics.getCompany());
				userGiftVo.setLogisticsSn(logistics.getSn());
				LogisticsStatus status = logistics.getStatus();
				userGiftVo.setLogisticsStatus(status == null ? null : status.name());
				list.add(userGiftVo);
			}
			// 封装分页对象数据
			pageTables.setDraw(draw);
			pageTables.setRecordsTotal(Long.valueOf(list.size()));
			pageTables.setRecordsFiltered(Long.valueOf(list.size()));
			pageTables.setData(list);
		} catch (Exception e) {
			LOGGER.error("获取用户礼品记录信息异常", e);
		}
		return pageTables;
	}

	/** 用户导出 */
	@RequestMapping("exportUserExcel")
	@ResponseBody
	@UserLog(remark = "【用户管理】用户导出")
	public void exportUserExcel(HttpServletRequest request, HttpServletResponse response) {
		UserVo userVo = createUserVo(request);
		customerService.exportUser(request, response, userVo);
	}

	/**
	 * 生成userVo对象
	 * 
	 * @author XieZG
	 * @Date:2016年1月7日上午10:45:50
	 * @param request
	 * @return
	 */
	private UserVo createUserVo(HttpServletRequest request) {
		String userId = request.getParameter("userId");
		String mobile = request.getParameter("mobile");
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		UserVo userVo = new UserVo(userId, mobile, beginDate, endDate);
		String platform = request.getParameter("platform");
		if (StringUtils.isNotEmpty(platform)) {
			if (StringUtils.isNotEmpty(request.getParameter("subPlatform"))) {
				platform = request.getParameter("subPlatform");
			}
			userVo.setPlatformId(Long.parseLong(platform));
		}
		return userVo;
	}

	/**
	 * 设为管理员
	 * 
	 * @param email
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "setMng")
	@ResponseBody
	@UserLog(remark = "【用户管理】设为管理员")
	public Result<String> setMng(String email, Long userId) {
		Result<String> result = new Result<String>();
		try {
			User customer = customerService.findOne(userId);
			User customerByEmail = customerService.findByEmail(email);
			if (customerByEmail != null && !customerByEmail.getEmail().equals(customer.getEmail())) {
				result.setType(Type.FAILURE);
				result.addMessage("已存在该邮箱下的管理员！");
				return result;
			}

			PwdGen pwdGen = new PwdGen();
			String password = pwdGen.getpwd(8, true, true, true);
			customer.setPassword(bCryptPasswordEncoder.encode(password));
			customer.setIsMng(User.MngState.ISMNG.name());
			customer.setEmail(email);
			customerService.saveOrUpdate(customer, null);

			MarsMessage marsMessage = new MarsMessage();
			marsMessage.setTo(email);
			marsMessage.setMessageType("3");
			marsMessage.setFrom("1");
			marsMessage.setOprOption("2");
			marsMessage.setContent("您的管理员账号为：" + marsMessage.getTo() + ",密码为：" + password);
			marsMessage.setSubject("管理员密码");
			apolloSender.sendMailMessage(marsMessage);

			result.setType(Type.SUCCESS);
		} catch (Exception e) {
			result.setType(Type.FAILURE);
		}

		return result;
	}

	/**
	 * 取消管理员
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "cancelMng")
	@ResponseBody
	@UserLog(remark = "【用户管理】取消管理员")
	public com.snowstore.diana.vo.Result<String> cancelMng(Long id) {
		Result<String> result = new Result<String>();
		try {
			User customer = customerService.findOne(id);
			customer.setIsMng(User.MngState.ISNOTMNG.name());
			;
			customer.setPassword("");
			customerService.saveOrUpdate(customer, id);
			result.setType(Type.SUCCESS);
		} catch (Exception e) {
			result.setType(Type.FAILURE);
		}

		return result;
	}

	@RequestMapping(value = "/changePasswordPage")
	@UserLog(remark = "【系统管理】跳转至修改密码页面")
	public String changePasswordPage(Model model) {
		User user = customerService.getCurrentUser();
		model.addAttribute("username", user.getEmail());
		return "changePassword";
	}

	@RequestMapping(value = "/changePassword")
	@ResponseBody
	@UserLog(remark = "【系统管理】加载至修改密码页面信息")
	public Result<String> changePassword(HttpServletRequest request, HttpServletResponse response) {
		Result<String> result = new Result<String>();
		String oldpass = request.getParameter("oldpass");
		String newpass = request.getParameter("newpass");
		try {
			User customer = customerService.getCurrentUser();
			if (passwordEncoder.matches(oldpass, customer.getPassword())) {// 原始密码正确
				customer.setPassword(passwordEncoder.encode(newpass));
				customerService.saveOrUpdate(customer, customer.getId());
				result.setType(Type.SUCCESS);
			} else {
				result.setType(Type.FAILURE);
				result.addMessage("旧密码不符，请输入正确旧密码");
			}

		} catch (Exception e) {
			result.setType(Type.FAILURE);
			result.addMessage("密码修改失败，请刷新页面试试");
		}
		return result;
	}
}
