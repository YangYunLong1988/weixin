package com.snowstore.diana.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.common.Calendars;
import com.snowstore.diana.domain.Channel;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Statistics;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.domain.User.Role;
import com.snowstore.diana.repository.OrderDao;
import com.snowstore.diana.repository.StatisticsDao;
import com.snowstore.diana.utils.DateUtils;
import com.snowstore.diana.utils.ExcelUitl;
import com.snowstore.diana.vo.PageFormVo;
import com.snowstore.diana.vo.StatisticsVo;

@Service
@Transactional
public class StatisticsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsService.class);

	@Autowired
	private StatisticsDao statisticsDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ChannelService channelService;

	/**
	 *
	 * @param beginDate
	 * @param endDate
	 * @param channelId
	 * @param subChannelId
	 * @return
	 */
	public Page<StatisticsVo> findAll(final Date beginDate, final Date endDate, final Long channelId,final Long subChannelId, PageFormVo pageFormVo){
		final Date currDay = Calendars.parse(Calendars.getCurrentDate(), "yyyy-MM-dd");
		//渠道
		List<Channel> whereChannel = new ArrayList<Channel>();
		final Map<String,String> channelCodeMap = new HashMap<String,String>();
		if( null != subChannelId){
			whereChannel = channelService.getSubChannelByParentIdRecursively(subChannelId);
		}else if(null != channelId){
			whereChannel = channelService.getSubChannelByParentIdRecursively(channelId);
		}else{
			User user = customerService.getCurrentUser();
			if(Role.CHANNEL  == Role.valueOf(user.getRole())){   //如果是渠道用户，则根据渠道代码过滤数据
				Channel channel = channelService.getChannelByCode(user.getPlatform());
				whereChannel = channelService.getSubChannelByParentIdRecursively(channel.getId());
			}else{
				whereChannel = channelService.getAllChannel();
			}
		}

		if(!whereChannel.isEmpty()){
			for (Channel channel : whereChannel) {
				channelCodeMap.put(channel.getCode(),channel.getName());
			}
		}

		//获取统计信息
		Page<Statistics> statisticsPage = statisticsDao.findAll(new Specification<Statistics>() {
			@Override
			public Predicate toPredicate(Root<Statistics> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();

				//开始日期
				if (beginDate != null) {
					list.add(cb.greaterThanOrEqualTo(root.<Date>get("dateStatistics"), beginDate));
				}

				//结束日期
				if (endDate != null) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					calendar.add(Calendar.DATE, 1);
					calendar.add(Calendar.SECOND, -1);
					list.add(cb.lessThanOrEqualTo(root.<Date>get("dateStatistics"), calendar.getTime()));

				}

				//渠道
				Set<String> platforms = channelCodeMap.keySet();
				if (!platforms.isEmpty()) {
					list.add(cb.isTrue(root.<String>get("platform").in(platforms)));
				}

				list.add(cb.lessThan(root.<Date>get("dateStatistics"), currDay));

				Predicate[] predicate = new Predicate[list.size()];
				return cb.and(list.toArray(predicate));
			}
		}, pageFormVo);

		List<StatisticsVo> resultList = new ArrayList<StatisticsVo>();
		if(!statisticsPage.getContent().isEmpty()){
			for(Statistics statistics : statisticsPage.getContent()){
				StatisticsVo vo = new StatisticsVo();
				vo.setPlatform(channelCodeMap.get(statistics.getPlatform()));
				vo.setDateStatistics(statistics.getDateStatistics());
				vo.setAccessStatistics(statistics.getAccessStatistics()==null ? 0 : statistics.getAccessStatistics());
				vo.setOrderStatistics(statistics.getOrderStatistics()==null ? 0 : statistics.getOrderStatistics());
				vo.setTicketStatistics(statistics.getTicketStatistics()==null ? 0 : statistics.getTicketStatistics());
				vo.setTransactionsStatistics(statistics.getTransactionsStatistics()==null ? new BigDecimal("0") : statistics.getTransactionsStatistics());
				resultList.add(vo);
			}
		}
		Page<StatisticsVo> page = new PageImpl<StatisticsVo>(resultList, pageFormVo, statisticsPage.getTotalElements());
		return page;
	}

	/**
	 * 根据日期区间，查询所有统计信息
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @param channelId 渠道ID
	 * @param subChannelId 子渠道ID
	 * @author wulinjie
	 * @return 分页统计结果
	 */
	public Page<StatisticsVo> findAllByPage(final Date beginDate, final Date endDate, final Long channelId, final Long subChannelId, PageFormVo pageFormVo) {
		return findAll(beginDate, endDate, channelId, subChannelId, pageFormVo);
	}

	/**
	 * 根据日期区间，汇总统计信息的成交总额
	 * @author wulinjie
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public BigDecimal summaryStatistics(final Date beginDate, final Date endDate,final Long channelId,final Long subChannelId){
		
		final Date currDay = Calendars.parse(Calendars.getCurrentDate(), "yyyy-MM-dd");
		
		BigDecimal summary = new BigDecimal("0");
		//渠道
		List<Channel> whereChannel = new ArrayList<Channel>();
		final Map<String,String> channelCodeMap = new HashMap<String,String>();
		if( null != subChannelId){
			whereChannel = channelService.getSubChannelByParentIdRecursively(subChannelId);
		}else if(null != channelId){
			whereChannel = channelService.getSubChannelByParentIdRecursively(channelId);
		}else{
			User user = customerService.getCurrentUser();
			if(Role.CHANNEL  == Role.valueOf(user.getRole())){   //如果是渠道用户，则根据渠道代码过滤数据
				Channel channel = channelService.getChannelByCode(user.getPlatform());
				whereChannel = channelService.getSubChannelByParentIdRecursively(channel.getId());
			}else{
				whereChannel = channelService.getAllChannel();
			}
		}

		if(!whereChannel.isEmpty()){
			for (Channel channel : whereChannel) {
				channelCodeMap.put(channel.getCode(),channel.getName());
			}
		}
		
		//查询统计列表
		List<Statistics> statisticsLst = statisticsDao.findAll(new Specification<Statistics>() {

			@Override
			public Predicate toPredicate(Root<Statistics> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();

				if (beginDate != null) {
					list.add(cb.greaterThanOrEqualTo(root.<Date> get("dateStatistics"), beginDate));
				}

				if (endDate != null) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					calendar.add(Calendar.DATE, 1);
					calendar.add(Calendar.SECOND, -1);
					list.add(cb.lessThanOrEqualTo(root.<Date> get("dateStatistics"), calendar.getTime()));
					
				}
				
				list.add(cb.lessThan(root.<Date> get("dateStatistics"), currDay));
				
				list.add(cb.isTrue(root.<String>get("platform").in(channelCodeMap.keySet())));

				Predicate[] predicate = new Predicate[list.size()];
				return cb.and(list.toArray(predicate));
			}
		});
		
		//汇总成交金额
		if(statisticsLst!=null){
			for(Statistics statistics : statisticsLst){
				summary = summary.add(statistics.getTransactionsStatistics()==null? new BigDecimal("0") : statistics.getTransactionsStatistics());
			}
		}
		return summary;
	}
	
	/**
	 * 导出门户统计信息
	 * 
	 * @author wulinjie
	 * @param request
	 * @param response
	 */
	public void exportStatisitcs(final Date beginDate, final Date endDate, final Long channelId ,final Long subChannelId ,HttpServletRequest request, HttpServletResponse response, PageFormVo pageFormVo) {
		LOGGER.info("开始导出门户统计信息");
		try {
			//定义标题行
			String[] header = { "渠道名称", "日期", "访问人数", "新增订单", "累计出票", "成交金额(元)" };
			//封装数据
			Page<StatisticsVo> voPage = findAll(beginDate, endDate, channelId, subChannelId, pageFormVo);
			//导出到excel
			ExcelUitl.exportExcel("统计信息" + DateUtils.dateToString(new Date(), "yyMMdd") + ".xlsx", header, voPage.getContent(), request, response, "yyyy-MM-dd");
		} catch (Exception e) {
			LOGGER.error("统计信息导出异常!", e);
		}
		LOGGER.info("门户统计信息导出结束");
	}

	/**
	 * 每日更新统计信息
	 * 
	 * @author wulinjie
	 */
	public void updateEndDayStatistics() {
		LOGGER.info("开始更新当天统计信息");
		//当前时间
		Calendar calendar = Calendar.getInstance();
		calendar = org.apache.commons.lang3.time.DateUtils.truncate(calendar, Calendar.DATE);
		
		//统计区间
		Date endDate = calendar.getTime();		//结束日期
		calendar.add(Calendar.DATE, -1);
		Date beginDate = calendar.getTime();	//开始日期

		List<Channel> channelList = channelService.getAllChannel();
		LOGGER.info("渠道数量：" + channelList.size());
		if(!channelList.isEmpty()){
			for (Channel channel : channelList) {
				//初始化前一天的统计信息
				Long total = statisticsDao.countByDateStatisticsAndPlatform(beginDate, channel.getCode());
				if (total <= 1) {
					// 新建统计信息
					Statistics newStatistics = statisticsDao.findByDateStatisticsAndPlatform(beginDate, channel.getCode());
					if (newStatistics == null) {
						newStatistics = new Statistics();
						newStatistics.setPlatform(channel.getCode());
					}

					newStatistics.setDateStatistics(calendar.getTime()); // 统计日期

					// 当日新增订单数量
					Long totalOrder = orderDao.countByDatePlatformStatus(beginDate, endDate, channel.getCode(), Order.Status.已付款.name());
					newStatistics.setOrderStatistics(totalOrder==null?0:totalOrder);

					// 当日累计出票数量
					Long totalTicket = orderDao.sumTicketNumByDatePlatformStatus(beginDate, endDate, channel.getCode(), Order.Status.已付款.name());
					newStatistics.setTicketStatistics(totalTicket==null?0:totalTicket);

					// 当日成交总额
					BigDecimal totalMoney = orderDao.sumAmountByDatePlatformStatus(beginDate, endDate,channel.getCode(), Order.Status.已付款.name());
					newStatistics.setTransactionsStatistics(totalMoney == null ? new BigDecimal("0") : totalMoney);

					statisticsDao.save(newStatistics);
				}

				//初始化当天统计信息
				total = statisticsDao.countByDateStatisticsAndPlatform(endDate, channel.getCode());
				if(total <= 1){
					// 新建统计信息
					Statistics newStatistics = statisticsDao.findByDateStatisticsAndPlatform(endDate, channel.getCode());
					if (newStatistics == null) {
						newStatistics = new Statistics();
						newStatistics.setPlatform(channel.getCode());
						newStatistics.setDateStatistics(endDate); // 统计日期
						statisticsDao.save(newStatistics);
					}
				}
			}
		}
		LOGGER.info("当天统计信息更新结束");
	}

}
