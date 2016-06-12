package com.snowstore.diana.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.code.kaptcha.Constants;
import com.snowstore.diana.common.DianaConstants;
import com.snowstore.diana.domain.Channel;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Statistics;
import com.snowstore.diana.domain.UnionUser;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.domain.User.Role;
import com.snowstore.diana.repository.OrderDao;
import com.snowstore.diana.repository.StatisticsDao;
import com.snowstore.diana.repository.UserDao;
import com.snowstore.diana.service.userDetails.UserDetailsImpl;
import com.snowstore.diana.utils.Arith;
import com.snowstore.diana.utils.BeanUtilsExtended;
import com.snowstore.diana.utils.DateUtils;
import com.snowstore.diana.utils.ExcelUitl;
import com.snowstore.diana.utils.PwdGen;
import com.snowstore.diana.vo.PageFormVo;
import com.snowstore.diana.vo.PageTables;
import com.snowstore.diana.vo.Result;
import com.snowstore.diana.vo.Result.Type;
import com.snowstore.diana.vo.UserVo;
import com.snowstore.hera.connector.vo.mars.MarsMessage;

@Service
@Transactional
public class CustomerService {

	private static final Result<String> RESULT_FAIL = new Result<String>(Result.Type.FAILURE, "图片验证码错误！");

	private static final Result<String> RESULT_OK = new Result<String>(Result.Type.SUCCESS);

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private StatisticsDao statisticsDao;

	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private ApolloSender apolloSender;

	@Autowired
	private ChannelService channelService;

	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valueOps;

	@Autowired
	HttpSession httpSession;

	@Autowired
	private UnionUserService unionUserService;

	private Map<Integer, String> codeMap = new HashMap<Integer, String>(1000);

	private Random random = new Random(1000);

	/**
	 * 保存用户信息
	 * @param user
	 * @return
	 */
	public User saveOrUpdate(User user) {
		return userDao.save(user);
	}

	/**
	 * 初始化系统用户
	 * 
	 * @return
	 */
	@PostConstruct
	public synchronized void initSystemUser() {
		List<User> list = userDao.findByRole(User.Role.SYSTEM.name());
		if (null == list || list.size() == 0) {
			User system = new User();
			system.setUsername(User.Role.SYSTEM.name());
			system.setPassword(passwordEncoder.encode("123456"));
			system.setRole(User.Role.SYSTEM.name());
			userDao.save(system);
		}
		for (int i = 1; i < 1000; i++) {
			codeMap.put(i, RandomStringUtils.randomNumeric(4));
		}

	}

	/**
	 * 获取系统用户
	 * 
	 * @return
	 */
	public User getSystemUser() {
		return userDao.findByRole(User.Role.SYSTEM.name()).get(0);
	}

	/**
	 * 获取当前用户
	 * 
	 * @return curent user
	 */
	public User getCurrentUser() {
		Authentication authen = SecurityContextHolder.getContext().getAuthentication();
		if (!"anonymousUser".equals(authen.getPrincipal())) {

			return loadById(((User) authen.getPrincipal()).getId());
		} else {
			return null;
		}
	}

	public UserDetailsImpl getUserDetails() {
		Authentication authen = SecurityContextHolder.getContext().getAuthentication();
		if (!"anonymousUser".equals(authen.getPrincipal())) {
			return (UserDetailsImpl) authen.getPrincipal();
		} else {
			return null;
		}
	}

	/**
	 * 根据用户ID，查询用户
	 * 
	 * @param id
	 *            用户ID
	 * @return 用户
	 */
	public User loadById(Long id) {
		return userDao.findOne(id);
	}

	/**
	 * 根据手机号码，查询用户
	 * 
	 * @author wulinjie
	 * @param mobile
	 *            手机号码
	 * @return 用户
	 */
	public User loadByMobile(String mobile) {
		return userDao.findByMobile(mobile);
	}

	/***
	 * 获取用户信息(分页获取用户数据)
	 * 
	 * @param user
	 * 
	 * @param form
	 *            分页对象
	 * @return 用户信息列表
	 */
	public Page<User> findUserList(final UserVo user, PageFormVo form) {
		return userDao.findAll(buildSpecification(user), form);

	}

