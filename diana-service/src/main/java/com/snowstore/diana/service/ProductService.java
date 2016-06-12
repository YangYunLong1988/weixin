package com.snowstore.diana.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.snowstore.diana.common.Calendars;
import com.snowstore.diana.common.DianaConstants;
import com.snowstore.diana.domain.Concert;
import com.snowstore.diana.domain.ConcertSeat;
import com.snowstore.diana.domain.Product;
import com.snowstore.diana.domain.Product.ImageType;
import com.snowstore.diana.domain.Product.Status;
import com.snowstore.diana.domain.Product.Type;
import com.snowstore.diana.repository.ConcertDao;
import com.snowstore.diana.repository.ConcertSeatDao;
import com.snowstore.diana.repository.GroupRuleDao;
import com.snowstore.diana.repository.ProductDao;
import com.snowstore.diana.utils.ExcelUitl;
import com.snowstore.diana.vo.ConcertVo;
import com.snowstore.diana.vo.ImgVo;
import com.snowstore.diana.vo.PageFormVo;
import com.snowstore.diana.vo.ProductVo;
import com.snowstore.diana.vo.Result;
import com.snowstore.poseidon.client.JopClient;
import com.snowstore.poseidon.client.exception.BusinessException;
import com.snowstore.poseidon.client.vo.request.FinanceProductReq;
import com.snowstore.poseidon.client.vo.response.FinanProductVo;
import com.snowstore.poseidon.client.vo.response.FinanceProductResp;

