package com.snowstore.diana.job;

import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.snowstore.diana.service.StatisticsService;

@Component
@EnableScheduling
public class EndDayStatisticsJob {

	@Autowired
	private StatisticsService statisticsService;
	@Autowired
	private SettingsService settingsService;

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	/**
	 * 日终统计
	 * 
	 * @author wulinjie
	 */
	@Scheduled(cron = "0 10 0 * * ?")
	public void updateStatistics() {
		//检查系统设置
		Settings settings = settingsService.getByKey("endDayStatistics");
		if(null!=settings && "off".equals(settings.getValue())){
			return;
		}
		LOGGER.info("统计每日交易量....");
		statisticsService.updateEndDayStatistics();
	}

}
