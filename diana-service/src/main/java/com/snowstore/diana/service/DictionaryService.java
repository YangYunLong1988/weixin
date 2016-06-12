package com.snowstore.diana.service;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.Dictionary;
import com.snowstore.diana.repository.DictionaryDao;

@Service
@Transactional(readOnly = true)
public class DictionaryService {
	@Autowired
	private DictionaryDao dictionaryDao;

	/**
	 * 字典缓存,只读
	 */
	private Set<Dictionary> dics = new HashSet<Dictionary>();

	public Dictionary findByItemCode(String itemCode) {
		if (CollectionUtils.isEmpty(dics)) {
			for (Dictionary dictionary : dictionaryDao.findAll()) {
				dics.add(dictionary);
			}
		}
		for (Dictionary dictionary : dics) {
			if (itemCode.equals(dictionary.getItemCode())) {
				return dictionary;
			}
		}
		return new Dictionary();
	}
}
