package com.snowstore.diana.utils;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snowstore.diana.domain.AbstractEntity;

/**
 * 实体帮助类
 * 
 * @author wujinsong
 * 
 */
public class CommUtil {
	/**
	 * 随机数对象
	 */
	private static Random RANDOM = new Random();
	private final static Logger LOGGER = LoggerFactory.getLogger(CommUtil.class);
	/**
	 * 数字格式化对象
	 */
	private static NumberFormat numberFormat = NumberFormat.getNumberInstance();

	/**
	 * 集合是否含有相同的相同的属性值
	 * 
	 * @param abstractEntity
	 * @param name
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static boolean hasSameValueInListBySomeAttr(List<? extends AbstractEntity> list, String name) throws IllegalArgumentException, IllegalAccessException {
		boolean flag = false;
		List<String> entities = new ArrayList<String>();

		for (AbstractEntity abstractEntity : list) {
			Object object = CommUtil.getFieldValueByName(name, abstractEntity);
			if (object != null) {
				if (entities.contains(object.toString())) {
					return true;
				} else {
					entities.add(object.toString());
				}
			}
		}

		return flag;
	}

	/**
	 * 根据属性名获取属性值
	 * 
	 * @param fieldName
	 * @param o
	 * @return
	 */
	public static Object getFieldValueByName(String fieldName, Object o) {
		try {
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + fieldName.substring(1);
			Method method = o.getClass().getMethod(getter, new Class[] {});
			Object value = method.invoke(o, new Object[] {});
			return value;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成UUID 32位
	 * 
	 * @author XieZG
	 * @Date:2015年11月16日上午10:47:04
	 * @return
	 */
	public static String createUUID() {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		return uuid;
	}

	/**
	 * 获取随机数
	 * 
	 * @author XieZG
	 * @Date:2015年11月17日下午2:56:19
	 * @param size
	 * @return
	 */
	public static String getRandom(int len) {
		numberFormat.setMinimumIntegerDigits(len);
		numberFormat.setGroupingUsed(false);
		int max = (int) Math.pow(10, len);
		return numberFormat.format(Math.abs(RANDOM.nextInt(max)));
	}

	/**
	 * 是否为数字
	 * @author XieZG
	 * @Date:2016年2月24日下午1:14:48
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否为手机号
	 * @author XieZG
	 * @Date:2016年2月24日下午1:14:51
	 * @param str
	 * @return
	 */
	public static boolean isPhone(String str) {
		boolean flag = false;
		if (isNum(str)) {
			long phone = Long.valueOf(str);
			if (phone > 13000000000L && phone < 18999999999L) {
				flag = true;
			}
		}
		return flag;
	}
	/**
	 * 判断字符串是否为日期格式
	 * @author XieZG
	 * @Date:2016年2月24日下午1:19:14 
	 * @param strDate
	 * @return
	 */
	public static boolean isDate(String strDate) {
		Pattern pattern = Pattern
				.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 校验身份证
	 * @author XieZG
	 * @Date:2016年2月24日下午1:16:17 
	 * @param IDStr
	 * @return boolean
	 */
	public static boolean IDCardValidate(String IDStr) {
		boolean flag = true;
		String errorInfo = "";// 记录错误信息
		String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2" };
		String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
		String Ai = "";
		// ================ 号码的长度 15位或18位 ================
		if (IDStr.length() != 15 && IDStr.length() != 18) {
			errorInfo = "身份证号码长度应该为15位或18位。";
			flag = false;
			return flag;
		}
		// =======================(end)========================

		// ================ 数字 除最后以为都为数字 ================
		if (IDStr.length() == 18) {
			Ai = IDStr.substring(0, 17);
		} else if (IDStr.length() == 15) {
			Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		}
		if (isNum(Ai) == false) {
			errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
			flag = false;
			return flag;
		}
		// =======================(end)========================

		// ================ 出生年月是否有效 ================
		String strYear = Ai.substring(6, 10);// 年份
		String strMonth = Ai.substring(10, 12);// 月份
		String strDay = Ai.substring(12, 14);// 月份
		if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
			errorInfo = "身份证生日无效。";
			flag = false;
			return flag;
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150 || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
				errorInfo = "身份证生日不在有效范围。";
				flag = false;
				return flag;
			}
		} catch (Exception e) {
			LOGGER.info("身份证生日不正确", e);
		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			errorInfo = "身份证月份无效";
			flag = false;
			return flag;
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			errorInfo = "身份证日期无效";
			flag = false;
			return flag;
		}
		// =====================(end)=====================

		// ================ 地区码时候有效 ================
		Hashtable<?, ?> h = GetAreaCode();
		if (h.get(Ai.substring(0, 2)) == null) {
			errorInfo = "身份证地区编码错误。";
			flag = false;
			return flag;
		}
		// ==============================================

		// ================ 判断最后一位的值 ================
		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];
		Ai = Ai + strVerifyCode;

		if (IDStr.length() == 18) {
			if (Ai.equalsIgnoreCase(IDStr) == false) {
				errorInfo = "身份证无效，不是合法的身份证号码";
				flag = false;
				return flag;
			}
		} else {
			flag = false;
			return flag;
		}
		LOGGER.info(errorInfo);
		// =====================(end)=====================
		return flag;
	}
	
	/**
	 * 设置地区编码
	 * @author XieZG
	 * @Date:2016年2月24日下午1:19:47 
	 * @return
	 */
	private static Hashtable<String, String> GetAreaCode() {
		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		hashtable.put("11", "北京");
		hashtable.put("12", "天津");
		hashtable.put("13", "河北");
		hashtable.put("14", "山西");
		hashtable.put("15", "内蒙古");
		hashtable.put("21", "辽宁");
		hashtable.put("22", "吉林");
		hashtable.put("23", "黑龙江");
		hashtable.put("31", "上海");
		hashtable.put("32", "江苏");
		hashtable.put("33", "浙江");
		hashtable.put("34", "安徽");
		hashtable.put("35", "福建");
		hashtable.put("36", "江西");
		hashtable.put("37", "山东");
		hashtable.put("41", "河南");
		hashtable.put("42", "湖北");
		hashtable.put("43", "湖南");
		hashtable.put("44", "广东");
		hashtable.put("45", "广西");
		hashtable.put("46", "海南");
		hashtable.put("50", "重庆");
		hashtable.put("51", "四川");
		hashtable.put("52", "贵州");
		hashtable.put("53", "云南");
		hashtable.put("54", "西藏");
		hashtable.put("61", "陕西");
		hashtable.put("62", "甘肃");
		hashtable.put("63", "青海");
		hashtable.put("64", "宁夏");
		hashtable.put("65", "新疆");
		hashtable.put("71", "台湾");
		hashtable.put("81", "香港");
		hashtable.put("82", "澳门");
		hashtable.put("91", "国外");
		return hashtable;
	}

}
