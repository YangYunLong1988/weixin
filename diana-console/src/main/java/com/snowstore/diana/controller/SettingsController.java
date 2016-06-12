package com.snowstore.diana.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.snowstore.diana.domain.Product;
import com.snowstore.diana.domain.Settings;
import com.snowstore.diana.service.ProductService;
import com.snowstore.diana.service.SettingsService;
import com.snowstore.diana.vo.Result;
import com.snowstore.log.annotation.UserLog;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wulinjie on 2016/3/2.
 */
@Controller
@RequestMapping("settings")
public class SettingsController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	private static final String FILE_SETTINGS = "default-settings.json";
	private static final String FILE_DATA = "data-init.json";

	@Autowired
	private SettingsService settingsService;
	@Autowired
	private ResourceLoader resourceLoader;
	@Autowired
	private ProductService productService;

	@PostConstruct
	private void init(){
		LOGGER.info("开始初始化系统设置");
		List<String> usedKeys = settingsService.getUsedKeys();
		List<Settings> defaultSettings = getDefaultSettings();
		if(CollectionUtils.isNotEmpty(defaultSettings)){
			for (Settings defaultSetting : defaultSettings) {
				boolean isMissingFromDb = !usedKeys.contains(defaultSetting.getKey());
				if(isMissingFromDb){
					LOGGER.info("缺失系统设置：{}", defaultSetting.getKey());
					settingsService.saveOrUpdate(defaultSetting);
				}
			}
		}
		LOGGER.info("初始化系统设置结束");

		LOGGER.info("开始初始化数据");
		initProducts();
		LOGGER.info("初始化数据结束");
	}

	/**
	 * 获取系统默认设置
	 * @author wulinjie
	 * @return 系统默认设置
	 */
	private List<Settings> getDefaultSettings() {
		LOGGER.info("获取系统默认设置");
		List<Settings> settingsList = new ArrayList<Settings>();
		try {
			String dist = readJsonFromFile(FILE_SETTINGS);
			JSONObject json = JSONObject.parseObject(dist);
			Set<String> categories = json.keySet();
			for (String category : categories) {
				JSONObject items = json.getJSONObject(category);
				for (String key : items.keySet()) {
					JSONObject item = items.getJSONObject(key);
					Settings settings = new Settings();
					settings.setTitle(item.getString("title"));
					settings.setType(category);
					settings.setKey(key);
					settings.setValue(item.getString("defaultValue"));
					settings.setDescription(item.getString("description"));
					settingsList.add(settings);
				}
			}
			return settingsList;
		} catch (Exception e) {
			LOGGER.info("初始化系统设置出错", e);
		}
		return null;
	}

	private void initProducts(){
		LOGGER.info("开始初始化产品表数据");
		String dist = readJsonFromFile(FILE_DATA);
		JSONObject json = JSON.parseObject(dist);
		JSONArray products = json.getJSONArray("product");
		for (Object product : products) {
			Product newProduct = JSONObject.parseObject(product.toString(), Product.class);
			List<Product> productList = productService.findByName(newProduct.getName());
			if(CollectionUtils.isEmpty(productList)){
				productService.saveOrUpdate(newProduct);
			}
		}
	}

	/**
	 * 读取json文件
	 * @author wulinjie
	 * @param fileName  文件名
	 * @return json数据
	 */
	private String readJsonFromFile(String fileName){
		InputStream inputStream = null;
		try{
			Resource resource = resourceLoader.getResource("classpath:" + fileName);
			inputStream = resource.getInputStream();
			String src = IOUtils.toString(inputStream);
			inputStream.close();

			Pattern p = Pattern.compile("\\s*|\r|\n");
			Matcher m = p.matcher(src);
			String dist = m.replaceAll("").trim();
			LOGGER.info("json file：{}", dist);
			return dist;
		}catch (IOException e){
			LOGGER.error("读取json文件异常", e);
		}
		return null;
	}

	@RequestMapping("")
	@UserLog(remark = "【后台系统设置】系统设置-->进入系统设置页面")
	public String settings(Model model){
		model.addAttribute("settingsList", settingsService.getAll());
		return "settings";
	}

	@UserLog(remark = "【后台系统设置】系统设置-->保存系统设置")
	@RequestMapping("/save")
	@ResponseBody
	public Result<String> save(String key, String value){
		return settingsService.save(key, value);
	}

}
