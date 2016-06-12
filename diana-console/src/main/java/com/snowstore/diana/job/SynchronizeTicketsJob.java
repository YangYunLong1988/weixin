package com.snowstore.diana.job;

import java.util.List;
import java.util.Map;

import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.SettingsService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.service.MovieCodeServiceV2;

@Component
@EnableScheduling
public class SynchronizeTicketsJob {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Autowired
	private MovieCodeServiceV2 movieCodeServiceV2;
	@Autowired
	private SettingsService settingsService;

	private final static int MAX_FETCH_ROW = 1000;

	@Scheduled(fixedRate = 1000 * 60 * 10)
	public void splitSeatInfo(){
		Settings settings = settingsService.getByKey("splitSeatInfo");
		if(null != settings && "off".equals(settings.getValue())){
			return;
		}

		List<Object[]> resultList;
		try{
			while (true){
				resultList= movieCodeServiceV2.getSeatMerged(MAX_FETCH_ROW);
				if(CollectionUtils.isEmpty(resultList)){
					break;
				}
				movieCodeServiceV2.splitSeatInfo(resultList);
			}
		}catch (Exception e){
			LOGGER.info("拆分电影票座位信息异常", e);
		}
	}

	@Scheduled(cron="0 0/2 * * * ?")
	public void confirmTicketsStatus(){
		//检查系统设置
		Settings settings = settingsService.getByKey("confirmTicketsStatus");
		if(null!=settings && "off".equals(settings.getValue())){
			return;
		}

		LOGGER.info("开始确认中影订单");
		Long start = System.currentTimeMillis();
		Map<Object,List<Tickets>> ticketsMap = movieCodeServiceV2.findUnpaidTickets();
		for (Object orderCode : ticketsMap.keySet()) {
			try{
				List<Tickets> items = ticketsMap.get(orderCode);
				movieCodeServiceV2.confirmOrderStatus(orderCode.toString(), items);
			}catch (Exception e){
				LOGGER.error("确认中影订单异常",e);
			}
		}
		Long end = System.currentTimeMillis();
		LOGGER.info("确认中影订单结束，执行耗时：{}秒", (end - start) / 1000);
	}

}
