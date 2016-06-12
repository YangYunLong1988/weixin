/**  
 * @Title: UserGiftDao.java
 * @Package com.snowstore.diana.repository
 * @Description: (用户礼品)
 * @author wangyunhao  
 * @date 2015年10月22日 下午5:15:15
 * @version V1.0  
 */
package com.snowstore.diana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.UserGift;

/**
 * @ClassName: UserGiftDao
 * @Description: (用户礼品)
 * @author wangyunhao
 * @date 2015年10月22日 下午5:15:15
 */
public interface UserGiftDao extends PagingAndSortingRepository<UserGift, Long>, JpaSpecificationExecutor<UserGift> {
	/** 根据用户和订单ID查询用户礼品记录 */
	@Query("from UserGift u where u.userId = ?1 and u.order.id = ?2")
	public List<UserGift> findByUserGift(Long userId, Long orderId);

	/** 根据订单ID查询用户礼品记录 */
	@Query("from UserGift u where u.order.id = ?1")
	public List<UserGift> findByOrderId(Long orderId);

	public List<UserGift> findByUserId(Long userId);

	@Query("from UserGift ug where ug.bestCakeCode is null and ug.refGift='mm14' and ug.order.status ='已付款'")
	public List<UserGift> findBestCakeGift();

	@Query("from UserGift ug where ug.refGift = 'mm57' and ug.status ='未分配' and ug.order.status ='已付款'")
	public List<UserGift> findRedeenUserGrift();

	@Query("from UserGift ug where ug.userId = ?1 and ug.order.id = ?2 and ug.refGift=?3 and ug.order.status ='已付款'")
	public UserGift findRedeenUserGiftCode(Long userId, Long orderId, String refGift);
}
