package com.snowstore.diana.service;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by wulinjie on 2016/2/24.
 */
public interface BaseMovieService {

	public JSONObject getLoginUrl(HttpServletRequest request);

	public String sychOrder(HttpServletRequest request);

}
