package com.snowstore.diana.job;

import java.util.List;

import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.snowstore.diana.domain.GiftCode;
import com.snowstore.diana.domain.UserGift;
import com.snowstore.diana.service.GiftCodeService;
import com.snowstore.diana.service.UserGiftService;

@Component
@EnableScheduling
public class PortionGiftCodeJob {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserGiftService userGiftService;
	@Autowired
	private GiftCodeService giftCodeService;
	@Autowired
	private SettingsService settingsService;

	@Scheduled(cron = "0 0/2 * * * ?")
	public void distributionRedeemCode() {
		//检查系统设置
		Settings settings = settingsService.getByKey("portionGiftCode");
		if(null!=settings && "off".equals(settings.getValue())){
			return;
		}
		LOGGER.info("开始分配兑换卷兑换码........");
		List<UserGift> giftList = userGiftService.findRedeenUserGriftService();
		
			for (UserGift ug : giftList) {
				try {
					 giftCodeService.updateRedeemCodeService(ug.getOrder().getId(), GiftCode.RedeemCodeType.DQ.name(),ug);
				} catch (Exception e) {
					LOGGER.error("分配兑换卷兑换码异常..........", e);
				}
			}
		
	}
}