	/**
	 * 封装用户VO对象数据
	 * 
	 * @param page
	 * @return
	 */
	public PageTables<UserVo> getPageTables(Page<User> page) {
		PageTables<UserVo> pageTables = new PageTables<UserVo>();
		try {
			List<UserVo> userVoList = new ArrayList<UserVo>();
			UserVo userVo = null;
			for (User user : page.getContent()) {
				userVo = new UserVo();
				BeanUtils.copyProperties(userVo, user);
				Iterator<Order> it = user.getOrder().iterator();
				BigDecimal sum = BigDecimal.ZERO;
				Integer count = 0;
				while (it.hasNext()) {
					Order order = it.next();
					if (order.getStatus().equals(Order.Status.已付款.name())) {
						sum = Arith.add(sum, order.getAmount());
						count++;// 只统计成功购买的
					}
				}
				userVo.setPlatform(channelService.getChannelByCode(user.getPlatform()).getName());
				userVo.setConsumerTotal(sum);// 总金额
				userVo.setOrderCount(count);// 用户购买次数
				userVoList.add(userVo);
			}

			pageTables.setRecordsTotal(page.getTotalElements());
			pageTables.setRecordsFiltered(page.getTotalElements());
			pageTables.setData(userVoList);
		} catch (Exception e) {
			LOGGER.error("封装用户VO对象异常 ", e);
		}
		return pageTables;
	}

