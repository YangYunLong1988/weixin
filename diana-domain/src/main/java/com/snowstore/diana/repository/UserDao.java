package com.snowstore.diana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.User;

public interface UserDao extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {
	public User findByEmail(String email);

	public List<User> findByRole(String role);

	public User findByMobileAndPlatform(String mobile, String platform);

	public List<User> findByEmailIn(List<String> emails);

	public User findByMobileAndMobileVerifyCode(String mobile, String code);

	public User findByMobile(String mobile);

	@Query("from User U where exists (select 1 from UnionUser L where L.mobile = U.mobile and L.id = ?1 ) and U.platformNo >= ?2 and U.platformNo < ?3 ")
	public List<User> findByUnionIdAndPlatformNoBetween(Long unionId, Long start, Long end);
	
	public User findByPlatformAndRole(String platform,String role);
}