package com.snowstore.diana.service;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.snowstore.diana.common.Calendars;
import com.snowstore.diana.domain.Coupon;
import com.snowstore.diana.domain.CouponGroup;
import com.snowstore.diana.domain.Product;
import com.snowstore.diana.domain.Tickets;
import com.snowstore.diana.exception.CouponNotFoundException;
import com.snowstore.diana.repository.CouponDao;
import com.snowstore.diana.repository.CouponGroupDao;
import com.snowstore.diana.service.thirdapp.HuoClient;

/**
 * Created by wulinjie on 2015/11/10.
 */
@Service
public class CouponService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CouponService.class);

	@Autowired
	private ProductService productService;
	@Autowired
	private CouponGroupDao couponGroupDao;
	@Autowired
	private CouponDao couponDao;

	/**
	 * 获取卡券
	 * @author wulinjie
	 * @param tickets 电影票兑换码
	 * @return 卡券号
	 */
	@Transactional(readOnly=true)
	public Coupon getUnusedCouponCode(Tickets tickets) throws CouponNotFoundException{
		Coupon coupon;
		if(null != tickets.getCouponId()){
			coupon = couponDao.findOne(tickets.getCouponId());
			return coupon;
		}

		coupon = couponDao.findUnusedCoupon();	//获取有效的卡券
		if(null == coupon){
			throw new CouponNotFoundException("没有可用的卡券");
		}
		return coupon;

		/*
		if(group!=null){
			coupon = couponDao.findAvaliableCouponByGroupId(group.getId());//获取有效的卡券
			if(coupon == null){
				//该组没有可用的卡券时，则更新该组状态为0（不可用）
				//group.setStatus(0);
				//couponGroupDao.save(group);
				//创建新的卡券组
				Long groupId = getProxy().createCouponGroup(productId);
				coupon = couponDao.findAvaliableCouponByGroupId(groupId);		//获取有效的卡券(未绑定用户的)
			}
		}else{
			//新的新的卡券组
			Long groupId = getProxy().createCouponGroup(productId);
			coupon = couponDao.findAvaliableCouponByGroupId(groupId);		//获取有效的卡券(未绑定用户的)
		}
		
		if(coupon==null){
			throw new RuntimeException("没有可用的卡券");
		}
		*/
	}

	/**
	 *创建卡券组
	 * @author wulinjie
	 * @return 卡券组ID
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Long createCouponGroup(Long productId){
		LOGGER.info("开始生成卡券组");
		Product product = productService.get(productId);
		String secretKey = System.getProperty("caimiao.huo.rule");	//获取活动编码

		//创建卡券组
		CouponGroup group = new CouponGroup();
		group.setGroupName(getCouponGroupName(product.getName()));
		group.setTypeCode("103");
		//TODO 无法确定卡券金额和数量 暂时写死
		group.setTotal(200);					//卡券数量
		group.setAmout(new BigDecimal("50"));	//卡券金额
		group.setSecretKey(secretKey);			//活动编码
		group.setEnablePwd(1);					//是否加密
		//TODO 无法确定是否集团投资
		group.setIsGroupMovie(1);				//是否集团投资
		group.setNote(" ");						//备注
		JSONObject root = JSON.parseObject("{}");
		try{
			root = HuoClient.createCouponGroup(group);
		}catch (IOException e){
			LOGGER.error("生成卡券组异常[productId：" + productId + "]", e);
		}

		String msg = root.getString("msg");
		if("ok".equals(msg)){
			JSONObject data = root.getJSONObject("data");
			group.setTotal(data.getLong("total"));		//卡券数量
			group.setId(data.getLong("groupId"));		//卡券组ID
			couponGroupDao.save(group);
			createCoupon(group.getId());				//开始生成卡券
		}
		LOGGER.info("生成卡券组完成");
		return group.getId();
	}

	/**
	 * 根据卡券组ID查询卡券
	 * @author wulinjie
	 * @param groupId 卡券组ID
	 */
	@Transactional
	public void createCoupon(Long groupId){
		LOGGER.info("开始生成卡券,卡券组ID : " + groupId);
		CouponGroup group = couponGroupDao.findOne(groupId);
		int count = 0;
		if( group!=null ){
			JSONObject root = JSON.parseObject("{}");
			try{
				root = HuoClient.queryByGroup(groupId.toString());	//根据卡券组ID，查询卡券列表
			}catch (IOException e){
				LOGGER.error("查询卡券异常[groupId：" + groupId + "]", e);
			}
			int code = root.getInteger("code");
			if (code == 0) {
				JSONObject data = root.getJSONObject("data");
				JSONArray list = data.getJSONArray("list");
				for(count = 0;count < list.size(); count++){
					JSONObject item = list.getJSONObject(count);
					Coupon coupon = new Coupon();
					coupon.setCouponId(item.getLong("id"));
					coupon.setGroupId(item.getLong("group_id"));
					coupon.setTypeCode(item.getString("type_code"));
					coupon.setCode(item.getString("code"));
					coupon.setPassword(item.getString("password"));
					coupon.setAmount(new BigDecimal(item.getString("amount")));
					String startDate = item.getString("start_date");
					String endDate = item.getString("end_date");
					Object orderDate = item.get("order_data");
					coupon.setBeginDate(Calendars.StringToDate(startDate, "yyyymmdd"));
					coupon.setEndDate(Calendars.StringToDate(endDate, "yyyymmdd"));
					//coupon.setUserId(item.getLong("uid"));
					//coupon.setUserName(item.getString("username"));
					coupon.setOrderCode(item.getString("order_code"));
					if (orderDate!=null) {
						coupon.setOrderDate(Calendars.StringToDate(orderDate.toString(),"yyyymmdd"));
					}
					coupon.setIsBind(item.getInteger("is_bind"));
					coupon.setIsUsed(item.getInteger("is_use"));
					coupon.setStatus(item.getInteger("status"));
					couponDao.save(coupon);
				}

				JSONObject groupJson = data.getJSONObject("group");
				group.setAmout(new BigDecimal(groupJson.getInteger("total") * groupJson.getDouble("amount")));
				group.setStatus(groupJson.getInteger("status"));
				couponGroupDao.save(group);
			}
			LOGGER.info("生成卡券完成,卡券数量" + count);
		}
	}

	/**
	 * 生成卡券组名称
	 * @param productName 产品名称
	 * @author wulinjie
	 * @return 卡券组名称
	 */
	private String getCouponGroupName(String productName){
		return productName + "_" + Calendars.format("yyyyMMddHHmmss",new Date());
	}

	/**
	 * 查询卡券
	 * @author wulinjie
	 * @param id 卡券ID
	 * @return
	 */
	@Transactional
	public Coupon getCouponById(Long id){
		return couponDao.findOne(id);
	}

	/**
	 * 查询卡券
	 * @author wulinjie
	 * @param code 卡券ID
	 * @return
	 */
	@Transactional
	public Coupon getCouponByCode(String code){
		return couponDao.findByCode(code);
	}

	/**
	 * 保存卡券信息
	 * @author wulinjie
	 */
	@Transactional
	public void saveOrUpdate(Coupon coupon){
		couponDao.save(coupon);
	}

	/**
	 * 获取卡用的卡券数量
	 * @author wulinjie
	 * @return 卡用的卡券数量
	 */
	public Long countUnusedCouponCount(){
		return couponDao.countAvaliableCoupon();
	}
}
