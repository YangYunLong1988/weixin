package com.snowstore.diana.controller;

import java.util.Date;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.snowstore.diana.editor.DateEditor;
import com.snowstore.diana.vo.PageFormVo;

public abstract class BaseController {

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateEditor(true));
	}

	/**
	 * 设置基本查询条件
	 * 
	 * @param pageFormVo
	 */
	public void setBasicQueryCondition(PageFormVo pageFormVo) {
		pageFormVo.setPage(pageFormVo.getStart() == 0 ? 1 : pageFormVo.getStart() / pageFormVo.getLength() + 1);
		pageFormVo.setRows(pageFormVo.getLength());
	}
}
