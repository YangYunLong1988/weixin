package com.snowstore.diana.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json tools
 * 
 * @author: fuhongxing
 * @date: 2015年6月17日
 * @version 1.0
 */
public class JacksonUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(JacksonUtil.class);
	private static ObjectMapper mapper;

	private JacksonUtil() {

	}

	/**
	 * 获取ObjectMapper实例
	 * 
	 * @return
	 */
	public static ObjectMapper getMapperInstance() {
		if (mapper == null) {
			mapper = new ObjectMapper();
		}
		return mapper;
	}

	/**
	 * 将java对象转换成json字符串
	 * 
	 * @param obj
	 *            准备转换的对象
	 * @return json字符串
	 * @throws Exception
	 */
	public static String beanToJson(Object obj) {
		try {
			ObjectMapper objectMapper = getMapperInstance();
			String json = objectMapper.writeValueAsString(obj);
			return json;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			throw new ClassCastException(e.getMessage());
		}
	}

	/**
	 * 将json字符串转换成java对象
	 * 
	 * @param json
	 *            准备转换的json字符串
	 * @param cls
	 *            准备转换的类
	 * @return
	 * @throws Exception
	 */
	public static Object jsonToBean(String json, Class<?> cls) throws ClassCastException {
		try {
			ObjectMapper objectMapper = getMapperInstance();
			Object vo = objectMapper.readValue(json, cls);
			return vo;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			throw new ClassCastException(e.getMessage());
		}
	}

	/**
	 * 使用泛型方法，把json字符串转换为相应的JavaBean对象。
	 * (1)转换为普通JavaBean：readValue(json,Student.class)
	 * (2)转换为List,如List<Student>,将第二个参数传递为Student
	 * [].class.然后使用Arrays.asList();方法把得到的数组转换为特定类型的List
	 * 
	 * @param jsonStr
	 * @param valueType
	 * @return
	 */
	public static <T> T readValue(String jsonStr, Class<T> valueType) {

		try {
			ObjectMapper objectMapper = getMapperInstance();
			return objectMapper.readValue(jsonStr, valueType);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
		return null;
	}

	/**
	 * json转java集合
	 * 
	 * @param jsonStr
	 *            字符串
	 * @param valueTypeRef
	 *            TypeReference可以用来应对复杂的范型
	 * @return
	 */
	public static <T> T readValue(String jsonStr, TypeReference<T> valueTypeRef) {
		try {
			ObjectMapper objectMapper = getMapperInstance();
			return objectMapper.readValue(jsonStr, valueTypeRef);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}

		return null;
	}

}