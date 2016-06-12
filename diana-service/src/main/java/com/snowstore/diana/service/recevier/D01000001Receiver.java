package com.snowstore.diana.service.recevier;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.common.DianaConstants;
import com.snowstore.diana.domain.Channel;
import com.snowstore.diana.domain.ChannelProduct;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.domain.User.Role;
import com.snowstore.diana.service.ChannelProductService;
import com.snowstore.diana.service.ChannelService;
import com.snowstore.diana.service.CustomerService;
import com.snowstore.hera.connector.vo.diana.D01000001;
import com.zendaimoney.hera.connector.vo.Datagram;

/**
 * Created by wulinjie on 2016/1/25.
 */
@Service
@Transactional
public class D01000001Receiver {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private ChannelProductService channelProductService;
	@Autowired
	private CustomerService customerService;
	

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	// 成功
	public final static String SUCCESSED = "000000";
	// 失败
	public final static String FAILED = "000001";

	/**
	 * 新增接入渠道
	 * @param datagram
	 * @return
	 */
	public Datagram receive(Datagram datagram) {
		LOGGER.info("新增接入渠道");

		//authorityFilter.doFilter(datagram);

		D01000001 d01000001 = (D01000001) datagram.getDatagramBody();
		try{
			if(StringUtils.isEmpty(d01000001.getChannelCode())){
				return setBackMessage(datagram, "D01000001新增渠道失败：渠道代码不能为空", FAILED);
			}

			if(StringUtils.isEmpty(d01000001.getProductType())){
				return setBackMessage(datagram, "D01000001新增渠道失败：产品类型不能为空", FAILED);
			}

			if( d01000001.getProductType().indexOf(DianaConstants.ACTIVITY_100) < 0 &&  d01000001.getProductType().indexOf(DianaConstants.ACTIVITY_1) < 0){
				return setBackMessage(datagram, "D01000001新增渠道失败：无效的产品类型", FAILED);
			}

			if(StringUtils.isEmpty(d01000001.getSecretKey())){
				return setBackMessage(datagram, "D01000001新增渠道失败：接入密钥不能为空", FAILED);
			}

			Channel channel = channelService.getChannelByCode(d01000001.getChannelCode());
			if(null != channel){
				return setBackMessage(datagram, "D01000001新增渠道失败：该渠道已存在", FAILED);
			}

			Long platformNo = 0L;
			String path = "";
			try{
				platformNo = channelService.generateChannelNo();	//生成渠道编号
				LOGGER.info("生成渠道编号：{}", platformNo);
				path = channelService.generateChannelPath();	//生成查询路径
				LOGGER.info("生成渠道路径：{}", path);
			}catch (Exception e){
				return setBackMessage(datagram, "D01000001新增渠道失败：渠道已达上限", FAILED);
			}

			//新增渠道
			channel = new Channel();
			channel.setCode(d01000001.getChannelCode());	//渠道代码
			channel.setName(d01000001.getChannelName());
			if(StringUtils.isEmpty(channel.getName())){
				channel.setName(channel.getCode());			//渠道名称
			}
			channel.setParentId(null);						//父渠道ID
			channel.setPath(path);							//查询路径
			channel.setPlatformNo(platformNo);				//渠道编号
			channel.setSecretKey(d01000001.getSecretKey());	//接入密钥

			//新增渠道管理员
			User user = new User();
			user.setEmail(d01000001.getChannelCode() + "@jinlufund.com");
			user.setPlatform(d01000001.getChannelCode());
			user.setChannel(d01000001.getChannelCode());
			user.setRole(Role.CHANNEL.name());
			user.setPlatformNo(platformNo);
			user.setUsername(d01000001.getChannelCode());
			user.setPassword(passwordEncoder.encode("000000"));
			user = customerService.saveOrUpdate(user);

			channel.setUserId(user.getId());				//管理用户
			channel = channelService.saveOrUpdate(channel);

			//配置渠道产品类型
			String[] productTypes = d01000001.getProductType().split(",");
			for(String productType : productTypes){
				ChannelProduct channelProduct = channelProductService.findByProductTypeAndChannelId(productType, channel.getId());
				if(null == channelProduct){
					channelProduct = new ChannelProduct();
					channelProduct.setChannelId(channel.getId());
					channelProduct.setProductType(productType);
					channelProductService.saveOrUpdate(channelProduct);
				}
			}
		}catch (Exception e){
			LOGGER.error("新增接入渠道异常", e);
			return setBackMessage(datagram, "D01000001新增渠道失败：", FAILED);
		}
		LOGGER.info("新增接入渠道完成");
		return setBackMessage(datagram, "D01000001新增渠道成功", SUCCESSED);
	}

	/**
	 * 设置响应报文
	 * @param datagram
	 * @param Memo
	 * @param code
	 * @return
	 */
	public Datagram setBackMessage(Datagram datagram,String Memo,String code){
		datagram.getDatagramBody().setMemo(Memo);
		datagram.getDatagramBody().setOperateCode(code);
		datagram.getDatagramBody().setOperateTime(new Date());
		return datagram;
	}

}
