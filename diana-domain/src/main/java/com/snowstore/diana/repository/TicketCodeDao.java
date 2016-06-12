package com.snowstore.diana.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.TicketCode;

public interface TicketCodeDao extends PagingAndSortingRepository<TicketCode, Long>, JpaSpecificationExecutor<TicketCode> {

	/**
	 * 根据渠道类型和状态查询
	 * 
	 * @author mingzhi.dong
	 * @date 2016年2月26日
	 * @param channelType
	 *            渠道类型
	 * @param status
	 *            状态
	 * @param pageable
	 * @return
	 */
	public List<TicketCode> findByChannelTypeAndStatus(String channelType, String status, Pageable pageable);

	/**
	 * 根据票id查询
	 * 
	 * @author mingzhi.dong
	 * @date 2016年2月27日
	 * @param ticketsId
	 * @return
	 */
	public TicketCode findByTicketsId(Long ticketsId);

}
