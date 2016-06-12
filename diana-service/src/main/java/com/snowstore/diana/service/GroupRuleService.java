package com.snowstore.diana.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.GroupRule;
import com.snowstore.diana.repository.GroupRuleDao;
import com.snowstore.diana.vo.GroupRuleVo;
import com.snowstore.diana.vo.PageTables;

@Service
@Transactional
public class GroupRuleService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GroupRuleService.class);
	@Autowired
	private GroupRuleDao groupRuleDao;

	/***
	 * 分页显示组合产品规则列表
	 * 
	 * @author mingzhi.dong
	 * @date 2016年1月22日
	 * @param pageable
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public PageTables<GroupRuleVo> findPageGroupRuleService(Pageable pageable) throws Exception {
		PageTables<GroupRuleVo> pageTables = new PageTables<GroupRuleVo>();
		Page<GroupRule> pageGroupRule = groupRuleDao.findAll(pageable);
		List<GroupRuleVo> listGroupRuleVo = new ArrayList<GroupRuleVo>();
		for (GroupRule gr : pageGroupRule) {
			GroupRuleVo grvo = new GroupRuleVo();
			BeanUtils.copyProperties(grvo, gr);
			listGroupRuleVo.add(grvo);
		}
		pageTables.setData(listGroupRuleVo);
		pageTables.setRecordsTotal(pageGroupRule.getTotalElements());
		pageTables.setRecordsFiltered(pageGroupRule.getTotalElements());
		return pageTables;
	}

	/**
	 * 添加或修改组合产品规则
	 * 
	 * @author mingzhi.dong
	 * @date 2016年1月22日
	 * @param groupRule
	 * @return
	 */
	public boolean addOrUpdateGroupRuleService(GroupRuleVo groupRuleVo) {
		boolean action = false;
		try {
			Long id = groupRuleVo.getId();
			GroupRule groupRule = null;
			if (null == id || id == 0) {
				groupRule = new GroupRule();
				BeanUtils.copyProperties(groupRule, groupRuleVo);
			} else {
				groupRule = this.groupRuleDao.findOne(id);
				BeanUtils.copyProperties(groupRule, groupRuleVo);
			}
			groupRuleDao.save(groupRule);
			action = true;
		} catch (Exception e) {
			LOGGER.error("保存或修改组合产品规则出错:" + e);
		}
		return action;
	}

	/**
	 * 根据id查询产品规则
	 * 
	 * @author mingzhi.dong
	 * @date 2016年1月25日
	 * @param id
	 * @return
	 */
	public GroupRuleVo getGroupRuleVoService(Long id) throws Exception {
		GroupRuleVo groupRuleVo = null;
		GroupRule groupRule = this.groupRuleDao.findOne(id);
		if (null != groupRule) {
			groupRuleVo = new GroupRuleVo();
			BeanUtils.copyProperties(groupRuleVo, groupRule);
		}
		return groupRuleVo;
	}

	/**
	 * 删除组合产品规则
	 * 
	 * @author mingzhi.dong
	 * @date 2016年1月22日
	 * @param id
	 * @return
	 */
	public void deleteGroupRuleService(Long id) {
		groupRuleDao.delete(id);
	}
	
	/**获取规则最大价格
	 * @return
	 */
	public BigDecimal getMaxPrice(){
		return groupRuleDao.getMaxPrice();
	}

}
