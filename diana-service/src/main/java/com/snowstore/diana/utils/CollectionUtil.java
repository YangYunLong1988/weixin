package com.snowstore.diana.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionUtil {
	
	private static Logger LOGGER = LoggerFactory.getLogger(CollectionUtil.class);

	/**
	 * 根据List元素的属性值进行排序
	 * @author wulinjie
	 * @param self 待排序的集合
	 * @param property 属性名
	 * @return
	 */
	public static <T> Map<Object, List<T>> groupBy(Collection<T> self, String property) {
		LinkedHashMap<Object, List<T>> answer = new LinkedHashMap<Object, List<T>>();
		try {
			Iterator<T> i$ = self.iterator();
			while (i$.hasNext()) {
				T element = i$.next();
				Class<? extends Object> clazz = element.getClass();
				String methodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
				Method method = clazz.getDeclaredMethod(methodName, new Class[]{});
				groupAnswer(answer, element, method.invoke(element));
			}
		} catch (Exception e) {
			LOGGER.error("对Collection进行排序错误，" + e.toString());
			return null;
		}
		return answer;
	}

	/**
	 *
	 * @param answer
	 * @param element
	 * @param value
	 */
	protected static <T> void groupAnswer(Map<Object, List<T>> answer, T element, Object value) {
		if (answer.containsKey(value)) {
			answer.get(value).add(element);
		} else {
			List<T> groupedElements = new ArrayList<T>();
			groupedElements.add(element);
			answer.put(value, groupedElements);
		}
	}
}
