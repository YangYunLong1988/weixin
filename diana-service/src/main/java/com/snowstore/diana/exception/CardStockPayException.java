package com.snowstore.diana.exception;

/**
 * 卡券支付一场
 * Created by wangtengfei on 2016/01/28.
 */
public class CardStockPayException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8470971425352706587L;

	/**
	 * @author wangtengfei
	 * @Date:2016年1月28日下午11:05:06 
	 */

	public CardStockPayException(String message) {
		super(message);
	}

}
