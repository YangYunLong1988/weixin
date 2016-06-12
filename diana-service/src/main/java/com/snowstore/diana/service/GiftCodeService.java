package com.snowstore.diana.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.GiftCode;
import com.snowstore.diana.domain.GiftCode.Status;
import com.snowstore.diana.domain.GiftCode.RedeemCodeType;
import com.snowstore.diana.domain.UserGift;
import com.snowstore.diana.repository.GiftCodeDao;
import com.snowstore.diana.repository.UserGiftDao;
import com.snowstore.diana.vo.Result;

@Service
@Transactional
public class GiftCodeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GiftCodeService.class);

	@Autowired
	private GiftCodeDao giftCodeDao;
	
	@Autowired
	private UserGiftDao userGiftDao;

	/**
	 * 执行为订单分配兑换码
	 * @author mingzhi.dong
	 * @date 2016年3月7日
	 * @param orderId 订单id
	 * @param redeemCodeType 类型
	 * @return
	 */
	public Result<String> updateRedeemCodeService(Long orderId, String redeemCodeType,UserGift ug) {
		Result<String> result = null;
		GiftCode giftCode = giftCodeDao.findTop1ByRedeemCodeTypeAndStatus(redeemCodeType, Status.未分配.name());
		if(null==giftCode){
			LOGGER.info("{}渠道兑换码数量不足,可用卡券数量：0", redeemCodeType);
			result = new Result<String>(Result.Type.FAILURE, redeemCodeType + "渠道兑换码数量不足,可用卡券数量：0");
		}else{
			giftCode.setOrderId(orderId);
			giftCode.setStatus(Status.已分配.name());
			giftCode = giftCodeDao.save(giftCode);
			ug  = userGiftDao.findOne(ug.getId());
			ug.setStatus(UserGift.Status.已分配.name());
			ug = userGiftDao.save(ug);
		}
		return result;
	}
	
	public GiftCode getGiftCodeByOrderId(Long orderId,String redeemCodeType){
		List<GiftCode> list = giftCodeDao.findByOrderIdAndRedeemCodeType(orderId, redeemCodeType);
		if(list.isEmpty()){
			return null;
		}
		if(list.size() > 1){
			LOGGER.error("数据异常，一个订单分配多个DQ券{}",orderId);
		}
		return list.get(0);
	}

	public Long getDQCodeAmount(){
		return giftCodeDao.countByRedeemCodeTypeAndStatus(RedeemCodeType.DQ.name(), Status.未分配.name());
	}
}
