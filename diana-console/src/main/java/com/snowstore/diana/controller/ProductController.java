package com.snowstore.diana.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.snowstore.diana.domain.Product;
import com.snowstore.diana.service.ProductService;
import com.snowstore.diana.vo.PageFormVo;
import com.snowstore.diana.vo.PageTables;
import com.snowstore.diana.vo.ProductVo;
import com.snowstore.diana.vo.Result;
import com.snowstore.log.annotation.UserLog;

@Controller
@RequestMapping(value = "/product")
public class ProductController extends BaseController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private ProductService productService;

	@RequestMapping("/productList")
	@UserLog(remark = "【产品管理】返回产品信息列表页面")
	public String productList(Model model) {
		model.addAttribute("statusList", Product.Status.values());
		model.addAttribute("typeList", Product.Type.values());
		return "productList";
	}

	/**
	 * 查询产品
	 * 
	 * @author wulinjie
	 * @param draw
	 * @param start
	 * @param length
	 * @return
	 */
	@RequestMapping("/loadProductList")
	@ResponseBody
	@UserLog(remark = "【产品管理】后台获取产品信息列表")
	public PageTables<Product> loadProductList(ProductVo productVo, Integer draw, Integer start, Integer length) {
		LOGGER.info("理财产品查询....");
		PageFormVo pageFormVo = new PageFormVo();
		pageFormVo.setPage(start / 10 + 1);
		pageFormVo.setRows(length);
		pageFormVo.setStart(start);
		pageFormVo.setLength(length);
		pageFormVo.setDraw(draw);
		Sort sort = new Sort(Direction.fromString("desc"),Arrays.asList("sortNum","id"));
		pageFormVo.setSortSet(sort);

		Page<Product> page = productService.loadByCondition(productVo, pageFormVo);

		// 封装分页对象数据
		PageTables<Product> pageTables = new PageTables<Product>();
		List<Product> productList = page.getContent();
		pageTables.setDraw(draw);
		pageTables.setRecordsTotal(page.getTotalElements());
		pageTables.setRecordsFiltered(page.getTotalElements());
		pageTables.setData(productList);
		return pageTables;
	}

	/**
	 * 编辑产品
	 * 
	 * @author wulinjie
	 * @param productId
	 *            产品ID
	 * @return
	 */
	@RequestMapping("/productEdit")
	@UserLog(remark = "【产品管理】后台编辑产品信息")
	public String productEdit(Long productId, Model model,VelocityContext context) {
		productService.editProduct(productId, model);
		//context.put("typeList", Product.Type.class);
		return "productEdit";
	}

	/**
	 * 保存产品
	 * 
	 * @author wulinjie
	 * @param product
	 *            产品
	 * @return 处理结果
	 */
	@RequestMapping("/productSave")
	@ResponseBody
	@UserLog(remark = "【产品管理】后台保存产品信息")
	public Result<String> productSave(ProductVo vo, MultipartFile titleImg, MultipartFile posterImg) {
		productService.saveProduct(vo, titleImg, posterImg);
		return new Result<String>(Result.Type.SUCCESS, "保存成功");
	}

	/**
	 * 下架产品
	 * 
	 * @author 吴林杰
	 * @param productId
	 * @return
	 */
	@RequestMapping("/productOffShelve/{productId}")
	@ResponseBody
	@UserLog(remark = "【产品管理】下架产品")
	public Result<String> productOffShelve(@PathVariable("productId") Long productId) {
		return productService.offShelve(productId);
	}

	/**
	 * 预览产品图片
	 * 
	 * @author 吴林杰
	 * @param productId
	 * @param imgType
	 * @param response
	 */
	@RequestMapping("/productImgPreview/{productId}")
	@UserLog(remark = "【产品管理】预览产品图片")
	public void productImgPreview(@PathVariable("productId") Long productId, String imgType, HttpServletResponse response) {
		productService.previewProductImage(productId, imgType, response);
	}
	
	/**
	 * 导出产品信息
	 * @author 吴林杰
	 * @param productVo	产品VO
	 * @param request
	 * @param response
	 */
	@RequestMapping("/productExcelExport")
	@UserLog(remark = "【产品管理】导出产品列表")
	public void productExcelExport(ProductVo productVo, HttpServletRequest request, HttpServletResponse response){
		productService.exportProductXls(productVo, request, response);
	}
	
	/**
	 * 置顶产品
	 * @author wulinjie
	 * @param productId	产品ID
	 * @return
	 */
	@RequestMapping("/productSetTop")
	@ResponseBody
	@UserLog(remark = "【产品管理】置顶产品")
	public Result<String> productSetTop(Long productId){
		return productService.setTop(productId);
	}
	
	/**
	 * 取消置顶产品
	 * @author wulinjie
	 * @param productId	产品ID
	 * @return
	 */
	@RequestMapping("/productCancelTop")
	@ResponseBody
	@UserLog(remark = "【产品管理】取消置顶产品")
	public Result<String> productCancelTop(Long productId){
		return productService.cancelTop(productId);
	}

}
