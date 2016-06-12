package com.snowstore.diana.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.snowstore.diana.result.GiftResult;
import com.snowstore.diana.utils.DateUtils;
import com.snowstore.diana.utils.ExcelUitl;
import com.snowstore.diana.vo.PageFormVo;
import com.snowstore.diana.vo.PageTables;

@Service
public class GiftService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GiftService.class);
	@PersistenceContext
	private EntityManager entityManager;

	public PageTables<GiftResult> findGiftList(PageFormVo form, GiftResult queryGift) {
		PageTables<GiftResult> pageTables = new PageTables<GiftResult>();
		List<Object> paramValue = new ArrayList<Object>();
		StringBuffer paramSql = new StringBuffer();
		// 封装查询条件
		buildParams(queryGift, paramValue, paramSql);
		queryCount(pageTables, paramValue, paramSql);
		queryData(form, pageTables, paramValue, paramSql);
		return pageTables;
	}

	/**
	 * 封装查询条件
	 * 
	 * @author XieZG
	 * @Date:2016年1月8日上午10:23:18
	 * @param queryGift
	 * @param paramValue
	 * @param paramSql
	 */
	private void buildParams(GiftResult queryGift, List<Object> paramValue, StringBuffer paramSql) {
		if (StringUtils.isNotEmpty(queryGift.getMobile())) {
			paramSql.append("and u.mobile=? ");
			paramValue.add(queryGift.getMobile());
		}
		if (queryGift.getId() != null) {
			paramSql.append("and o.id=? ");
			paramValue.add(queryGift.getId());
		}
		if (queryGift.getBeginDate() != null) {
			paramSql.append("and ug.created_date>? ");
			paramValue.add(queryGift.getBeginDate());
		}
		if (queryGift.getEndDate() != null) {
			paramSql.append("and ug.created_date<? ");
			paramValue.add(queryGift.getEndDate());
		}
	}

	/**
	 * 查询数据 tips:form参数为null时不分页
	 * 
	 * @author XieZG
	 * @Date:2016年1月8日上午10:23:31
	 * @param form
	 * @param pageTables
	 * @param paramValue
	 * @param paramSql
	 */
	@SuppressWarnings("unchecked")
	private void queryData(PageFormVo form, PageTables<GiftResult> pageTables, List<Object> paramValue, StringBuffer paramSql) {
		Query query;
		String querySql = "select u.mobile,ug.id,ug.order_id,o.product_name,o.reference_order,o.created_date,ug.created_date as gift_created_date,ug.gift_name,p.recipients,p.mobile as recipients_mobile,p.province, p.city, p.area, p.address "
				+ "from  diana_user_gift ug left join diana_package p on p.id=ug.ref_package  left join diana_order o on ug.order_id = o.id left join diana_user u on ug.user_id=u.id "
				+ "where p.order_id = o.id and ug.ref_package = p.id and o.status='已付款' ";
		querySql = querySql.toString() + paramSql.toString() + " order by o.id desc";
		query = entityManager.createNativeQuery(querySql, GiftResult.class);
		for (int i = 0; i < paramValue.size(); i++) {
			query.setParameter(i + 1, paramValue.get(i));
		}
		if (form != null) {
			query.setFirstResult((form.getPage() - 1) * form.getRows());
			query.setMaxResults(form.getRows());
		}
		pageTables.setData(query.getResultList());
	}

	private void queryCount(PageTables<GiftResult> pageTables, List<Object> paramValue, StringBuffer paramSql) {
		String queryCountSql = "select count(1) " + "from  diana_user_gift ug left join diana_package p on p.id=ug.ref_package  left join diana_order o on ug.order_id = o.id left join diana_user u on ug.user_id=u.id  "
				+ "where p.order_id = o.id and ug.ref_package = p.id and o.status='已付款' ";
		Query query = entityManager.createNativeQuery(queryCountSql + paramSql.toString());
		for (int i = 0; i < paramValue.size(); i++) {
			query.setParameter(i + 1, paramValue.get(i));
		}
		BigDecimal totalElements = (BigDecimal) query.getSingleResult();
		pageTables.setRecordsTotal(totalElements.longValue());
		pageTables.setRecordsFiltered(totalElements.longValue());
	}

	/**
	 * 导出订单数据
	 * 
	 * @param request
	 * @param response
	 * @param order
	 *            查询条件
	 * @return
	 * @throws Exception
	 */
	public void exportGift(HttpServletRequest request, HttpServletResponse response, GiftResult queryGift) {
		try {
			Map<String,String> headers = new LinkedHashMap<String,String>();
			headers.put("mobile", "手机号");
			headers.put("id", "礼品编号");
			headers.put("orderId", "订单编号");
			headers.put("productName", "购买产品");
			headers.put("giftCreatedDate", "礼品提交时间");
			headers.put("createdDate", "订单提交时间");
			headers.put("receiveGiftNum", "领取礼品数量");
			headers.put("giftName", "礼品类型");
			headers.put("giftNum", "礼品数量");
			headers.put("recipients", "取件人");
			headers.put("recipientsMobile", "取件人手机");
			headers.put("address", "配送地址");
			
			PageTables<GiftResult> pageTables = new PageTables<GiftResult>();
			List<Object> paramValue = new ArrayList<Object>();
			StringBuffer paramSql = new StringBuffer();
			// 封装查询条件
			buildParams(queryGift, paramValue, paramSql);
			queryData(null, pageTables, paramValue, paramSql);
			ExcelUitl.exportExcel("礼品信息" + DateUtils.dateToString(new Date(), "yyMMdd") + ".xlsx", headers, pageTables.getData(), request, response, "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			LOGGER.error("订单导出异常 ", e);
		}

	}
}