@Service
@Transactional
public class ProductService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	private ProductDao productDao;
	@Autowired
	private JopClient jopClient;

	@Autowired
	private ConcertDao concertDao;

	@Autowired
	private ConcertSeatDao concertSeatDao;
	
	@Autowired
	private GroupRuleDao groupRuleDao;

	/**
	 * 根据日期同步理财产品
	 * 
	 * @param certificate
	 * @param createDate
	 */
	public void synchronizeProductFromSingleCertificate(String createDate) {
		FinanceProductReq reqData = new FinanceProductReq();
		reqData.setLevel("2");
		reqData.setPageNum(1);
		reqData.setPageSize(10000);
		reqData.setCreateDate(createDate);
		FinanceProductResp result = null;
		try {
			result = jopClient.req(reqData);
		} catch (BusinessException e) {
			LOGGER.error("调用开放平台出错", e);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		if (null == result) {
			LOGGER.info("没有新的理财产品........");
			return;
		}
		List<FinanProductVo> proList = result.getContent();
		LOGGER.info("更新理财产品....数量：" + proList.size());
		for (FinanProductVo finanProductVo : proList) {
			try {
				add(finanProductVo);
			} catch (Exception e) {
				LOGGER.info("finanProductVo:" + finanProductVo.getId());
				LOGGER.error("保存理财产品出错", e);
			}
		}
	}

	public Product add(FinanProductVo vo) throws ParseException {
		if (null == vo) {
			return null;
		}
		BigDecimal price = groupRuleDao.getMaxPrice();
		Product product = productDao.findByReferenceProduct(Long.valueOf(vo.getId()));
		if (null == product) {
			product = new Product();
			product.setAmount(vo.getLimit());
			product.setAvailableAmount(vo.getLimit());
			product.setPeriod(Integer.valueOf(vo.getTimeLimit().substring(0, vo.getTimeLimit().length() - 1)));
			product.setPeriodType(vo.getTimeLimit().substring(vo.getTimeLimit().length() - 1, vo.getTimeLimit().length()));
			Date begin = Calendars.StringToDate(vo.getDateOfValue());
			product.setBeginCountInterest(begin);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(begin);
			calendar.add(Calendar.DAY_OF_MONTH, product.getPeriod());
			product.setEndCountInterest(DateUtils.truncate(calendar.getTime(), Calendar.DATE));
			product.setGuaranteeAgency(vo.getAssetGuarantee());
			product.setInstitutionInstruction(vo.getInstitutionInstruction());
			product.setLabelVolmoney(vo.getLabelVolmoney());
			product.setMinPurchaseAmount(new BigDecimal(100));// 起投金额设为100
			product.setName(vo.getFinanceProductName());
			product.setOriginalName(vo.getFinanceProductName());
			product.setRaiseEndTime(Calendars.StringToDate(vo.getRaiseEndTime(), "yy-MM-dd HH:mm:ss"));
			product.setRate(vo.getInterestRate());
			product.setReferenceProduct(Long.valueOf(vo.getId()));
			product.setRepaymentMode(vo.getAssetsList().get(0).getRepaymentMode());
			product.setRiskTip(vo.getRiskTip());
			product.setStatus(Product.Status.初始);
			product = productDao.save(product);
			return product;
		} else if (Product.Status.下架.equals(product.getStatus())) {
			return product;
		} else if (Calendars.getCurrentDate().after(product.getRaiseEndTime()) || DianaConstants.AtlantisProductStatus.UNDO.equals(vo.getStatus()) || DianaConstants.AtlantisProductStatus.RAISE_SUCCEED.equals(vo.getStatus())) {
			product.setStatus(Product.Status.售罄);
			product = productDao.save(product);
			return product;
		} else if (Product.Status.售罄.equals(product.getStatus()) && product.getAvailableAmount().compareTo(Product.Type.多张组合.name().equals(product.getType())?price:product.getPrice()) >= 0) {
			product.setStatus(Product.Status.在售);
			product = productDao.save(product);
			return product;
		}
		return null;

	}

	/**
	 * @Title: findProductList
	 * @Description: (查询所有产品信息)
	 * @author wangyunhao
	 * @date 2015年10月20日 上午11:08:41
	 * @return Result<ArrayList<Product>> 返回类型
	 */
	public Result<ArrayList<Product>> findProductList() {
		LOGGER.info("首页展示接口：###开始查询所有产品信息......");
		Result<ArrayList<Product>> result = new Result<ArrayList<Product>>(Result.Type.SUCCESS);
		ArrayList<Product> productList = null;
		try {
			productList = productDao.findProductList();
		} catch (Exception e) {
			LOGGER.error("查询产品信息出错！" + e);
		}
		if (productList != null && productList.size() > 0) {
			// formatProductList(productList);
			LOGGER.info("查询到的产品数量：###" + productList.size());
			result.setData(productList);
			result.addMessage("查询所有产品信息成功！");
		} else {
			result.addMessage("暂时没有产品信息！");
		}
		return result;
	}

	/**
	 * @Title: findProduct
	 * @Description: (根据产品主键查询产品信息)
	 * @author wangyunhao
	 * @date 2015年10月20日 上午11:08:10
	 * @return Result<Product> 返回类型
	 */
	public Result<Product> findProduct(Long id) {
		LOGGER.info("理财产品查询接口：###开始查询单个产品信息......");
		Result<Product> result = new Result<Product>(Result.Type.SUCCESS);
		Product product = null;
		try {
			product = productDao.findProductById(id);
		} catch (Exception e) {
			LOGGER.error("查询单个产品信息出错！" + e);
		}
		if (product != null) {
			LOGGER.info("查询理财产品：###" + product.getId());
			result.setData(product);
			result.addMessage("查询单个产品信息成功！");
		} else {
			result.addMessage("暂时没有查到对应的产品信息！");
		}
		return result;
	}

	public List<Product> findByName(String name){
		return productDao.findByName(name);
	}

	/**
	 * @param page
	 * @return
	 */
	public List<ProductVo> findByStatus(Pageable page) {
		List<Status> status = new ArrayList<Product.Status>();
		status.add(Product.Status.在售);
		Page<Product> resultPage = productDao.findByStatusIn(status, page);
		List<ProductVo> result = new ArrayList<ProductVo>();
		for (Product product : resultPage) {
			ProductVo vo = new ProductVo();
			vo.setProductId(product.getId());
			vo.setName(product.getName());
			result.add(vo);
		}
		return result;
	}

	/**
	 * 根据产品类型
	 * @param status 	产品状态
	 * @param type		产品类型
	 * @return
	 */
	public List<Product> findByStatusAndType(Status status, Type type){
		return productDao.findByStatusAndType(status, type.name());
	}

	/**根据产品状态和类型查询订单
	 * @param page
	 * @param status
	 * @param type
	 * @return
	 */
	public List<ProductVo> findByStatusAndProductType(Pageable page,List<Status> status,List<String> type) {
		Page<Product> resultPage = productDao.findByStatusInAndTypeIn(status, type,page);
		List<ProductVo> result = new ArrayList<ProductVo>();
		for (Product product : resultPage) {
			ProductVo vo = new ProductVo();
			vo.setProductId(product.getId());
			vo.setName(product.getName());
			vo.setIsOff(!Product.Status.在售.equals(vo.getStatus()));
			result.add(vo);
		}
		return result;
	}

	/**
	 * 根据理财产品获取座位详情
	 * 
	 * @param id
	 * @return
	 */
	public ConcertVo getConcert(Long id) {
		Product product = productDao.findOne(id);
		Concert concert = concertDao.findOne(product.getRefConcert());
		ConcertVo vo = new ConcertVo();
		vo.setConcert(concert);
		vo.setSeats(concertSeatDao.findByRefConcert(concert.getId()));
		return vo;
	}

	public ConcertSeat getConcertSeat(Long id) {
		return concertSeatDao.findOne(id);
	}

	public void initSeat(Long id) {
		Product product = productDao.findOne(id);
		Concert concert = new Concert();
		concert.setName("莫文蔚全球巡演");
		concert.setPerformAddress("鸟巢");
		concert.setPerformTime(new Date());
		concert = concertDao.save(concert);
		ConcertSeat seat = new ConcertSeat();
		seat.setRefConcert(concert.getId());
		seat.setPrice(new BigDecimal(100));
		seat.setType("A区");
		ConcertSeat seat1 = new ConcertSeat();
		seat1.setRefConcert(concert.getId());
		seat1.setPrice(new BigDecimal(100));
		seat1.setType("B区");
		concertSeatDao.save(seat);
		concertSeatDao.save(seat1);
		product.setRefConcert(concert.getId());
		productDao.save(product);
	}

	/**
	 * 根据id获取产品
	 * 
	 * @param id
	 * @return
	 */
	public Product get(Long id) {
		return productDao.findOne(id);
	}

	public Page<Product> loadAll(PageFormVo page) {
		return productDao.findAll(page);
	}

	/**
	 * 根据页面查询条件，获取产品列表
	 * 
	 * @author wulinjie
	 * @param productVo
	 * @param pageFormVo
	 * @return
	 */
	public Page<Product> loadByCondition(final ProductVo productVo, PageFormVo pageFormVo) {

		return productDao.findAll(new Specification<Product>() {

			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();

				// 产品ID
				if (productVo.getProductId() != null) {
					list.add(cb.equal(root.<Serializable> get("id"), productVo.getProductId()));
				}

				// 产品名称
				if (StringUtils.isNotEmpty(productVo.getName())) {
					list.add(cb.like(root.<String> get("name"), "%" + productVo.getName() + "%"));
				}

				// 状态
				if (StringUtils.isNotEmpty(productVo.getStatus()) && !"全部".equals(productVo.getStatus())) {
					list.add(cb.equal(root.<String> get("status"), Product.Status.valueOf(productVo.getStatus())));
				}
				
				// 产品类型
				if (StringUtils.isNotEmpty(productVo.getType()) && !"全部".equals(productVo.getType())) {
					list.add(cb.equal(root.<String> get("type"), Product.Type.valueOf(productVo.getType()).name()));
				}

				// 统计日期
				if (productVo.getCreatedDateStart() != null) {
					list.add(cb.greaterThanOrEqualTo(root.<Date> get("createdDate"), productVo.getCreatedDateStart()));
				}

				if (productVo.getCreatedDateEnd() != null) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(productVo.getCreatedDateEnd());
					calendar.add(Calendar.DATE, 1);
					calendar.add(Calendar.SECOND, -1);
					list.add(cb.lessThanOrEqualTo(root.<Date> get("createdDate"), calendar.getTime()));
				}

				// 过滤产品状态（只显示在售的）
				// list.add(cb.in(root.<String>
				// get("status").in(Arrays.asList(Product.Status.在售.name(),
				// Product.Status.初始.name(), Product.Status.下架.name()))));

				Predicate[] predicate = new Predicate[list.size()];
				return cb.and(list.toArray(predicate));
			}

		}, pageFormVo);

	}

	/**
	 * 编辑产品
	 * 
	 * @author 吴林杰
	 * @param productId
	 * @param model
	 */
	public void editProduct(Long productId, Model model) {
		int random = (int) (Math.random() * 100); // 添加随机数，防止页面图片浏览器缓存
		Product product = get(productId); // 加载产品
		List<Concert> concertList = (List<Concert>) concertDao.findAll(); // 演唱会

		model.addAttribute("product", product);
		model.addAttribute("concertList", concertList);
		model.addAttribute("typeList", Product.Type.values());
		model.addAttribute("random", random);
		model.addAttribute("stringUtils", new StringUtils());
	}

	/**
	 * 保存产品
	 * 
	 * @author wulinjie
	 * @param product
	 *            产品表单对象
	 */
	public void saveProduct(ProductVo vo, MultipartFile titleImg, MultipartFile posterImg) {
		LOGGER.info("开始保存产品  产品ID：" + vo.getProductId());
		try {
			Product product = productDao.findOne(vo.getProductId());
			if (product == null) {
				LOGGER.error("保存产品出错，该产品不存在");
				throw new RuntimeException("保存产品出错，该产品不存在");
			}
			product.setName(vo.getName());
			product.setType(vo.getType());
			
			if (Product.Type.valueOf(vo.getType()) == Type.多张组合){
				product.setGiftNum(0);	//裸票默认无礼品
			}else{
				product.setPrice(vo.getPrice());	//每份价格
				product.setGiftNum(vo.getGiftNum());
				product.setRefConcert(null);		//移除演唱会价格区间
				product.setPosterImgName(null);		//移除海报标题(影视类不需要海报)
				product.setPosterImg(null);			//移除海报内容(影视类不需要海报)
				product.setExchangeAmount(vo.getExchangeAmount().intValue());
			}
			// 比较上传文件是否发生了改变
			if (titleImg != null && !compareFileContent(titleImg.getBytes(), product.getTitleImg())) {
				product.setTitleImgName(titleImg.getOriginalFilename());
				product.setTitleImg(titleImg.getBytes());
			}

			// 比较上传文件是否发生了改变
			if (posterImg != null && !compareFileContent(posterImg.getBytes(), product.getPosterImg())) {
				product.setPosterImgName(posterImg.getOriginalFilename());
				product.setPosterImg(posterImg.getBytes());
			}

			


			// 初始产品改为在售，其他保留原来状态
			if (product.getStatus().equals(Status.初始)) {
				product.setStatus(Status.在售);
			}

			productDao.save(product);
		} catch (Exception e) {
			LOGGER.error("保存产品出错!" + e.toString());
			throw new RuntimeException("保存产品出错!" + e.toString());
		}
		LOGGER.info("产品保存成功");
	}

	/**
	 * 下架产品
	 * 
	 * @author wulinjie
	 * @param productId
	 *            产品ID
	 * @return 处理结果
	 */
	public Result<String> offShelve(Long productId) {
		LOGGER.info("开始产品下架，产品ID：" + productId);
		Product product = new Product();
		try {
			product = get(productId);
			if (product == null) {
				return new Result<String>(Result.Type.SUCCESS, "产品编号[" + productId + "]不存在");
			}

			/*
			 * if () { return new Result(Result.Type.SUCCESS, "产品[" +
			 * product.getName() + "]已经下架，不允许重复操作"); }
			 * 
			 * if () { return new Result(Result.Type.SUCCESS, "产品[" +
			 * product.getName() + "]下架失败，该产品已售罄"); }
			 */

			if (product.getStatus() != Status.在售) {
				return new Result<String>(Result.Type.SUCCESS, "产品[" + product.getName() + "]下架失败，该产品未上架");
			}

			product.setStatus(Status.下架);
			productDao.save(product);

			LOGGER.info("产品下架成功");
			return new Result<String>(Result.Type.SUCCESS, "产品[" + product.getName() + "]下架成功");
		} catch (Exception e) {
			LOGGER.error("产品下架失败!" + e.toString());
			throw new RuntimeException("产品下架失败!" + e.toString());
		}
	}

	/**
	 * 产品图片预览
	 * 
	 * @author 吴林杰
	 * @param productId	产品ID
	 * @param imgType		图片类型
	 * @param response		http response
	 */
	public void previewProductImage(Long productId, String imgType, HttpServletResponse response) {
		OutputStream outStream = null;
		try {
			Product product = get(productId);
			if (product != null) {
				if (ImageType.valueOf(imgType) == ImageType.封面 && product.getTitleImg() != null) {
					response.reset();
					response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(product.getTitleImgName(), "UTF-8") + "\"");
					response.setContentType("application/octet-stream; charset=uft-8");
					response.setContentLength(product.getTitleImg().length);
					outStream = response.getOutputStream();
					outStream.write(product.getTitleImg());

				} else if (ImageType.valueOf(imgType) == ImageType.海报 && product.getPosterImg() != null) {
					response.reset();
					response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(product.getPosterImgName(), "UTF-8") + "\"");
					response.setContentType("application/octet-stream; charset=uft-8");
					response.setContentLength(product.getPosterImg().length);
					outStream = response.getOutputStream();
					outStream.write(product.getPosterImg());

				}

			}
		} catch (Exception e) {
			LOGGER.error("产品预览失败!" + e.toString());
			throw new RuntimeException("产品图片预览失败!" + e.toString());
		} finally {
			if (outStream != null) {
				try {
					outStream.flush();
					outStream.close();
				} catch (IOException ioe) {
					LOGGER.error("产品图片预览失败!" + ioe.toString());
				}

			}
		}

	}

	/**
	 * 通过计算文件的MD5摘要信息，比较文件内容是否相同
	 * 
	 * @author 吴林杰
	 * @param file1
	 * @param file2
	 * @return true 文件内容相同，false 文件内容不同，文件为空默认false
	 */
	private boolean compareFileContent(byte[] file1, byte[] file2) {
		if (file1 == null || file1.length == 0 || file2 == null || file2.length == 0) {
			return false;
		}
		String md5_file1 = DigestUtils.md5Hex(file1);
		String md5_file2 = DigestUtils.md5Hex(file2);
		if (md5_file1.equals(md5_file2)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 导出产品清单
	 * 
	 * @author 吴林杰
	 * @param request
	 * @param response
	 */
	public void exportProductXls(final ProductVo productVo, HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("开始导出产品信息");
		try {
			
			Map<String,String> headers = new LinkedHashMap<String,String>();

			headers.put("productId", "产品编号");
			headers.put("name", "产品名称");
			headers.put("type", "产品类型");
			headers.put("giftNum", "礼品数量");
			headers.put("availableAmount", "剩余可投");
			headers.put("status", "状态");
			headers.put("createdDateStart", "发布时间");

			List<Product> productList = productDao.findAll(new Specification<Product>() {

				@Override
				public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

					List<Predicate> list = new ArrayList<>();

					// 产品ID
					if (productVo.getProductId() != null) {
						list.add(cb.equal(root.<Serializable> get("id"), productVo.getProductId()));
					}

					// 产品名称
					if (StringUtils.isNotEmpty(productVo.getName())) {
						list.add(cb.like(root.<String> get("name"), "%" + productVo.getName() + "%"));
					}

					// 状态
					if (StringUtils.isNotEmpty(productVo.getStatus()) && !"全部".equals(productVo.getStatus())) {
						list.add(cb.equal(root.<String> get("status"), Product.Status.valueOf(productVo.getStatus())));
					}

					// 产品类型
					if (StringUtils.isNotEmpty(productVo.getType()) && !"全部".equals(productVo.getType())) {
						list.add(cb.equal(root.<String> get("type"), Product.Type.valueOf(productVo.getType()).name()));
					}

					// 统计日期
					if (productVo.getCreatedDateStart() != null) {
						list.add(cb.greaterThanOrEqualTo(root.<Date> get("createdDate"), productVo.getCreatedDateStart()));
					}

					if (productVo.getCreatedDateEnd() != null) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(productVo.getCreatedDateEnd());
						calendar.add(Calendar.DATE, 1);
						calendar.add(Calendar.SECOND, -1);
						list.add(cb.lessThanOrEqualTo(root.<Date> get("createdDate"), calendar.getTime()));
					}

					Predicate[] predicate = new Predicate[list.size()];
					return cb.and(list.toArray(predicate));
				}

			}, new Sort(Direction.fromString("desc"), Arrays.asList("sortNum", "id")));

			List<ProductVo> voList = new ArrayList<ProductVo>();
			if (productList != null) {
				for (Product product : productList) {
					ProductVo vo = new ProductVo();
					vo.setProductId(product.getId());
					vo.setName(product.getName());
					vo.setType(product.getType());
					vo.setGiftNum(product.getGiftNum()!=null?product.getGiftNum():0);
					vo.setAvailableAmount(product.getAvailableAmount());
					vo.setUrl(product.getUrl());
					vo.setStatus(product.getStatus().name());
					vo.setCreatedDateStart(product.getCreatedDate());
					voList.add(vo);
				}
			}

			ExcelUitl.exportExcel("产品信息" + com.snowstore.diana.utils.DateUtils.dateToString(new Date(), "yyMMdd") + ".xlsx", headers, voList, request, response, "yyyy-MM-dd");

		} catch (Exception e) {
			LOGGER.error("产品信息导出异常!" + e.toString());
		}
		LOGGER.info("产品信息导出完成");
	}

	/**
	 * 置顶产品
	 * 
	 * @author wulinjie
	 * @param productId
	 *            产品ID
	 * @return
	 */
	public Result<String> setTop(Long productId) {
		Long maxSortNum = productDao.findMaxSortNum(); // 获取置顶排序字段的最大值
		Product product = productDao.findOne(productId);// 获取产品信息
		product.setSortNum(maxSortNum + 1); // 更新产品置顶排序字段
		productDao.save(product);
		return new Result<String>(Result.Type.SUCCESS, "置顶成功！");
	}

	/**
	 * 取消置顶
	 * 
	 * @author wulinjie
	 * @param productId
	 *            产品ID
	 * @return
	 */
	public Result<String> cancelTop(Long productId) {
		Product product = productDao.findOne(productId);// 获取产品信息
		product.setSortNum(0L); // 更新产品置顶排序字段
		productDao.save(product);
		return new Result<String>(Result.Type.SUCCESS, "取消置顶成功！");
	}

	public List<Product> findByStatusInAndTypeNot(List<Status> status, Type type) {
		return productDao.findByStatusInAndTypeNot(status, type.name());
	}

	public void updateProductFromSingleCertificate(List<Product> list) {
		LOGGER.info("更新理财产品........");
		StringBuilder codes = new StringBuilder();
		for (Product product : list) {
			codes.append(product.getReferenceProduct());
			codes.append(",");
		}
		if (codes.length() <= 0) {
			return;
		}
		FinanceProductReq reqData = new FinanceProductReq();
		reqData.setLevel("2");
		reqData.setPageNum(1);
		reqData.setPageSize(10000);
		reqData.setCodes(codes.substring(0, codes.length() - 1));
		FinanceProductResp result = null;
		try {
			result = jopClient.req(reqData);
		} catch (BusinessException e) {
			LOGGER.error("调用开放平台出错", e);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		if (null == result) {
			LOGGER.info("没有新的理财产品........");
			return;
		}
		List<FinanProductVo> proList = result.getContent();
		for (FinanProductVo finanProductVo : proList) {
			try {
				add(finanProductVo);
			} catch (Exception e) {
				LOGGER.info("finanProductVo:" + finanProductVo.getId());
				LOGGER.error("保存理财产品出错", e);
			}
		}
	}

	/**
	 * 保存产品信息
	 * @author wulinjie
	 * @param product
	 */
	public void saveOrUpdate(Product product){
		productDao.save(product);
	}
	
	public Page<Product> loadByType(List<String> type,Pageable page){
		List<Status> status = new ArrayList<Status>();
		status.add(Product.Status.在售);
		status.add(Product.Status.售罄);
		return productDao.findByStatusInAndTypeIn(status, type, page);
	}
	
	@Resource(name = "redisTemplate")
	private ValueOperations<Long, ImgVo> titleImg;
	
	/**
	 * 初始化图片缓存
	 */
	@PostConstruct
	public void initProductImage(){
		Iterable<Product> iterable = productDao.findAll();
		for (Product product : iterable) {
			try {
				initProductImage(product.getId());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
	
	public ImgVo initProductImage(Long id){
		Product product = productDao.findOne(id);
		ImgVo vo = new ImgVo();
		vo.setContent(product.getTitleImg());
		vo.setName(product.getTitleImgName());
		vo.setType("titleImg");
		int index = product.getTitleImgName().lastIndexOf(".");
		vo.setSuffix(product.getTitleImgName().substring(index+1, product.getTitleImgName().length()));
		titleImg.getAndSet(product.getId(), vo);
		return vo;
	}
}
