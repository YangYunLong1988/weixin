package com.snowstore.diana.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Package;
import com.snowstore.diana.domain.User;
import com.snowstore.diana.repository.PackageDao;
import com.snowstore.diana.vo.PageFormVo;

@Service
@Transactional
public class PackageService {

	@Autowired
	private PackageDao packageDao;

	/** 添加、修改收货地址 */
	public Package save(Package pack) {
		return packageDao.save(pack);
	}

	/** 根据收货地址ID查询 */
	public Package findById(Long id) {
		return packageDao.findOne(id);
	}

	/**
	 * 查询用户收货地址
	 * 
	 * @return
	 */
	public Page<Package> findByUserPackage(User user, PageFormVo form) {
		// 获取用户的所有订单
		Iterator<Order> it = user.getOrder().iterator();
		List<Long> orderId = new ArrayList<Long>();
		while (it.hasNext()) {
			Order order = it.next();
			orderId.add(order.getId());
		}

		if (orderId.size() > 0) {
			return packageDao.findByOrderId(orderId, form);
		}

		return new PageImpl<Package>(new ArrayList<Package>());
	}

	/**
	 * @Title: findPackageByOrderId
	 * @Description: (根据订单id查询配送信息)
	 * @author wangyunhao
	 * @date 2015年10月21日 下午6:22:20
	 * @return String 返回类型
	 */
	public Package findPackageByOrderId(Long orderId) {
		List<Package> packages = packageDao.findPackageByOrderId(orderId);
		if (packages != null && packages.size() > 0) {
			return packages.get(0);
		}
		return null;
	}
}
