package com.snowstore.diana.config;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.snowstore.diana.domain.User;
import com.snowstore.diana.service.CustomerService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomerService customerService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private UserDetailsService customUserDetailsService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/css/**", "/fonts/**", "/js/**", "/font-awesome/**/**", "/img/**","/importUsers","/order/loadOrderList/channel","/job/*").permitAll().anyRequest().fullyAuthenticated().and().formLogin().loginPage("/login").defaultSuccessUrl("/", true).failureUrl("/login?error").permitAll();
		http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		List<User> users = null;
		String email = System.getProperty("diana.email");
		if(StringUtils.isEmpty(email)){
			throw new RuntimeException("请设置管理员邮箱！");
		}
		Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
		Matcher m = p.matcher(email);
		boolean b = m.matches();
		if(!b){
			throw new RuntimeException("管理员邮箱格式不正确！");
		}
		users = customerService.findByRole(User.Role.ADMIN.name());
		if(users.isEmpty()){
			customerService.createAdmain(email);
		}else if(users.size()>1){
			//管理员变更删除
			customerService.del(users);
			//管理员变更新建
			customerService.createAdmain(email);
		} else if(!email.equals(users.get(0).getEmail())){
			//管理员变更删除
			customerService.del(users);
			//管理员变更新建
			customerService.createAdmain(email);
		}
		auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
	}
}