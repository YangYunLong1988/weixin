package com.snowstore.diana.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.Channel;
import com.snowstore.diana.domain.ChannelProduct;
import com.snowstore.diana.repository.ChannelProductDao;

@Service
@Transactional
public class ChannelProductService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelProductService.class);

	@Autowired
	private ChannelProductDao channelProductDao;
	
	@Autowired
	private ChannelService channelService;
	
	public List<String> getByChannelCode(String code){
		List<String> list = new ArrayList<String>();
		Channel channel = channelService.getChannelByCode(code);
		if(null == channel){
			LOGGER.error("渠道[{}]不存在。",code);
			throw new RuntimeException("渠道不存在！");
		}
		List<ChannelProduct> channelProductList = channelProductDao.findByChannelId(channel.getId());
		for (ChannelProduct channelProduct : channelProductList) {
			list.add(channelProduct.getProductType());
		}
		if(list.isEmpty()){
			throw new RuntimeException("渠道对应理财产品不存在！");
		}
		return list;
	}

	public ChannelProduct saveOrUpdate(ChannelProduct channelProduct){
		return channelProductDao.save(channelProduct);
	}

	public ChannelProduct findByProductTypeAndChannelId(String productType,Long channelId){
		return channelProductDao.findByProductTypeAndChannelId(productType,channelId);
	}
}
