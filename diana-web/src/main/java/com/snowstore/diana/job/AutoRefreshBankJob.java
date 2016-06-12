package com.snowstore.diana.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.snowstore.diana.service.QuickPayService;

@Component
@EnableScheduling
public class AutoRefreshBankJob {
	@Autowired
	private QuickPayService quickPayService;
	/**
	 * 每天凌晨2点更新银行信息
	 * @author XieZG
	 * @Date:2016年2月26日下午5:38:37
	 */
	@Scheduled(cron = "0 0 2 * * ?")
	public void execute() {
		quickPayService.refreshQuickPayBank();
	}
}
