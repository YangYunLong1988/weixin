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

import com.snowstore.diana.domain.BestcakeCard;
import com.snowstore.diana.domain.UserGift;
import com.snowstore.diana.service.BestCakeCardService;
import com.snowstore.diana.service.UserGiftService;

@Component
@EnableScheduling
public class PortionBestckeCodeJob {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Autowired
	private UserGiftService userGiftService;
	@Autowired
	private BestCakeCardService bestCakeCardService;
	@Autowired
	private SettingsService settingsService;

	@Scheduled(cron = "0 * * * * ?")
	public void execute() {
		//检查系统设置
		Settings settings = settingsService.getByKey("portionBestckeCode");
		if(null!=settings && "off".equals(settings.getValue())){
			return;
		}
		LOGGER.info("开始分配蛋糕券code......");
		List<UserGift> gifts = userGiftService.findBestCakeGift();
		for (UserGift userGift : gifts) {
			BestcakeCard bestcakeCard = bestCakeCardService.getOne();
			if (null == bestcakeCard) {
				throw new RuntimeException("系统异常，蛋糕券不足。。。。。。");
			} else {
				userGift.setBestCakeCode(bestcakeCard.getCode());
				bestcakeCard.setIsBind(true);
				bestCakeCardService.save(bestcakeCard);
				userGiftService.save(userGift);
			}
		}
	}

}
