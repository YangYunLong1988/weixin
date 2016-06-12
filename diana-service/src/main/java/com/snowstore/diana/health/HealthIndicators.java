package com.snowstore.diana.health;

import java.util.Collection;

import com.snowstore.diana.service.GiftCodeService;
import org.springframework.boot.actuate.endpoint.SystemPublicMetrics;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.snowstore.diana.service.CouponService;

/**
 * @author wangtengfei 系统依赖项检测
 */
@Configuration
public class HealthIndicators {

	/**
	 * 系统参数
	 * 
	 * @param systemPublicMetrics
	 * @return
	 */
	@Bean
	public HealthIndicator system(final SystemPublicMetrics systemPublicMetrics) {
		return new AbstractHealthIndicator() {
			@Override
			protected void doHealthCheck(Builder builder) throws Exception {
				Collection<Metric<?>> set = systemPublicMetrics.metrics();
				builder.up();
				for (Metric<?> metric : set) {
					builder.withDetail(metric.getName(), metric.getValue());
				}

			}
		};
	}
	
	/**火票卡券数监控
	 * @param couponService
	 * @return
	 */
	@Bean
	public HealthIndicator checkCoupon(final CouponService couponService){
		return new AbstractHealthIndicator() {
			@Override
			protected void doHealthCheck(Builder builder) throws Exception {
				long count = couponService.countUnusedCouponCount();
				if(count>5000){
					builder.up().withDetail("count", count);
				}else{
					builder.down().withDetail("memo", "火票卡券数:"+count+",不足5000");
				}
			}
		};
	}

	/**
	 * 检查DQ券是否充足
	 * @author wulinjie
	 * @param giftCodeService
	 * @return
	 */
	@Bean
	public HealthIndicator checkDqCode(final GiftCodeService giftCodeService){
		return new AbstractHealthIndicator() {
			@Override
			protected void doHealthCheck(Builder builder) throws Exception {
				long count = giftCodeService.getDQCodeAmount();
				if(count > 500){
					builder.up().withDetail("count", count);
				}else{
					builder.up().withDetail("memo", "DQ券数：" + count + "，不足500");
				}
			}
		};
	}

}
