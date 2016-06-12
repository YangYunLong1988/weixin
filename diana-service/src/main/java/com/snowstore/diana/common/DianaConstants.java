package com.snowstore.diana.common;

import java.util.HashMap;
import java.util.Map;

public class DianaConstants {
	public static final String ATLANTIS_URL_PREFIX = "https://open.jlfex.com/rest";
	public static final String ATLANTIS_URL_PREFIX_TEST = "https://172.16.230.186/rest";
	private static Map<DianaPlatform,String> map = new HashMap<DianaConstants.DianaPlatform, String>();
	public enum DianaPlatform {
		JUPITER, PROMETHEUS,JINLUFUNDSCYL,WAP, OTHER,YUGAO,PROMETHEUS0,PROMETHEUS1,ZHT,SSDJ,LRJR,XSTZ,SNYG
	}

	public enum DianaDevice {
		PROMETHEUS, JUPITER, JINLUFUNDSCYL,UNKNOW
	}
	
	public enum DianaChannel{
		JINLU0,JINLUCCTV1,JINLUCCTV2,JINLUCCTV3,JINLUCCTV4,JINLUCCTV5,JINLUCCTV6,XSTX
	}

	public static final class AtlantisProductStatus {
		/** 募资中 */
		public static final String FUNDRAISING = "募资中";
		/** 待付款 */
		public static final String PAYMENT_WAITING = "待付款";
		/** 募资完成 */
		public static final String RAISE_SUCCEED = "募资完成";
		/** 已失效 */
		public static final String UNDO = "已失效";
	}

	public static final String SESSION_PLATFORM = "s_platform";
	public static final String SESSION_CHANNEL = "s_channel";
	
	public static final String ACTIVITY_100 = "百元玩电影";
	public static final String ACTIVITY_1 = "壹票玩电影";
	
	static{
		map.put(DianaPlatform.PROMETHEUS, ACTIVITY_100);
		map.put(DianaPlatform.JINLUFUNDSCYL, ACTIVITY_100);
		map.put(DianaPlatform.PROMETHEUS0, ACTIVITY_100);
		map.put(DianaPlatform.PROMETHEUS1, ACTIVITY_100);
		map.put(DianaPlatform.ZHT, ACTIVITY_100);
		
		map.put(DianaPlatform.JUPITER, ACTIVITY_1);
		map.put(DianaPlatform.YUGAO, ACTIVITY_1);
		map.put(DianaPlatform.SSDJ, ACTIVITY_1);
		map.put(DianaPlatform.LRJR, ACTIVITY_1);
		map.put(DianaPlatform.XSTZ, ACTIVITY_1);
	}
	/**根据接入平台获取展示产品
	 * @param platform
	 * @return
	 */
	public static String getProductByChannel(DianaPlatform platform){
		return map.get(platform);
	}
}