	/**
	 * 导出用户数据
	 * 
	 * @param request
	 * @param response
	 * @param user
	 *            查询条件
	 * @return
	 */
	public void exportUser(HttpServletRequest request, HttpServletResponse response, final UserVo user) {
		try {
			String[] header = { "用户编号", "绑定手机", "接入时间", "购买次数", "消费总额", "所属渠道" };
			List<UserVo> userVoList = new ArrayList<UserVo>();

			List<User> userList = userDao.findAll(buildSpecification(user), new Sort(Direction.fromString("desc"), "id"));
			// 使用VO对象封装 数据
			UserVo userVo = null;
			BigDecimal sum = null;
			for (User u : userList) {
				userVo = new UserVo();
				BeanUtils.copyProperties(userVo, u);
				sum = orderDao.findByUserOrderTotalAmount(u, Order.Status.已付款.name());
				Integer count = orderDao.countByUserAndStatus(u, Order.Status.已付款.name());
				userVo.setConsumerTotal(sum == null ? BigDecimal.ZERO : sum);// 总金额
				userVo.setOrderCount(count);// 用户购买次数
				userVo.setPlatform(channelService.getChannelByCode(u.getPlatform()).getName());
				userVoList.add(userVo);
			}

			ExcelUitl.exportExcel("用户信息" + DateUtils.dateToString(new Date(), "yyMMdd") + ".xlsx", header, userVoList, request, response, "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			LOGGER.error("用户导出异常!", e);
		}
	}

	private Specification<User> buildSpecification(final UserVo user) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				User currUser = getCurrentUser();

				List<Predicate> list = new ArrayList<>();
				List<Channel> channelList = new ArrayList<Channel>();
				List<String> channelCodeList = new ArrayList<String>();

				if (user.getPlatformId() != null) {
					channelList = channelService.getSubChannelByParentIdRecursively(user.getPlatformId());
				}else{
					if(Role.CHANNEL == Role.valueOf(currUser.getRole())){
						Channel channel = channelService.getChannelByCode(currUser.getPlatform());
						channelList = channelService.getSubChannelByParentIdRecursively(channel.getId());
					}else{
						channelList = channelService.getAllChannel();
					}
				}

				if(!channelList.isEmpty()){
					for (Channel channel : channelList) {
						channelCodeList.add(channel.getCode());
					}
				}

				list.add(root.<String> get("platform").in(channelCodeList));		//所属渠道

				if (user != null) {
					// 用户ID
					if (user.getId() != null) {
						list.add(cb.equal(root.<Long> get("id"), user.getId()));
					}

					// 按照手机号码模糊查询
					if (StringUtils.isNotEmpty(user.getMobile())) {
						list.add(cb.like(root.<String> get("mobile"), '%' + user.getMobile() + '%'));
					}

					// 按照时间段查询
					if (user.getBeginDate() != null && user.getEndDate() != null) {
						list.add(cb.greaterThanOrEqualTo(root.<Date> get("createdDate"), user.getBeginDate()));
						list.add(cb.lessThanOrEqualTo(root.<Date> get("createdDate"), user.getEndDate()));
					}

				}
				//不查询后台管理员和渠道管理员
				list.add(cb.or(cb.isNull(root.<String> get("role")), cb.and(cb.notEqual(root.<Long> get("role"), User.Role.ADMIN.name()),cb.notEqual(root.<String> get("role"), User.Role.CHANNEL.name()))));

				Predicate[] predicate = new Predicate[list.size()];
				return cb.and(list.toArray(predicate));
			}
		};
	}

	/**
	 * 保存用户登录会话
	 * @param openId	微信openId
	 * @param mobile	手机号码
	 * @param platform 登陆渠道
	 */
	public synchronized void pushUserToSecurity(String openId, String mobile, String platform) {
		Channel channel = channelService.getChannelByCode(platform);	//查询当前登陆渠道
		User user = userDao.findByMobileAndPlatform(mobile, platform);	//查询当前登陆用户
		UnionUser unionUser = unionUserService.findByMobile(mobile);
		if (user == null) {
			user = new User();
			user.setMobile(mobile);						//手机
			user.setPlatform(channel.getCode());		//渠道
			user.setChannel(channel.getCode());			//子渠道
			user.setPlatformNo(channel.getPlatformNo());//渠道编码
			user.setRole(Role.CUSTOMER.name());		//角色(客户)
			user.setLastLoginTime(new Date());			//最后登录时间
			user = userDao.save(user);
		} else {
			user.setRole(Role.CUSTOMER.name());
			user.setLastLoginTime(new Date());
			user = userDao.save(user);
		}

		if(null == unionUser){
			unionUser = unionUserService.add(mobile);
		}
		final Long id = user.getId();
		UserDetailsImpl userDetails = new UserDetailsImpl(id);
		userDetails.setPlatform(platform);
		userDetails.setMobile(mobile);
		userDetails.setOpenId(openId);
		userDetails.setUnionUserId(unionUser.getId());
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		Calendar calendar = Calendar.getInstance();
		calendar = org.apache.commons.lang3.time.DateUtils.truncate(calendar, Calendar.DATE);
		Statistics statistics = statisticsDao.findByDateStatisticsAndPlatform(calendar.getTime(), platform);
		if (null != statistics) {
			statistics.setAccessStatistics(statistics.getAccessStatistics() + 1);
			statistics = statisticsDao.save(statistics);
		}
	}

	/**
	 * 根据角色查找用户
	 * 
	 * @param role
	 * @return
	 */
	public List<User> findByRole(String role) {
		return userDao.findByRole(role);
	}

	/**
	 * 保存或更新用户
	 * 
	 * @param user
	 * @param id
	 * @return
	 */
	public Result<String> saveOrUpdate(User user, Long id) {
		Result<String> result = new Result<>(Type.SUCCESS);
		try {
			User tempUser = user;
			if (id != null) {
				tempUser = userDao.findOne(id);
				BeanUtilsExtended.copyProperties(tempUser, user);
			}

			userDao.save(tempUser);
		} catch (Exception e) {
			result.setType(Type.FAILURE);
		}

		return result;
	}

	/**
	 * 批量修改用户
	 * 
	 * @param users
	 * @param ids
	 * @return
	 */
	public Result<String> saveOrUpdateList(List<User> users, List<Long> ids) {
		Result<String> result = new Result<>(Type.SUCCESS);
		try {
			int i = 0;
			for (Long id : ids) {
				this.saveOrUpdate(users.get(i), id);
			}
		} catch (Exception e) {
			result.setType(Type.FAILURE);
		}

		return result;
	}

	/**
	 * 删除用户
	 * 
	 * @return
	 */
	public Result<String> del(List<User> users) {
		Result<String> result = new Result<>(Type.SUCCESS);
		try {
			userDao.delete(users);
		} catch (Exception e) {

		}

		return result;
	}

	/**
	 * 根据邮箱查找用户
	 * 
	 * @param email
	 * @return
	 */
	public User findByEmail(String email) {
		return userDao.findByEmail(email);
	}

	/**
	 * 根据邮件查找
	 * 
	 * @param emails
	 * @return
	 */
	public List<User> findByEmailIn(List<String> emails) {
		return userDao.findByEmailIn(emails);
	}

	/**
	 * 根据id查找
	 * 
	 * @param id
	 * @return
	 */
	public User findOne(Long id) {
		return userDao.findOne(id);
	}

	/**
	 * 发送duanxi
	 * 
	 * @param mobile
	 * @param request
	 * @param response
	 * @param flag
	 * @return
	 */
	public Result<String> sendVerifyCode(String mobile, @RequestParam(required = false) String flag, String imgCode, Boolean checkValidateCode) {

		Result<String> result = RESULT_FAIL;

		String trueImageCode = (String) httpSession.getAttribute(Constants.KAPTCHA_SESSION_KEY);
		if (checkValidateCode && !imgCode.equals(trueImageCode))
			return result;
		try {
			// User user = new User();
			// 产生验证码

			String validateCode = codeMap.get(random.nextInt(999)); // RandomStringUtils.randomNumeric(4);
			LOGGER.info("手机注册验证码：" + validateCode);
			StringBuilder sendMessage = new StringBuilder();
			// 判断是否重置密码请求
			if (flag != null && flag.equals("reset")) {
				sendMessage.append("您的验证码为：");
			} else if (flag != null && flag.equals("balala")) {
				sendMessage.append("您的验证码为：");
			} else {
				sendMessage.append("您的注册验证码为：");
			}
			sendMessage.append(validateCode + ",手机尾号（" + mobile.substring(mobile.length() - 4, mobile.length()) + "）（验证码30分钟内有效）。");

			LOGGER.info("发送短信内容:" + sendMessage);

			if (!StringUtils.isBlank(validateCode)) {
				// 保存验证码和验证码产生的时间
				// user.setMobileVerifyCode(validateCode);
				// user.setMobileVerifyCodeTime(new Date());
				// user.setMobile(mobile);
				LOGGER.info("验证码产生时间：" + new Date());
				// this.create(user);
				// 发送短信
				// 发送短信
				// httpSession.setAttribute("validateCode", validateCode);
				valueOps.set(mobile, validateCode);
				sendMessage(mobile, sendMessage.toString());
				result = RESULT_OK;
			}
			if(!"pro".equals(System.getProperty("diana.env"))){
				result.setData(validateCode);
			}
		} catch (Exception e) {
			LOGGER.error("调用mars-->发送短信验证码异常!", e);
		}
		return result;
	}

	/**
	 * 打包数据mars系统需要的数据
	 * 
	 * @param mobile
	 * @param code
	 * @return
	 */
	public void sendMessage(String mobile, String code) {
		MarsMessage datagramBody = new MarsMessage();
		datagramBody.setTo(mobile);
		datagramBody.setFrom("1");
		datagramBody.setContent(code);
		datagramBody.setOprOption("4");
		datagramBody.setSubject("1");
		datagramBody.setMessageType("1");
		apolloSender.sendMailMessage(datagramBody);

	}

	/**
	 * 验证短信验证码
	 * 
	 * @param mobile
	 * @param code
	 * @return
	 */
	public boolean validatePhoneCode(String mobile, String code) throws Exception {
		String trueCode = valueOps.get(mobile);
		if (code.equals(trueCode)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置用户使用的设备
	 * 
	 * @param req
	 * @param device
	 */
	public void setUserDevice(HttpServletRequest req, String device) {
		req.getSession().setAttribute("userDevice", device);
	}

	/**
	 * 获取用户使用的设备
	 * 
	 * @param req
	 * @return
	 */
	public String getUserDevice(HttpServletRequest req) {
		String device = (String) req.getSession().getAttribute("userDevice");
		if(req.getHeader("User-Agent").contains("MicroMessenger")){
			device = DianaConstants.DianaDevice.JUPITER.name();
		} 
		if (device == null) {
			device = DianaConstants.DianaDevice.UNKNOW.name();
		}
		return device;
	}
	
	/**创建管理员
	 * @param email
	 */
	public void createAdmain(String email){
		User admin = new User();
		admin.setEmail(email);
		PwdGen pwdGen = new PwdGen();
		String password = pwdGen.getpwd(8, true, true, true);
		admin.setPassword(passwordEncoder.encode(password));
		admin.setRole(User.Role.ADMIN.toString());
		admin = userDao.save(admin);
		MarsMessage marsMessage = new MarsMessage();
		marsMessage.setTo(email);
		marsMessage.setMessageType("3");
		marsMessage.setFrom("1");
		marsMessage.setOprOption("2");
		marsMessage.setContent("您的管理员账号为：" + marsMessage.getTo() + ",密码为：" + password+",登陆地址："+System.getProperty("diana.domain")+":"+System.getProperty("server.port"));
		marsMessage.setSubject("管理员密码");
		apolloSender.sendMailMessage(marsMessage);
	}

	/**
	 * 获取当前用户一级渠道下的所有用户ID
	 * @author wulinjie
	 * @return 一级渠道下的所有用户ID
	 */
	public List<Long> loadByPlatformNoCorrespond(){
		User user = getCurrentUser();	//获取当前登陆用户
		Long unionId = unionUserService.findOrAdd(user.getMobile());	//获取当前用户的unionId
		List<User> userList = userDao.findByUnionIdAndPlatformNoBetween(unionId, user.getPlatformNoStart(), user.getPlatformNoEnd());	//根据unionId和platformNo，查找所有相关用户
		List<Long> userIdList = new ArrayList<Long>();
		if(!userList.isEmpty()){
			for (User item : userList) {
				userIdList.add(item.getId());
			}
		}
		return userIdList;
	}
	
	/**渠道用户查询
	 * @param user
	 */
	public void pushUserToSecurity(User user){
		UserDetailsImpl userDetails = new UserDetailsImpl(user.getId());
		userDetails.setPlatform(user.getPlatform());
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	public User getChannelUser(String platform){
		return userDao.findByPlatformAndRole(platform, User.Role.CHANNEL.name());
	}
}
