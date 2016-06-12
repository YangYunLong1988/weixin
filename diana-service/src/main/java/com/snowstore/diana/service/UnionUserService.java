package com.snowstore.diana.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.UnionUser;
import com.snowstore.diana.repository.UnionUserDao;

/**
 * 唯一用户 service
 * @author XieZG
 * @Date:2016年1月15日下午1:37:33
 */
@Service
@Transactional
public class UnionUserService {
	
	@Autowired
	private UnionUserDao unionUserDao;
	
	public UnionUser findByMobile(String mobile){
		return unionUserDao.findByMobile(mobile);
	}
	@Transactional(readOnly=false)
	public UnionUser add(String mobile){
		UnionUser unionUser=new UnionUser();
		unionUser.setMobile(mobile);
		return unionUserDao.save(unionUser);
	}
	/**
	 * 根据手机号找unionID，没找到就新增
	 * @author XieZG
	 * @Date:2016年1月15日下午3:25:07 
	 * @param mobile
	 * @return
	 */
	@Transactional(readOnly=false)
	public Long findOrAdd(String mobile){
		if(mobile==null){
			return null;
		}
		UnionUser unionUser=unionUserDao.findByMobile(mobile);
		if(unionUser==null){
			unionUser=new UnionUser();
			unionUser.setMobile(mobile);
			unionUserDao.save(unionUser);
		}
		return unionUser.getId();
	}
}
