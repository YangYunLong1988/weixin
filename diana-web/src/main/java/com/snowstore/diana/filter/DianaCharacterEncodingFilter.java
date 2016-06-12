package com.snowstore.diana.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * 特定url支持不同的编码
 * 
 * @author 作者: gaojice
 * @version 2016-1-22 15:34:21
 */
@Component
public class DianaCharacterEncodingFilter extends CharacterEncodingFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (request.getRequestURI() != null && request.getRequestURI().equals("/gift/syncLogsticsStatus")) {
			request.setCharacterEncoding("GBK");
			filterChain.doFilter(request, response);
		} else {
			super.doFilterInternal(request, response, filterChain);
		}

	}

}
