package com.snowstore.diana.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

/**
 * 
 * @author: fuhongxing
 * @date: 2015年11月13日
 * @version 1.0
 * @description: 数据库中生成Sequence序列主键
 */
@Service
@Transactional
public class SequenceService {
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * 
	 * @author: fuhongxing
	 * @param seqName
	 * @return 返回序列主键
	 */
	public String nextSequenceValue(String seqName) {
		synchronized (this) {
			String sql = "select " + seqName + ".nextval seq from dual";
			Query query = entityManager.createNativeQuery(sql);
			return query.getSingleResult().toString();
		}
	}
}
