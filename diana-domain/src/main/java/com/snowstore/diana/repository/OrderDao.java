package com.snowstore.diana.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.User;

public interface OrderDao extends PagingAndSortingRepository<Order, Long>, JpaSpecificationExecutor<Order> {

	/**
	 * 根据创建日期、用户平台、订单状态统计订单数量
	 * 
	 * @author wulinjie
	 * @param beginDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param platform
	 *            用户平台
	 * @param status
	 *            订单状态
	 * @return 订单数量
	 */
	@Query("select count(M.id) from Order M where M.createdDate >= ?1 and M.createdDate < ?2 and M.user.platform=?3 and M.status=?4")
	public Long countByDatePlatformStatus(Date beginDate, Date endDate, String platform, String status);

	/**
	 * 根据创建日期、用户平台、订单状态统计出票数量
	 * 
	 * @author wulinjie
	 * @param beginDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param platform
	 *            用户平台
	 * @param status
	 *            订单状态
	 * @return 出票数量
	 */
	@Query("select sum(M.ticketNum) from Order M where M.createdDate >= ?1 and M.createdDate < ?2 and M.user.platform=?3 and M.status=?4")
	public Long sumTicketNumByDatePlatformStatus(Date beginDate, Date endDate, String platform, String status);

	/**
	 * 根据创建日期、用户平台、订单状态统计成交总额
	 * 
	 * @author wulinjie
	 * @param beginDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param platform
	 *            用户平台
	 * @param status
	 *            订单状态
	 * @return 成交总额
	 */
	@Query("select sum(M.amount) from Order M where M.createdDate >= ?1 and M.createdDate < ?2 and M.user.platform=?3 and M.status=?4")
	public BigDecimal sumAmountByDatePlatformStatus(Date beginDate, Date endDate, String platform, String status);

	/** 统计用户的下单购买次数 */
	@Query("select count(o.id) from Order o where o.user=?1 and o.status=?2")
	public Long findByUserOrderCount(User user, String status);

	/** 统计用户消费总金额 */
	@Query("select sum(o.amount) from Order o where o.user=?1 and o.status=?2")
	public BigDecimal findByUserOrderTotalAmount(User user, String status);

	/** 获取用户全部订单(分页) */
	@Query(" from Order o where o.user=?1 and o.type=?2")
	public Page<Order> findByUserOrder(User user, String type, Pageable page);

	/** 获取用户全部订单(分页) */
	@Query(" from Order o where o.user=?1 and o.type!='电影转礼品'")
	public Page<Order> findByUserOrder(User user, Pageable page);

	/** 获取用户全部订单(不分页) */
	@Query(" from Order o where o.user=?1")
	public List<Order> findByUserOrder(User user);

	@Query("from Order O where O.user = ?1 and O.status=?2 order by O.id desc")
	public List<Order> findOrderListByUserId(User user, String status);

	public List<Order> findByUserAndStatusInOrderByCreatedDateDesc(User user, List<String> status);

	@Query("from Order O where O.user.id in ?1 and O.status in ?2 order by O.createdDate desc")
	public List<Order> findByUserInAndStatusInOrderByCreatedDateDesc(List<Long> userIdList, List<String> status);

	@Query("from Order O where O.unionId = ?1 and O.status in ?2 order by O.createdDate desc")
	public List<Order> findByUnionIdAndStatusInOrderByCreatedDateDesc(Long unionId, List<String> status);

	public Order findByReferenceOrder(String referenceOrder);

	/** 统计用户购买成功的订单数量 */
	@Query("select count(o.id) from Order o where o.user = ?1 and o.status=?2")
	public Integer countByUserAndStatus(User user, String status);

	@Query("from Order o where o.user = ?1 and o.status=?2")
	public List<Order> findByUserAndStatus(User user, String status);

	/** 统计用户购买成功的有礼品的订单数量 */
	@Query("select count(o.id) from Order o where o.user = ?1 and o.status=?2 and o.gitfNum>?3")
	public Integer findByUserAndStatusAndGift(User user, String status, Integer gitfNum);

	/** 查询支付成功未通知开放平台的订单 */
	@Query("select o.id from Order o where o.notify = ?1 and status=?2 and o.referenceOrder is not null order by o.id desc")
	public List<Long> findByNotify(String notify, String status);

	/** 根据订单状态查询订单信息 */
	@Query("from Order o where o.status = ?1 order by o.id desc")
	public List<Order> findByStatus(String status);

	/**
	 * 根据用户和类型查找
	 * 
	 * @param user
	 * @param type
	 * @return
	 */
	public List<Order> findByUserAndTypeAndStatus(User user, String type, String status);

	/**
	 * 查找付款成功订单未同步
	 * 
	 * @param status
	 * @return
	 */
	@Query("select o.id from Order o where o.referenceOrder is null and o.status=?1 and o.type <> ?2")
	public List<Long> findIdByStatusAndReferenceOrderIsNullAndTypeNot(String status, String type);

	/** 查询未发货的订单 */
	@Query("select o from Order o join o.userGift g left join o.logistics l  where  l=null and o.status='已付款' and o.lastModifiedDate <?1")
	public Page<Order> findUndeliveredOrders(Date lastModifiedDate, Pageable pageable);

	public List<Order> findTop100ByNoxNotifyAndStatusAndTypeNot(String noxNotify, String status, String type, Sort sort);

	public Order findByUnionIdAndId(Long userId, Long id);

	/**
	 * 查询出子订单(电影票兑换礼品)
	 * 
	 * @author mingzhi.dong
	 * @date 2016年3月31日
	 * @param orderId
	 * @return
	 */
	@Query("select o from Order o left join o.giftTickets t where o.id=t.subOrder and t.refOrder=?1 and t.subOrder is not null")
	public List<Order> findBySubOrder(Long orderId);
}
