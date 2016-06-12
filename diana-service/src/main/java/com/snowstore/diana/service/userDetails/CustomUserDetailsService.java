package com.snowstore.diana.service.userDetails;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.User;
import com.snowstore.diana.repository.UserDao;
import com.snowstore.diana.service.CustomerService;
import com.snowstore.diana.vo.PageFormVo;
import com.snowstore.log.service.UserDetailDelegate;
import com.snowstore.log.vo.UserInfo;

@Transactional
@Service
@SuppressWarnings("rawtypes")
public class CustomUserDetailsService implements UserDetailsService,UserDetailDelegate{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CustomerService customerService;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final User user = userDao.findByEmail(username);
		if(null == user){
			throw new UsernameNotFoundException("用户不存在");
		}
		UserDetailsImpl userDetail = new UserDetailsImpl(user.getId());
		BeanUtils.copyProperties(user, userDetail);
		return userDetail;
	}
	
	/**
	 * 查询用户信息
	 * @param id
	 * @param form
	 * @return
	 */
	public Page<User> qureyUser(final long id, PageFormVo form) {
		return userDao.findAll(new Specification<User>() {
			
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();
				//list.add(cb.equal(root.get("customer"), id));
				Predicate[] p = new Predicate[list.size()];
				return cb.and(list.toArray(p));
			}
			
		}, form);
	}

	@Override
	public UserInfo getUserInfo() {
		UserInfo userInfo = new UserInfo();
		try {
			UserDetailsImpl userDetails = customerService.getUserDetails();
			userInfo.setUserName(userDetails.getMobile());
			userInfo.setUserId(userDetails.getId());
		} catch (Exception e) {
			userInfo.setUserName("unknow user");
			userInfo.setUserId(0L);
		}
		return userInfo;
	}
	
}
