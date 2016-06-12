package com.snowstore.diana.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snowstore.diana.service.GroupRuleService;
import com.snowstore.diana.vo.GroupRuleVo;
import com.snowstore.diana.vo.PageFormVo;
import com.snowstore.diana.vo.PageTables;
import com.snowstore.diana.vo.Result;
import com.snowstore.log.annotation.UserLog;

@Controller
@RequestMapping("/groupRule")
public class GroupRuleController extends BaseController {

	@Autowired
	private GroupRuleService groupRuleService;

	@RequestMapping("/groupRuleList")
	@UserLog(remark = "规则-->返回规则列表页面")
	public String groupRuleListController() {
		return "groupRuleList";
	}

	@RequestMapping("/showAddOrUpdateGroupRule")
	@UserLog(remark = "规则-->根据ID获取每条规则")
	public String showAddOrUpdateGroupRuleController(Long id, Model model) throws Exception {
		GroupRuleVo groupRuleVo = new GroupRuleVo();
		model.addAttribute("action", null == id ? "添加" : "编辑");
		if (null != id) {
			groupRuleVo = this.groupRuleService.getGroupRuleVoService(id);
		}
		model.addAttribute("groupRuleVo", groupRuleVo);
		return "showAddOrUpdateGroupRule";
	}

	/**
	 * 分页显示组合产品规则列表
	 * 
	 * @author mingzhi.dong
	 * @date 2016年1月22日
	 * @param page
	 * @return
	 */
	@RequestMapping("/getPageGroupRule")
	@ResponseBody
	@UserLog(remark = "分页显示产品规则列表")
	public PageTables<GroupRuleVo> findPageGroupRuleController(Integer start, Integer length) throws Exception {
		PageFormVo pageFormVo = new PageFormVo();
		pageFormVo.setPage(start / 10 + 1);
		pageFormVo.setRows(length);
		pageFormVo.setStart(start);
		pageFormVo.setLength(length);
		pageFormVo.setSort("ticketNumber");
		pageFormVo.setOrder("ASC");
		return this.groupRuleService.findPageGroupRuleService(pageFormVo);
	}

	/**
	 * 添加或修改组合产品规则
	 * 
	 * @author mingzhi.dong
	 * @date 2016年1月22日
	 * @param groupRule
	 * @return
	 */
	@RequestMapping("/addOrUpdateGroupRule")
	@ResponseBody
	@UserLog(remark = "添加或修改产品规则")
	public Result<String> addOrUpdateGroupRuleController(GroupRuleVo groupRuleVo) {
		boolean action = this.groupRuleService.addOrUpdateGroupRuleService(groupRuleVo);
		if (action) {
			return new Result<String>(Result.Type.SUCCESS, "操作成功");
		} else {
			return new Result<String>(Result.Type.FAILURE, "操作失败");
		}
	}

	/**
	 * 删除组合产品规则
	 * 
	 * @author mingzhi.dong
	 * @date 2016年1月22日
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteGroupRule")
	@ResponseBody
	@UserLog(remark = "【按钮】规则-->删除规则")
	public Result<String> deleteGroupRuleController(Long id) {
		this.groupRuleService.deleteGroupRuleService(id);
		return new Result<String>(Result.Type.SUCCESS, "删除成功");
	}
}
