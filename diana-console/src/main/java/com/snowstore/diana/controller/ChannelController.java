package com.snowstore.diana.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snowstore.diana.domain.Channel;
import com.snowstore.diana.service.ChannelService;
import com.snowstore.log.annotation.UserLog;
@Controller
@RequestMapping("/channel")
public class ChannelController {
	@Autowired
	private ChannelService channelService;
	
	@RequestMapping("/getSubChannel")
	@ResponseBody
	@UserLog(remark = "获取二级渠道")
	public List<Channel> getSubChannel(String platform){
		return channelService.getSubChannelByParent(platform);
	}
}
