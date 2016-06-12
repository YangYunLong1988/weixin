package com.snowstore.diana.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.Channel;
import com.snowstore.diana.domain.User.Role;
import com.snowstore.diana.repository.ChannelDao;
import com.snowstore.diana.service.userDetails.UserDetailsImpl;

/**
 * Created by wulinjie on 2015/12/31.
 */
@Service
@Transactional
public class ChannelService {
	@Autowired
	private ChannelDao channelDao;
	@Autowired
	private CustomerService customerService;

	/**
	 * 根据渠道代码，获取渠道信息
	 * 
	 * @param code
	 *            渠道代码
	 * @author wulinjie
	 * @return 渠道信息
	 */

	@Cacheable(value = "channel")
	public Channel getChannelByCode(String code) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}
		return channelDao.findByCode(code);
	}

	/**
	 * 查询所有渠道信息
	 * 
	 * @author wulinjie
	 * @return 渠道信息列表
	 */
	public List<Channel> getAllChannel() {
		return channelDao.findAll();
	}

	/**
	 * 根据当前登录用户的权限，获取一级渠道信息列表
	 * 
	 * @return 渠道信息列表
	 */
	public List<Channel> getTopChannelByCurrUser() {
		UserDetailsImpl userDetails = customerService.getUserDetails();
		if (Role.CHANNEL == Role.valueOf(userDetails.getRole())) {
			List<Channel> list = new ArrayList<Channel>();
			Channel channel = channelDao.findByCode(userDetails.getPlatform());
			list.add(channel);
			return list;
		} else if (Role.ADMIN == Role.valueOf(userDetails.getRole())) {
			return channelDao.findAllTopChannel();
		}
		return null;
	}

	/**
	 * 获取父渠道信息
	 * @author wulinjie
	 * @param channelCode	渠道代码
	 * @return 父渠道信息
	 */
	public Channel getTopChannelByCode(String channelCode){
		Channel sub = getChannelByCode(channelCode);
		String path = sub.getPath();
		String parPath = path.substring(0 ,path.indexOf(".") + 1);
		return getChannelByPath(parPath);
	}

	public Channel getChannelByPath(String path){
		return channelDao.findByPath(path);
	}

	/**
	 * 根据上级渠道，查询所有子渠道
	 * 
	 * @author wulinjie
	 * @param code
	 *            渠道代码
	 * @return 所有子渠道
	 */
	public List<Channel> getSubChannelByParent(String code) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}
		Channel parent = channelDao.findByCode(code);
		if (null != parent) {
			return channelDao.findByParentId(parent.getId());
		}
		return null;
	}

	/**
	 * 根据上级渠道，查询所有子渠道CODE
	 * 
	 * @author wulinjie
	 * @param code
	 *            渠道代码
	 * @return 所有子渠道
	 */
	public List<String> getSubCodeByParent(String code) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}
		Channel parent = channelDao.findByCode(code);
		if (null != parent) {
			List<Channel> channels = channelDao.findByParentId(parent.getId());
			List<String> codes = new ArrayList<String>();
			codes.add(code);
			for (Channel channel : channels) {
				codes.add(channel.getCode());
			}
			return codes;
		}
		return null;
	}

	/**
	 * 根据父渠道ID，递归查询子渠道
	 * 
	 * @author wulinjie
	 * @param id
	 *            父渠道ID
	 * @return
	 */
	public List<Channel> getSubChannelByParentIdRecursively(final Long id) {
		if (null != id) {
			final Channel channel = channelDao.findOne(id);
			return channelDao.findAll(new Specification<Channel>() {
				@Override
				public Predicate toPredicate(Root<Channel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					List<Predicate> list = new ArrayList<>();
					list.add(cb.like(root.<String> get("path"), channel.getPath() + "%"));
					Predicate[] predicate = new Predicate[list.size()];
					return cb.and(list.toArray(predicate));
				}
			});
		}
		return null;
	}

	public Channel get(Long id) {
		return channelDao.findOne(id);
	}

	/**
	 * 生成一级渠道编号
	 * @author wulinjie
	 * @return 一级渠道编号
	 */
	public synchronized Long generateChannelNo(){
		Channel channel = channelDao.findFirstByOrderByPlatformNoDesc();
		Long nextTopPlatformNo = (channel.getPlatformNo() / 1000000) + 1;
		if( nextTopPlatformNo > 999 ){
			throw new RuntimeException("渠道编号超出允许的范围");
		}
		return nextTopPlatformNo * 1000000;
	}

	/**
	 * 生成一级渠道查询路径
	 * @author wulinjie
	 * @return 一级渠道查询路径
	 */
	public synchronized String generateChannelPath(){
		Channel channel = channelDao.findFirstByOrderByPlatformNoDesc();
		Long nextTopPlatformNo = (channel.getPlatformNo() / 1000000) + 1;
		if( nextTopPlatformNo > 999 ){
			throw new RuntimeException("渠道编号超出允许的范围");
		}
		return nextTopPlatformNo + ".";
	}

	public Channel saveOrUpdate(Channel channel){
		return channelDao.save(channel);
	}

}
