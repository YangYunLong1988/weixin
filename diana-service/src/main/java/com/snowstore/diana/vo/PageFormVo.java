package com.snowstore.diana.vo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/**
 * 分页vo对象
 * 
 * @author: fuhongxing
 * @date: 2015年6月25日
 * @version 1.0
 */
public class PageFormVo implements Pageable {
	private int page = 1;
	private int rows = 10;
	private String sort = "id";
	private String order = "desc";
	private Sort sortSet;
	private Integer start;
	private Integer length;
	private Integer draw;

	public PageFormVo() {
	}

	public PageFormVo(int page, int rows) {
		this.page = page;
		this.rows = rows;
	}

	public PageFormVo(int page, int rows, Sort sortSet) {
		this.page = page;
		this.rows = rows;
		this.sortSet = sortSet;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
	
	
	public void setSortSet(Sort sortSet) {
		this.sortSet = sortSet;
	}

	@Override
	public Sort getSort() {
		if (sortSet == null) {
			sortSet = new Sort(Direction.fromString(order), sort);
		}
		return sortSet;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public int getPageNumber() {
		return page - 1;
	}

	@Override
	public int getPageSize() {
		return rows;
	}

	@Override
	public int getOffset() {
		return (page - 1) * rows;
	}

	@Override
	public boolean hasPrevious() {
		return (page - 1) > 0;
	}

	@Override
	public Pageable first() {
		return new PageFormVo(0, rows, getSort());
	}

	@Override
	public Pageable next() {
		return new PageFormVo(page, rows, getSort());
	}

	@Override
	public Pageable previousOrFirst() {
		return hasPrevious() ? new PageFormVo(page - 2, rows, getSort()) : this;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getDraw() {
		return draw;
	}

	public void setDraw(Integer draw) {
		this.draw = draw;
	}
}
