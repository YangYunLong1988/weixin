package com.snowstore.diana.service;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.GiftCode;
import com.snowstore.diana.domain.Logistics;
import com.snowstore.diana.domain.Logistics.LogisticsStatus;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Package;
import com.snowstore.diana.domain.UserGift;
import com.snowstore.diana.repository.UserGiftDao;
import com.snowstore.diana.service.logistics.LogisticsService;
import com.snowstore.diana.vo.GiftVo;

/**
 * 用户礼品service处理层
 * 
 * @author: fuhongxing
 * @date: 2015年10月22日
 * @version 1.0.0
 */
@Service
@Transactional
public class UserGiftService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserGiftService.class);

	@Autowired
	private UserGiftDao userGiftDao;

	@Autowired
	private PackageService packageService;

	@Autowired
	private LogisticsService logisticsService;

	@Autowired
	private GiftCodeService giftCodeService;

	/** 添加用户礼品 */
	public UserGift save(UserGift userGift) {
		return userGiftDao.save(userGift);
	}

	/** 根据用户ID和订单ID查询用户礼品记录信息 */
	public List<UserGift> findByUserGift(Long userId, Long orderId) {
		return userGiftDao.findByUserGift(userId, orderId);
	}

	/** 根据订单ID查询礼品记录信息 */
	public List<UserGift> findByOrderId(Long orderId) {
		return userGiftDao.findByOrderId(orderId);
	}

	/**
	 * 
	 * 查询用户所有礼品
	 * 
	 * @param userId
	 * @return 添加省份城市区域参数
	 * @author mingzhi.dong
	 * @updateDate 2016年1月19日
	 */
	public LinkedList<GiftVo> findByUserId(Long userId) {
		List<UserGift> result = userGiftDao.findByUserId(userId);
		LinkedList<GiftVo> resultVo = new LinkedList<GiftVo>();
		for (UserGift userGift : result) {
			Package refpackage = packageService.findById(userGift.getRefPackage());
			if (null == refpackage) {
				continue;
			}
			GiftVo vo = new GiftVo();
			vo.setId(userGift.getId());
			vo.setSelected(true);
			vo.setPerson(refpackage.getRecipients());
			vo.setPhone(refpackage.getMobile());
			vo.setAddress(refpackage.getAddress());
			vo.setGift(userGift.getRefGift());
			vo.setGiftName(userGift.getGiftName());
			// 添加省份城市区域信息 @updateDate 2016年1月19日
			vo.setProvince(refpackage.getProvince());
			vo.setCity(refpackage.getCity());
			vo.setArea(refpackage.getArea());
			resultVo.add(vo);
		}
		LOGGER.info("用户:" + userId + ",礼品数量:" + result.size());
		return resultVo;
	}

	public UserGift saveUserGift(UserGift userGift, Package pkg) {
		pkg = packageService.save(pkg);
		userGift.setRefPackage(pkg.getId());
		userGift = userGiftDao.save(userGift);
		return userGift;
	}

	/**
	 * @param id
	 * @return
	 */
	public GiftVo get(Long id) {
		UserGift userGift = userGiftDao.findOne(id);
		GiftVo vo = new GiftVo();
		if (null == userGift) {
			return null;
		}
		Package refpackage = packageService.findById(userGift.getRefPackage());
		vo.setId(userGift.getId());
		vo.setSelected(true);
		vo.setPerson(refpackage.getRecipients());
		vo.setPhone(refpackage.getMobile());
		vo.setAddress(refpackage.getAddress());
		vo.setGift(userGift.getRefGift());
		vo.setGiftName(userGift.getGiftName());
		if ("mm14".equals(userGift.getRefGift())) {
			vo = getBestCakeVo(vo, userGift);
		} else if ("mm57".equals(userGift.getRefGift())) {
			vo = getDQGiftVo(vo, userGift);
		}
		// 添加省份城市区域信息 @updateDate 2016年1月19日
		vo.setProvince(refpackage.getProvince());
		vo.setCity(refpackage.getCity());
		vo.setArea(refpackage.getArea());
		vo.setOrderId(userGift.getOrder().getId());
		Logistics logistics = logisticsService.findOneByOrderId(userGift.getOrder().getId());
		if (logistics != null) {
			vo.setLogisticsCompany(logistics.getCompany());
			vo.setLogisticsSn(logistics.getSn());
			LogisticsStatus status = logistics.getStatus();
			vo.setLogisticsStatus(null == status ? null : status.name());
		}
		return vo;
	}

	/**
	 * 获取蛋糕券礼品
	 * 
	 * @return
	 */
	public List<UserGift> findBestCakeGift() {
		return userGiftDao.findBestCakeGift();
	}

	/**
	 * 获取未兑换码的礼品卷
	 * 
	 * @author mingzhi.dong
	 * @date 2016年3月7日
	 * @return
	 */
	public List<UserGift> findRedeenUserGriftService() {
		return userGiftDao.findRedeenUserGrift();
	}

	/**
	 * @param vo
	 * @param userGift
	 * @return
	 */
	public GiftVo getBestCakeVo(GiftVo vo, UserGift userGift) {
		if (!Order.Status.已付款.name().equals(userGift.getOrder().getStatus())) {

		} else if (null == userGift.getBestCakeCode()) {
			vo.setGiftNo("努力生成中......");
		} else {
			vo.setGiftNo(userGift.getBestCakeCode());
		}
		return vo;
	}

	/**
	 * @param vo
	 * @param userGift
	 * @return
	 */
	public GiftVo getDQGiftVo(GiftVo vo, UserGift userGift) {
		if (!Order.Status.已付款.name().equals(userGift.getOrder().getStatus())) {
			return vo;
		}
		GiftCode code = giftCodeService.getGiftCodeByOrderId(userGift.getOrder().getId(), GiftCode.RedeemCodeType.DQ.name());
		if (null != code) {
			vo.setGiftNo(code.getRedeemCode().split(",")[0]);
			vo.setGiftNo1(code.getRedeemCode().split(",")[1]);
			vo.setGiftNo2(code.getRedeemCode().split(",")[2]);
			vo.setGiftNo3(code.getRedeemCode().split(",")[3]);
			vo.setGiftNo4(code.getRedeemCode().split(",")[4]);
		} else {
			vo.setGiftNo("努力生成中......");
			vo.setGiftNo1("努力生成中......");
			vo.setGiftNo2("努力生成中......");
			vo.setGiftNo3("努力生成中......");
			vo.setGiftNo4("努力生成中......");
		}
		return vo;
	}

	/**
	 * 根据用户id和订单id查询
	 * 
	 * @author mingzhi.dong
	 * @date 2016年3月21日
	 * @param userId
	 * @param orderId
	 * @param refGift
	 * @return
	 */
	public UserGift getUserGiftService(Long userId,Long orderId,String refGift) {
		return userGiftDao.findRedeenUserGiftCode(userId, orderId, refGift);
	}
}
