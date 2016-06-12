package com.snowstore.diana.config;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.snowstore.diana.filter.HTMLCharacterFilter;
import com.snowstore.log.configure.LogStashConfigure;
import com.snowstore.terra.client.TerraPropertyPlaceholderConfigurer;

@Configuration
@EnableRedisHttpSession
@Import(LogStashConfigure.class)
public class WebConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 特殊字符过滤
	 * 
	 * @param filter
	 * @return
	 */
	@Bean
	public FilterRegistrationBean registration(HTMLCharacterFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(true);
		return registration;
	}

	/**
	 * 配置中心
	 * 
	 * @return
	 */
	@Bean
	public TerraPropertyPlaceholderConfigurer terraPropertyPlaceholderConfigurer() {
		return new TerraPropertyPlaceholderConfigurer();
	}

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {

		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				ErrorPage error400Page = new ErrorPage(HttpStatus.BAD_REQUEST, "/error_page/400.html");
				ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/error_page/401.html");
				ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/html/404.html");
				ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/html/500.html");
				container.addErrorPages(error400Page, error401Page, error404Page, error500Page);
			}
		};
	}
}
