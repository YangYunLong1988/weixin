package com.snowstore.diana.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.Coupon;
import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.domain.Tickets.Status;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.repository.TicketsDao;
import com.snowstore.diana.vo.PageFormVo;

@Service
@Transactional
public class TicketsService {
	
	@Autowired
	private TicketsDao ticketsDao;
	@Autowired
	private CouponService couponService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private UnionUserService unionUserService;
	
	/**
	 * 根据用户ID获取出票记录信息
	 * @return
	 */
	public Page<Tickets> findByUserId(Long id,PageFormVo form){
		return ticketsDao.findByUserId(id, form);
	}
	
	/**
	 * 根据用户ID获取出票记录信息List
	 * @return
	 */
	public List<Tickets> findByUserId(Long id){
		return ticketsDao.findByUserId(id);
	}
	
	/**
	 * 根据订单ID获取出票记录信息
	 * @param id
	 * @param form
	 * @return
	 */
	public Page<Tickets> findByOrderId(Long id, String status, PageFormVo form){
		return ticketsDao.findByOrderId(id, status, form);
	}
	
	/**
	 * 根据订单ID获取出票记录信息
	 * @param id
	 * @param form
	 * @return
	 */
	public List<Tickets> findByOrderId(Long id){
		return ticketsDao.findByOrderId(id);
	}
	
	/**
	* @Title: findTicketByOrderId
	* @Description: (根据订单ID查询票据信息)
	* @author wangyunhao  
	* @date 2015年10月21日 下午1:18:06
	* @return List<Tickets>    返回类型
	 */
	public List<Tickets> findTicketByOrderId(Long orderId) {
		Long unionId = unionUserService.findOrAdd(customerService.getUserDetails().getMobile());
		return ticketsDao.findByUnionIdAndRefOrder(unionId, orderId);
	}

	/**
	 * 根据火票订单标号，获取电影票
	 * @param orderCode
	 * @return
	 */
	public List<Tickets> findByOrderCode(String orderCode){
		return ticketsDao.findByOrderCode(orderCode);
	}

	/**
	 * 根据火票务订单号，统计电影票数量
	 * @author wulinjie
	 * @param orderCode
	 * @return
	 */
	public Integer countByOrderCode(String orderCode){
		return ticketsDao.countByOrderCode(orderCode);
	}

	/**
	 * 根据用户和订单ID获取用户出票记录信息
	 * @param userId
	 * @param orderId
	 * @param form
	 * @return
	 */
	public Page<Tickets> findTicketsByUser(Long userId, Long orderId,String status, PageFormVo form){
		return ticketsDao.findByOrderId(userId, orderId,status, form);
	}
	
	/**
	 * 根据卡券ID，获取相应的电影票
	 * @param couponId 卡券ID
	 * @return 电影票
	 */
	public Tickets findTicketsByCouponId(Long couponId){
		return ticketsDao.findByCouponId(couponId);
	}
	
	public Tickets get(Long id) {
		return ticketsDao.findOne(id);
	}
	
	public Tickets getUserTicketsById(Long id){
		Long unionId = unionUserService.findOrAdd(customerService.getUserDetails().getMobile());
		return ticketsDao.findByUnionIdAndId(unionId, id);
	}
	
	public Tickets saveOrUpdate(Tickets tickets){
		return ticketsDao.save(tickets);
	}

	public Iterable<Tickets> saveOrUpdate(List<Tickets> ticketsList){
		return ticketsDao.save(ticketsList);
	}

	public List<Tickets> findByIds(List<Long> ids){
		return ticketsDao.findByIdIn(ids);
	}

	/**
	 * 将电影票与卡券绑定，并返回绑定的所有卡券
	 * @author wulinjie
	 * @param ticketsList	电影票
	 * @return 卡券
	 */
	public List<String> assignCoupon(List<Tickets> ticketsList) throws Exception {
		User user;
		List<String> huoCoupons = new ArrayList<String>();
		if(ticketsList!=null){
			for(Tickets tickets : ticketsList){
				user = customerService.findOne(tickets.getUserId());

				Coupon coupon = couponService.getUnusedCouponCode(tickets);	//获取可用的卡券
				coupon.setIsBind(1);											//已绑定
				coupon.setIsUsed(1);											//已使用
				coupon.setUserId(user.getId());									//绑定用户ID
				coupon.setUnionId(unionUserService.findOrAdd(user.getMobile()));
				couponService.saveOrUpdate(coupon);							//绑定卡券

				tickets.setCouponId(coupon.getId());
				saveOrUpdate(tickets);

				huoCoupons.add(coupon.getCode());
			}
		}
		return huoCoupons;
	}
	
	/**
	 * 查询已出票但未同步座位信息的电影票列表
	 * @author wulinjie
	 * @return
	 */
	public List<Tickets> findIssueTicketsList(){
		return ticketsDao.findIssueTicketsList();
	}

	/**
	 * 获取未兑换的电影票
	 * @param start	开始日期
	 * @param end		结束日期
	 * @return
	 */
	public List<Tickets> findUnusedTicketsList(Date start, Date end){
		return ticketsDao.findTicketsByStatusAndDate(Status.未兑换.name(), start, end);
	}
}
