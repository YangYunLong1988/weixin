package com.snowstore.diana.utils;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.model.Token;
import com.foxinmy.weixin4j.token.TokenStorager;

/**
 * 用REDIS保存TOKEN
 * 
 * @className RedisTokenStorager
 * @author jy
 * @date 2015年1月9日
 * @since JDK 1.7
 */
@Service
@Transactional
public class RedisTokenStorager implements TokenStorager {
	
	@Resource(name = "redisTemplate")
	private ValueOperations<String, Token> valueOps;
	
    @Override
    public Token lookup(String cacheKey) throws WeixinException {
            Token accessToken = valueOps.get(cacheKey);
			return accessToken;
    }

    @Override
    public void caching(String cacheKey,Token token){
    	valueOps.set(cacheKey, token, token.getExpiresIn()-1000, TimeUnit.SECONDS);
    }
           
}
