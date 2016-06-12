package com.snowstore.diana.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.foxinmy.weixin4j.api.BaseApi;
import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.mp.WeixinProxy;
import com.foxinmy.weixin4j.mp.api.OauthApi;
import com.foxinmy.weixin4j.mp.token.WeixinJSTicketCreator;
import com.foxinmy.weixin4j.token.TokenHolder;
import com.foxinmy.weixin4j.token.TokenStorager;

@Configuration
public class WeixinConfig {

	@Autowired
	private TokenStorager redisTokenStorager;
	
	@Bean
	public WeixinProxy weixinProxy() throws WeixinException{
		WeixinProxy proxy =  new WeixinProxy (redisTokenStorager);
		return proxy;
	}
	
	@Bean
	public OauthApi oauthApi(){
		return new OauthApi();
	}
	@Autowired
	private WeixinProxy proxy;
	
	@Bean TokenHolder weixinJSTicketTokenHolder() throws WeixinException{
		TokenHolder jsTokenHolder = new TokenHolder(new WeixinJSTicketCreator(BaseApi.DEFAULT_WEIXIN_ACCOUNT.getId(), proxy.getTokenHolder()), redisTokenStorager);
		return jsTokenHolder;
	}
}
