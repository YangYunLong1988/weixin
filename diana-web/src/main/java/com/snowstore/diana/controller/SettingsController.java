package com.snowstore.diana.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.SettingsService;
import com.snowstore.diana.vo.Result;
import com.snowstore.log.annotation.UserLog;

/**
 * Created by wulinjie on 2016/3/2.
 */
@Controller
@RequestMapping("settings")
public class SettingsController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private SettingsService settingsService;
	
	@RequestMapping("/check")
	@ResponseBody
	@UserLog(remark = "【前台设置】检查大众点评开启状态")
	public Result<String> checkFuncStatus(String key){
		Result<String> result = new Result<String>(Result.Type.SUCCESS);
		Settings settings = settingsService.getByKey(key);
		if(null ==settings || "off".equals(settings.getValue())){
			result.setType(Result.Type.FAILURE);
			LOGGER.info("大众点评已关闭");
		}
		return result;
	}
	
}
