package com.snowstore.diana.service;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.repository.SettingsDao;
import com.snowstore.diana.vo.Result;
import com.snowstore.diana.vo.Result.Type;

/**
 * 系统设置
 * Created by wulinjie on 2016/3/2.
 */
@Service
@Transactional
public class SettingsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SettingsService.class);

	@Autowired
	private SettingsDao settingsDao;


	/**
	 * 获取当前系统设置项
	 * @author wulinjie
	 * @return 当前系统设置项
	 */
	public List<String> getUsedKeys(){
		return settingsDao.findKeys();
	}

	public Settings getByKey(String key){
		return settingsDao.findByKey(key);
	}

	public void saveOrUpdate(Settings settings){
		settingsDao.save(settings);
	}

	/**
	 * 获取全部设置项
	 * @author wulinjie
	 * @return
	 */
	public List<Settings> getAll(){
		return settingsDao.findAll();
	}

	public Settings get(Long id){
		return settingsDao.findOne(id);
	}

	public void saveAll(HttpServletRequest request){
		Enumeration<String> paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()){
			String key = paramNames.nextElement();
			String value = request.getParameter(key);
			Settings settings = settingsDao.findByKey(key);
			settings.setValue(value);
			settingsDao.save(settings);
		}
	}

	public Result<String> save(String key, String value){
		Settings settings = settingsDao.findByKey(key);
		settings.setValue(value);
		settingsDao.save(settings);
		LOGGER.info("操作成功");
		return new Result<String>(Type.SUCCESS, "操作成功");
	}

}
