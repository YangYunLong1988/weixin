package com.snowstore.diana.utils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(SignUtil.class);
	private SignUtil(){
		
	}
	/**
	 * 验证签名
	 * @param signature 
	 * @param messageDigest
	 * @param code
	 * @return
	 * @throws Exception 
	 */
	public static String signature(String key,  Map<String,String> map) throws Exception {
		List<String> list = new ArrayList<String>();
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		String[] arr = list.toArray(new String[list.size()]);
		// 将参数进行字典序排序
		Arrays.sort(arr);
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if(null != map.get(arr[i])){
				content.append(arr[i]+"="+map.get(arr[i]));
			}
			
		}
		content.append("secret=");
		content.append(key);
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] digest = messageDigest.digest(content.toString().getBytes());
		String sign = byteToStr(digest);
		LOGGER.info("签名结果:"+sign);
		return sign;
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 * 
	 * @param byteArray
	 * @return
	 */
	private static String byteToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}

	/**
	 * 将字节转换为十六进制字符串
	 * 
	 * @param mByte
	 * @return
	 */
	private static String byteToHexStr(byte mByte) {
		char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = digit[mByte & 0X0F];

		String s = new String(tempArr);
		return s;
	}
}