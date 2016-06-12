package com.snowstore.diana.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.snowstore.diana.domain.Product;
import com.snowstore.diana.domain.Product.Status;

@Repository
public interface ProductDao extends PagingAndSortingRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	public Product findByReferenceProduct(Long product);

	/**
	 * @Title: findProductList
	 * @Description: (查询所有产品信息)
	 * @author wangyunhao
	 * @date 2015年10月20日 上午11:08:59
	 * @return ArrayList<Product> 返回类型
	 */
	@Query("from Product P ")
	public ArrayList<Product> findProductList();

	/**
	 * @Title: findProductById
	 * @Description: (根据产品主键查询产品信息)
	 * @author wangyunhao
	 * @param id
	 * @date 2015年10月20日 上午11:07:33
	 * @return Product 返回类型
	 */
	@Query("from Product P where P.id = ?1")
	public Product findProductById(Long id);

	public Page<Product> findByStatusIn(List<Status> status, Pageable page);

	public List<Product> findByStatusIn(List<Status> status);

	public List<Product> findByStatusInAndTypeNot(List<Status> status, String type);
	
	@Query("select max(P.sortNum) from Product P")
	public Long findMaxSortNum();
	
	public Page<Product> findByStatusInAndTypeIn(List<Status> status,List<String> type, Pageable page);

	public List<Product> findByStatusAndType(Status status,String type);

	public List<Product> findByName(String name);
}