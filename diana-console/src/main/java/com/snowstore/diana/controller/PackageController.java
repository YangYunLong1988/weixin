package com.snowstore.diana.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snowstore.diana.domain.Package;
import com.snowstore.diana.service.PackageService;
import com.snowstore.diana.vo.Result;
import com.snowstore.log.annotation.UserLog;

@RequestMapping("/package")
@Controller
public class PackageController extends BaseController {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private PackageService packageService;
	
	/**
	 * 
	 * @param request
	 * @param pack
	 * @return
	 */
	@RequestMapping("/save")
	@ResponseBody
	@UserLog(remark = "【包裹】保存包裹信息")
	public String save(Model model, Long id, String mobile,String recipients,String address) {
		LOGGER.info("修改或保存包裹信息");
		String flag=null;
		Package pack = packageService.findById(id);
		pack.setMobile(mobile);
		pack.setRecipients(recipients);
		pack.setAddress(address);
		if(packageService.save(pack)!=null){
			flag = "success";
		}else{
			flag = "failed";
		}
		return flag;
	}

	@RequestMapping("/updatePackage")
	public String updatePackage(Model model) {
		
		return "index";
	}
	
	
	/** 修改礼品配送信息 */
	@RequestMapping("/orderPackageModify")
	@ResponseBody
	@UserLog(remark = "【后台订单列表按钮】开始修改礼品配送信息")
	public Result<String> orderPackageModify(Long orderIdInput,String packageName,String packageTel,String packagePrivince,String packageCity,String packageArea,String packageTetailAddress) {
	//	model.addAttribute("orderId", 123);
		Package pack = packageService.findPackageByOrderId(orderIdInput);
		
		pack.setAddress(packageTetailAddress);
		pack.setMobile(packageTel);
		pack.setRecipients(packageName);
		// 添加省份城市区域信息 @updateDate 2016年1月19日
		pack.setProvince(packagePrivince);
		pack.setCity(packageCity);
		pack.setArea(packageArea);
		
		try {
			packageService.save(pack);
			return new Result<String>(Result.Type.SUCCESS, "保存成功");
		} catch (Exception e) {
			LOGGER.error("修改配送信息失败！",e);
			return new Result<String>(Result.Type.FAILURE, "保存失败");
		}
		
	}
}
