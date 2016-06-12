package com.snowstore.diana.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.BestcakeCard;
import com.snowstore.diana.repository.BestcakeCardDao;

@Service
@Transactional
public class BestCakeCardService {
	@Autowired
	private BestcakeCardDao bestcakeCardDao;
	
	public BestcakeCard getOne(){
		List<BestcakeCard> list = bestcakeCardDao.findByIsBind(false,new PageRequest(1, 1));
		if(!list.isEmpty()){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	public BestcakeCard save(BestcakeCard bestcakeCard){
		return bestcakeCardDao.save(bestcakeCard);
	}
}
