package com.snowstore.diana.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.Tickets;

public interface TicketsDao extends PagingAndSortingRepository<Tickets, Long>, JpaSpecificationExecutor<Tickets> {

	/**根据用户的查询出票信息*/
	@Query("from Tickets t where t.userId=?1")
	public Page<Tickets> findByUserId(Long id, Pageable page);
	
	@Query("from Tickets t where t.userId=?1")
	public List<Tickets> findByUserId(Long orderId);
	
	/**根据订单查询出票信息*/
	@Query("from Tickets t where t.refOrder=?1 and status=?2")
	public Page<Tickets> findByOrderId(Long id, String status,Pageable page);
	
	/**根据订单查询出票信息*/
	@Query("from Tickets t where t.refOrder=?1")
	public List<Tickets> findByOrderId(Long id);

	@Query("from Tickets T where T.refOrder = ?1")
	public List<Tickets> findTicketByOrderId(Long orderId);
	
	/**查询用户的出票记录信息*/
	@Query("from Tickets t where t.userId=?1 and t.refOrder=?2 and status=?3")
	public Page<Tickets> findByOrderId(Long userId, Long orderId, String status, Pageable page);

	/**
	 * 根据火票订单编号，获取电影票
	 * @param orderCode
	 * @return
	 */
	public List<Tickets> findByOrderCode(String orderCode);

	/**
	 * 根据火票订单编号，统计电影票数量
	 * @param orderCode
	 * @return
	 */
	public int countByOrderCode(String orderCode);

	/**
	 * 根据电影票ID，获取电影票列表
	 * @param ids 电影票ID
	 * @return
	 */
	public List<Tickets> findByIdIn(List<Long> ids);
	
	/**
	 * 根据卡券ID，获取相应的电影票
	 * @param couponId 卡券ID
	 * @return 电影票
	 */
	public Tickets findByCouponId(Long couponId);
	
	/**
	 * 查询已出票但未同步座位信息的电影票
	 * @author wulinjie
	 * @return
	 */
	@Query("from Tickets M where M.orderCode is not null and (M.seat is null or M.performTime is null )")
	public List<Tickets> findIssueTicketsList();

	@Query("from Tickets M where M.status = ?1 and M.createdDate >= ?2 and M.createdDate < ?3")
	public List<Tickets> findTicketsByStatusAndDate(String status, Date start,Date end);
	
	public List<Tickets> findByUnionIdAndRefOrder(Long unionId,Long orderId);
	
	public Tickets findByUnionIdAndId(Long unionId,Long id);
}