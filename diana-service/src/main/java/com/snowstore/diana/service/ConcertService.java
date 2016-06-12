package com.snowstore.diana.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.Concert;
import com.snowstore.diana.domain.ConcertSeat;
import com.snowstore.diana.repository.ConcertDao;
import com.snowstore.diana.repository.ConcertSeatDao;
import com.snowstore.diana.utils.BeanUtilsExtended;
import com.snowstore.diana.utils.CommUtil;
import com.snowstore.diana.vo.ConcertQueryVo;
import com.snowstore.diana.vo.ConcertVo;
import com.snowstore.diana.vo.Result;
import com.snowstore.diana.vo.Result.Type;

/**
 * 电影院方案Service
 * 
 * @author wujinsong
 *
 */
@Service
@Transactional
public class ConcertService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConcertService.class);


	@Autowired
	private ConcertDao concertDao;
	@Autowired
	private ConcertSeatDao concertSeatDao;

	/**
	 * 根据id获取电影方案
	 * 
	 * @param id
	 * @return
	 */
	public Concert findOne(Long id) {
		return concertDao.findOne(id);
	}

	/**
	 * 获取含有座位的电影方案
	 * 
	 * @param id
	 * @return
	 */
	public ConcertVo findOneWithSeat(Long id) {
		ConcertVo concertVo = new ConcertVo();
		if (id != null) {
			Concert concert = this.findOne(id);
			List<ConcertSeat> concertSeats = concertSeatDao.findByRefConcertOrderByCreatedDateAsc(id);

			concertVo.setConcert(concert);
			concertVo.setSeats(concertSeats);
		}

		return concertVo;
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	public Result<String> del(Long id) {
		Result<String> result = new Result<>(Type.SUCCESS);
		try {
			Concert concert = concertDao.findOne(id);
			List<ConcertSeat> concertSeats = concertSeatDao.findByRefConcertOrderByCreatedDateAsc(id);

			concertSeatDao.delete(concertSeats);
			concertDao.delete(concert);
		} catch (Exception e) {
			result.setType(Type.FAILURE);
		}

		return result;
	}

	/**
	 * 保存或更新电影方案
	 * 
	 * @param concertVo
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public Result<String> saveOrUpdate(ConcertVo concertVo, Long id) {
		Result<String> result = new Result<>(Type.SUCCESS);
		// 判断是否可以新增（名称唯一）
		boolean canSaved = this.canSaved(id, concertVo.getConcert().getName());
		if (!canSaved) {
			result.setType(Type.FAILURE);
			result.addMessage("已经存在该名称的电影方案了！");

			return result;
		}

		try {
			boolean isHasSameValue = CommUtil.hasSameValueInListBySomeAttr(concertVo.getSeats(), "type");
			if (isHasSameValue) {
				result.setType(Type.FAILURE);
				result.addMessage("含有价格同名区间！");

				return result;
			}
		} catch (Exception e) {
			result.setType(Type.FAILURE);
			result.addMessage("出现错误！");

			return result;
		}

		Concert tempConcert = concertVo.getConcert();
		try {
			List<ConcertSeat> tempConcertSeats = new ArrayList<>();

			if (id != null) {
				Concert savedConcert = concertDao.findOne(id);
				BeanUtilsExtended.copyProperties(savedConcert, tempConcert);
				tempConcert = savedConcert;

				for (ConcertSeat concertSeat : concertVo.getSeats()) {
					if (concertSeat.getIdd() != null) {
						ConcertSeat savedConcertSeat = concertSeatDao.findOne(concertSeat.getIdd());
						BeanUtilsExtended.copyProperties(savedConcertSeat, concertSeat);
						tempConcertSeats.add(savedConcertSeat);
					} else {
						tempConcertSeats.add(concertSeat);
					}
				}
			} else {
				tempConcertSeats = concertVo.getSeats();
			}

			tempConcert = concertDao.save(tempConcert);
			for (ConcertSeat concertSeat : tempConcertSeats) {
				concertSeat.setRefConcert(tempConcert.getId());
			}

			concertSeatDao.save(tempConcertSeats);
		} catch (Exception e) {
			result.setType(Type.FAILURE);
			LOGGER.error("保存:Concert" + tempConcert.getId() + tempConcert.getName() + "错误");
		}

		return result;
	}

	/**
	 * 
	 * @param concertQueryVo
	 * @return
	 */
	public Page<Concert> loadByCondition(final ConcertQueryVo concertQueryVo) {

		return concertDao.findAll(new Specification<Concert>() {
			@Override
			public Predicate toPredicate(Root<Concert> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();

				// 产品名称
				if (StringUtils.isNotEmpty(concertQueryVo.getName())) {
					list.add(cb.like(root.<String> get("name"), "%" + concertQueryVo.getName() + "%"));
				}

				Predicate[] predicate = new Predicate[list.size()];
				return cb.and(list.toArray(predicate));
			}

		}, concertQueryVo);
	}

	/**
	 * 获取区间数量
	 * 
	 * @param refConcert
	 * @return
	 */
	public Long countByConcert(Long refConcert) {
		return concertSeatDao.countByRefConcert(refConcert);
	}

	/**
	 * 根据名称查找电影方案，根据设计，名称是唯一的
	 * 
	 * @param name
	 * @return
	 */
	public Concert findByName(String name) {
		return concertDao.findByName(name);
	}

	/**
	 * 判断是否可以新增电影方案
	 * 
	 * @param id
	 * @param name
	 * @return
	 */
	public boolean canSaved(Long id, String name) {
		boolean canSaved = true;
		if (id != null) {
			Concert concert = this.findByName(name);
			if (concert.equals(id)) {
				canSaved = false;
			}
		} else {
			Concert concert = this.findByName(name);
			if (concert != null) {
				canSaved = false;
			}
		}

		return canSaved;
	}
}
