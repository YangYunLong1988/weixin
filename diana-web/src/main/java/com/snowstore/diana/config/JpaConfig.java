package com.snowstore.diana.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.code.kaptcha.util.Config;
import com.snowstore.diana.service.userDetails.UserDetailsImpl;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
	@Bean
	public AuditorAware<String> auditorProvider() {
		 return new AuditorAware<String>() {

			@Override
			public String getCurrentAuditor() {
				Authentication authen = SecurityContextHolder.getContext().getAuthentication();
				return authen == null||"anonymousUser".equals(authen.getPrincipal()) ?"anonymousUser":((UserDetailsImpl)authen.getPrincipal()).getUsername();
			}
		};
	 }
	
	@Bean
	public Config config(){
		Properties pro = new Properties();
		pro.put("kaptcha.border", "yes");
		pro.put("kaptcha.border.color", "105,179,90");
		pro.put("kaptcha.textproducer.font.color", "blue");
		pro.put("kaptcha.image.width", "125");
		pro.put("kaptcha.image.height", "72");
		pro.put("kaptcha.textproducer.font.size", "46");
		pro.put("kaptcha.session.key", "code");
		pro.put("kaptcha.textproducer.char.length", "4");
		pro.put("kaptcha.textproducer.char.space", "2");
		pro.put("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
		return new Config(pro);
	}
}
