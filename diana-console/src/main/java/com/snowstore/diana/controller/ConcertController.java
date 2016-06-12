package com.snowstore.diana.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snowstore.diana.domain.Concert;
import com.snowstore.diana.service.ConcertService;
import com.snowstore.diana.vo.ConcertQueryVo;
import com.snowstore.diana.vo.ConcertVo;
import com.snowstore.diana.vo.PageTables;
import com.snowstore.diana.vo.Result;

/**
 * Concert Controller
 * 
 * @author wujinsong
 *
 */
@Controller
@RequestMapping(value = "/concert")
public class ConcertController extends BaseController {

	@Autowired
	private ConcertService concertService;

	/**
	 * 前往电影方案列表
	 * 
	 * @return
	 */
	@RequestMapping("/concertList")
	public String concertList() {
		return "concertList";
	}

	@RequestMapping("/goConcertEdit")
	public String goConcertEdit(Long id, Model model) {
		ConcertVo concertVo = concertService.findOneWithSeat(id);

		model.addAttribute("id", id);
		model.addAttribute("concertVo", concertVo);

		return "/concertEdit";
	}

	/**
	 * 保存或者更新
	 * 
	 * @param concert
	 * @param concertSeats
	 * @return
	 */
	@RequestMapping("saveOrUpdate")
	@ResponseBody
	public Result<String> saveOrUpdate(ConcertVo concertVo,Long id) {
		return concertService.saveOrUpdate(concertVo,id);
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("del")
	@ResponseBody
	public Result<String> del(Long id) {
		return concertService.del(id);
	}

	/**
	 * 获取电影方案数据
	 * 
	 * @param concertQueryVo
	 * @return
	 */
	@RequestMapping("/loadConcertList")
	@ResponseBody
	public PageTables<Concert> loadProductList(ConcertQueryVo concertQueryVo) {
		this.setBasicQueryCondition(concertQueryVo);

		Page<Concert> page = concertService.loadByCondition(concertQueryVo);
		// 封装分页对象数据
		PageTables<Concert> pageTables = new PageTables<Concert>();
		List<Concert> statisticsList = page.getContent();
		for (Concert concert : statisticsList) {
			concert.setAreaCount(concertService.countByConcert(concert.getId()));
		}
		pageTables.setDraw(concertQueryVo.getDraw());
		pageTables.setRecordsTotal(page.getTotalElements());
		pageTables.setRecordsFiltered(page.getTotalElements());
		pageTables.setData(statisticsList);

		return pageTables;
	}
}
