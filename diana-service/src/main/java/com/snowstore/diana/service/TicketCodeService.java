package com.snowstore.diana.service;

import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.TicketCode;
import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.repository.OrderDao;
import com.snowstore.diana.repository.TicketCodeDao;
import com.snowstore.diana.repository.TicketsDao;
import com.snowstore.diana.vo.Result;

@Service
@Transactional
public class TicketCodeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TicketCodeService.class);
	@Autowired
	private TicketCodeDao ticketCodeDao;
	@Autowired
	private TicketsDao ticketsDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private TicketsService ticketsService;

	/**
	 * 获取兑换码
	 * 
	 * @author mingzhi.dong
	 * @date 2016年2月26日
	 * @param ticketListId
	 *            被兑换票id
	 * @param channelType
	 *            兑换渠道
	 * @param orderId
	 *            订单id
	 * @return
	 */
	public Result<String> getCodeToTicketCodeService(List<Long> ticketListId, String channelType, Long orderId) {
		Result<String> result = null;
		Order order = orderDao.findOne(orderId);
		if (order.getStatus().equals(Order.Status.已付款.name())) {// 已付款
			PageRequest page = new PageRequest(0, ticketListId.size());
			List<TicketCode> ticketCodeList = ticketCodeDao.findByChannelTypeAndStatus(channelType, TicketCode.Status.未分配.name(), page);
			if (ticketListId.size() <= ticketCodeList.size()) {
				result = new Result<String>(Result.Type.SUCCESS);
				for (int i = 0; i < ticketListId.size(); i++) {
					Tickets tickets = ticketsDao.findOne(ticketListId.get(i));
					TicketCode existTc = ticketCodeDao.findByTicketsId(ticketListId.get(i));
					if (null == existTc) {
						TicketCode tc = ticketCodeList.get(i);
						tickets.setStatus(Tickets.Status.已兑换.name());
						tickets.setAgency(channelType);
						ticketsDao.save(tickets);
						tc.setTickets(tickets);
						tc.setStatus(TicketCode.Status.已分配.name());
						ticketCodeDao.save(tc);
						result.addMessage(tc.getTicketCode());
					}
				}
			} else {
				LOGGER.info(channelType + "渠道兑换取票码数量不足,可用卡券数量：" + ticketCodeList.size());
				result = new Result<String>(Result.Type.WARNING, "叶问三异常火爆，大众点评码已经抢空，积极补货中……建议直接选座。");
			}
		} else if (order.getStatus().equals(Order.Status.待付款.name())) {// 待付款
			for (int i = 0; i < ticketListId.size(); i++) {
				Tickets tickets = ticketsDao.findOne(ticketListId.get(i));
				tickets.setStatus(Tickets.Status.已兑换.name());
				tickets.setAgency(channelType);
				ticketsDao.save(tickets);
			}
			result = new Result<String>(Result.Type.SUCCESS, "支付后显示兑换码。");
		} else {// 已撤单
			result = new Result<String>(Result.Type.WARNING, "订单已取消。");
		}
		return result;
	}

	/**
	 * 支付成功后修已兑换票并生成兑换码
	 * 
	 * @author mingzhi.dong
	 * @date 2016年2月27日
	 * @param orderId
	 */
	@Async
	public void asyncUpdateTicketCodeService(Long orderId) {
		List<Tickets> tickets = ticketsService.findTicketByOrderId(orderId);
		if (null != tickets && tickets.size() > 0) {
			for (Tickets ts : tickets) {
				if (EnumUtils.isValidEnum(TicketCode.ChannelType.class, ts.getAgency())) {// 验证是否是通过兑换渠道
					PageRequest page = new PageRequest(0, 1);
					List<TicketCode> ticketCodeList = ticketCodeDao.findByChannelTypeAndStatus(ts.getAgency(), TicketCode.Status.未分配.name(), page);
					TicketCode existTc = ticketCodeDao.findByTicketsId(ts.getId());
					if (null == existTc && ticketCodeList != null && ticketCodeList.size() > 0) {
						TicketCode tc = ticketCodeList.get(0);
						tc.setTickets(ts);
						tc.setStatus(TicketCode.Status.已分配.name());
						ticketCodeDao.save(tc);
					}
				}
			}
		}
	}

	/**
	 * 根据票获取兑换码
	 * 
	 * @author mingzhi.dong
	 * @date 2016年2月27日
	 * @param tickets
	 */
	public void updateTicketCodeByTicketService(Tickets tickets) {
		if (EnumUtils.isValidEnum(TicketCode.ChannelType.class, tickets.getAgency())) {// 验证是否是通过兑换渠道
			PageRequest page = new PageRequest(0, 1);
			List<TicketCode> ticketCodeList = ticketCodeDao.findByChannelTypeAndStatus(tickets.getAgency(), TicketCode.Status.未分配.name(), page);
			TicketCode existTc = ticketCodeDao.findByTicketsId(tickets.getId());
			if (null == existTc && ticketCodeList != null && ticketCodeList.size() > 0) {
				TicketCode tc = ticketCodeList.get(0);
				tc.setTickets(tickets);
				tc.setStatus(TicketCode.Status.已分配.name());
				ticketCodeDao.save(tc);
			} else {
				LOGGER.info(tickets.getAgency() + "渠道兑换取票码数量不足,可用卡券数量：" + ticketCodeList.size());
			}
		}

	}

	/**
	 * 根据票id查询
	 * 
	 * @author mingzhi.dong
	 * @date 2016年2月27日
	 * @param ticketsId
	 * @return
	 */
	public TicketCode getTicketCodeService(Long ticketsId) {
		TicketCode tc = this.ticketCodeDao.findByTicketsId(ticketsId);
		return tc;
	}
}
