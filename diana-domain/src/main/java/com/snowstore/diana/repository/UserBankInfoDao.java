package com.snowstore.diana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.UserBankInfo;

public interface UserBankInfoDao extends PagingAndSortingRepository<UserBankInfo, Long>, JpaSpecificationExecutor<UserBankInfo> {
	/** 获取用户全部已认证银行卡 */
	@Query(" from UserBankInfo o where o.unionId=?1 and o.status=?2 order by lastModifiedDate desc")
	public List<UserBankInfo> findByUnionUser(Long unionId, String status);

	/** 根据流水号获取 */
	@Query(" from UserBankInfo o where o.authSerialNumber=?1")
	public UserBankInfo findByAuthSerialNumber(Long authSerialNumber);
}
