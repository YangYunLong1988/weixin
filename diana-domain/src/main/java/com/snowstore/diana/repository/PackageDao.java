package com.snowstore.diana.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snowstore.diana.domain.Package;

/**
 * @ClassName: PackageDao
 * @Description: (配送信息)
 * @author wangyunhao
 * @date 2015年10月22日 上午10:32:24
 */
public interface PackageDao extends PagingAndSortingRepository<Package, Long>, JpaSpecificationExecutor<Package> {

	@Query("from Package P where P.orderId = ?1 ")
	List<Package> findPackageByOrderId(Long orderId);

	@Query("from Package p where p.orderId in (?1)")
	public List<Package> findByOrderId(List<Long> orderId);

	@Query("from Package p where p.orderId in (?1)")
	public Page<Package> findByOrderId(List<Long> orderId, Pageable page);

}
