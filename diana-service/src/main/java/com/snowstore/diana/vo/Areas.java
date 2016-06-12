package com.snowstore.diana.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * 地区集合类
 * @author XieZG
 * @Date:2016年2月18日下午2:06:25
 */
public class Areas {
	private static Map<String,List<String>> map=new HashMap<String, List<String>>();
	
	public void put(String key,String value){
		if(map.containsKey(key)){
			map.get(key).add(value);
		}else{
			List<String> list=new ArrayList<String>();
			list.add(value);
			map.put(key, list);
		}
	}
	
	public Set<String> keySet(){
		return map.keySet();
	}
	
	public List<String> get(String key){
		return map.get(key);
	}
}
