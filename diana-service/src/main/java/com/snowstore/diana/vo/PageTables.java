package com.snowstore.diana.vo;

import java.util.List;

public class PageTables<T> {
	
	private Integer draw;//请求次数
	private Integer start;//从第几条数据开始查询(默认从0开始)
	private Integer length;//每页显示数量
	private Long recordsTotal; //数据总记录
	private Long recordsFiltered;//过滤后的记录数（如果有接收到前台的过滤条件，则返回的是过滤后的记录数） 
	private List<T> data ;	//数据库查询结果(不能传NULL值)
	//private Map
	public Integer getDraw() {
		return draw;
	}
	public void setDraw(Integer draw) {
		this.draw = draw;
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
	public Long getRecordsTotal() {
		return recordsTotal;
	}
	public void setRecordsTotal(Long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
	public Long getRecordsFiltered() {
		return recordsFiltered;
	}
	public void setRecordsFiltered(Long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	
	
}
