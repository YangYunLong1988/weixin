package com.snowstore.diana.job;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.snowstore.diana.domain.Product.Type;
import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.snowstore.diana.common.Calendars;
import com.snowstore.diana.domain.Product;
import com.snowstore.diana.service.ProductService;

@Service
public class SynchronizeProductJob {
	@Autowired
	private ProductService productService;
	@Autowired
	private SettingsService settingsService;

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	/**
	 * 增量添理财产品
	 */
	@Scheduled(cron="0 0/5 * * * ?")
	public void updateIncrements() {
		//检查系统设置
		Settings settings = settingsService.getByKey("updateIncrements");
		if(null!=settings && "off".equals(settings.getValue())){
			return;
		}

		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.HOUR_OF_DAY)<2){
			return;
		}
		LOGGER.info("开始同步理财产品");
		productService.synchronizeProductFromSingleCertificate(Calendars.format("yyyy-MM-dd", new Date()));
	}
	
	/**
	 * 更新理财产品状态
	 */
	@Scheduled(cron="0 0/5 * * * ?")
	public void updateProductStatus(){
		//检查系统设置
		Settings settings = settingsService.getByKey("updateProductStatus");
		if(null!=settings && "off".equals(settings.getValue())){
			return;
		}

		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.HOUR_OF_DAY)<2){
			return;
		}
		LOGGER.info("更新理财产品状态");
		List<Product> list = productService.findByStatusInAndTypeNot(Arrays.asList(Product.Status.初始,Product.Status.在售), Type.电影转礼品);
		productService.updateProductFromSingleCertificate(list);
	}
}
