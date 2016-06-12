package com.snowstore.diana.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
		return new CustomLoginUrlAuthenticationEntryPoint("/login");
	}

	@Autowired
	private LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint;

	@Autowired
	private UserDetailsService customUserDetailsService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private static List<String> antMatchers = new ArrayList<String>();

	static {
		antMatchers.add("/");
		antMatchers.add("/channel/entrance*");
		antMatchers.add("/channel/signEntrance");
		antMatchers.add("/authorizeSign");
		antMatchers.add("/activity/*");

		antMatchers.add("/css/**");
		antMatchers.add("/fonts/**");
		antMatchers.add("/img/**");
		antMatchers.add("/js/**");
		antMatchers.add("/img/**/**");

		antMatchers.add("/movie/noteTicket");
		antMatchers.add("/movie/submitMovieResult");
		antMatchers.add("/movie/ysOrderCallBack");

		antMatchers.add("/product/*");
		antMatchers.add("/product/authorize/*");
		antMatchers.add("/product/getProductTitleImg/*");
		
		antMatchers.add("/authorizeCallback");
		antMatchers.add("/weixin/getWeixinJSConfig");
		antMatchers.add("/weixin/notify");

		antMatchers.add("/login**");
		antMatchers.add("/getValidateCode");
		antMatchers.add("/getLoginPhoneCode");
		antMatchers.add("/submitLogin");
		antMatchers.add("/getPlatform");
		antMatchers.add("/getDevice");
		antMatchers.add("/gift/syncLogsticsStatus");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.httpBasic().authenticationEntryPoint(loginUrlAuthenticationEntryPoint).and().authorizeRequests()
				.antMatchers(antMatchers.toArray(new String[antMatchers.size()])).permitAll().anyRequest()
				.fullyAuthenticated();
		http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"));

	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
	}
}
