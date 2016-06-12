package com.snowstore.diana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.snowstore.diana.domain.GiftCode;

@Repository
public interface GiftCodeDao extends JpaSpecificationExecutor<GiftCode>, PagingAndSortingRepository<GiftCode, Long> {

	public List<GiftCode> findByOrderIdAndRedeemCodeType(Long orderId, String redeemCodeType);

	public List<GiftCode> findByRedeemCodeTypeAndStatus(String redeemCodeType, String status);

	public GiftCode findTop1ByRedeemCodeTypeAndStatus(String redeemCodeType, String status);

	public Long countByRedeemCodeTypeAndStatus(String redeemCodeType, String status);
}
