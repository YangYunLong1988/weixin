package com.snowstore.diana.exception;

/**
 * 没有卡用的卡券
 * Created by wulinjie on 2015/12/23.
 */
public class CouponNotFoundException extends RuntimeException{

	/**
	 * @author XieZG
	 * @Date:2016年1月6日下午1:05:06 
	 */
	private static final long serialVersionUID = -5144876789279515041L;

	public CouponNotFoundException(String message) {
		super(message);
	}

}
