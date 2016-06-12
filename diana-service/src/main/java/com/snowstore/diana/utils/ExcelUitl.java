package com.snowstore.diana.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.snowstore.diana.common.Constants;
/**
 * 
 * Excel 操作工具类
 * 
 * @author: fuhongxing
 * @date:   2015年8月10日
 * @version 1.0.0
 * @Description:
 * 
 */
public class ExcelUitl {
	private static Logger LOGGER = LoggerFactory.getLogger(ExcelUitl.class);
	/**
	 *  HSSF － 提供读写Microsoft Excel XLS格式档案的功能。　　
		XSSF － 提供读写Microsoft Excel OOXML XLSX格式档案的功能。　　
		HWPF － 提供读写Microsoft Word DOC格式档案的功能。　　
		HSLF － 提供读写Microsoft PowerPoint格式档案的功能。　　
		HDGF － 提供读Microsoft Visio格式档案的功能。　　
		HPBF － 提供读Microsoft Publisher格式档案的功能。　　
		HSMF － 提供读Microsoft Outlook格式档案的功能。
	 */
	
	
	 /** 
     * 1.创建 workbook 
     * @return 
     */  
    public static HSSFWorkbook getHSSFWorkbook(){  
        return new HSSFWorkbook();  
    }  
  
    /** 
     * 2.创建 sheet 
     * @param workbook 
     * @param sheetName sheet 名称 
     * @return 
     */  
    public static Sheet getHSSFSheet(Workbook workbook, String sheetName){  
        return workbook.createSheet(sheetName);
    } 
	
	/** 
     * 3.写入表头信息 
     * @param hssfWorkbook 
     * @param hssfSheet 
     * @param header excel标题栏
     */  
    @SuppressWarnings("static-access")
	public void writeHeader(HSSFWorkbook hssfWorkbook,HSSFSheet hssfSheet ,List<String> header){  
        HSSFCellStyle hSSFCellStyle = hssfWorkbook.createCellStyle();  // 样式对象 
        HSSFFont font = hssfWorkbook.createFont();  
        font.setFontHeightInPoints((short)12);  
        font.setBoldweight(font.BOLDWEIGHT_BOLD);  
        hSSFCellStyle.setFont(font);  
        hSSFCellStyle.setAlignment(hSSFCellStyle.ALIGN_CENTER);  // 设置单元格水平居中对齐
        hSSFCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 指定单元格垂直居中对齐
        HSSFRow row = hssfSheet.createRow(0);  //创建行
        row.setHeight((short) 380);  
        HSSFCell cell = null;  
        
        //设置表头
		for (int i = 0; i < header.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(header.get(i));
			cell.setCellStyle(hSSFCellStyle);
			//sheet.autoSizeColumn(i);
			hssfSheet.setColumnWidth(i, 5000);//设置列宽度
		}
    }  
    

	/**
	 * 写入到文件流
	 * @param os
	 * @param workbook
	 * @return
	 */
	private static boolean write(HttpServletRequest request , HttpServletResponse response , XSSFWorkbook wb,String fileName ,Integer total) {
		boolean isSuccess = false;
		OutputStream outputStream = null;
		//获取客户端浏览器和操作系统信息  
		//Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko --->IE11
		//Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.149 Safari/537.36 --->谷歌
		//Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0--->火狐
		String userAgent = request.getHeader("USER-AGENT");
		try {
			String finalFileName = null;
			//fileName = fileName + DateUtils.dateToString(new Date(), "yyyy-MM-dd")+".xlsx";
			 //解决文件名乱码
			 if(org.apache.commons.lang3.StringUtils.contains(userAgent, "MSIE") || org.apache.commons.lang3.StringUtils.contains(userAgent, "11.0")){//IE浏览器
	                finalFileName = URLEncoder.encode(fileName,"UTF8");
	          }else if(org.apache.commons.lang3.StringUtils.contains(userAgent, "Mozilla")){//google,火狐浏览器
	             finalFileName = new String(fileName.getBytes("utf-8"), "ISO8859-1");
	          }else{
	              finalFileName = URLEncoder.encode(fileName,"UTF8");//其他浏览器
	         }
			 //设置让浏览器弹出下载提示框，而不是直接在浏览器中打开
			response.setHeader("Content-Disposition", "attachment;filename="+finalFileName);
			response.setContentType("application/ynd.ms-excel;charset=UTF-8");
			outputStream = response.getOutputStream();
			wb.write(outputStream);
			LOGGER.info(fileName+total+"条");
		} catch (IOException e) {
			isSuccess = true;
			LOGGER.error("excel写入文件异常",e.getMessage());
		} finally {
			try {
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage(),e.getMessage());
			}
		}
		return isSuccess;
	}
	
	
	
	/** 
     * 利用JAVA的反射机制，导出excel文件
	 * @param <T>
	 * @param <T>
	 * @param <T>
     *  
     * @param fileName 
     *            文件名
     * @param headers 
     *            表格属性列名数组 
     * @param dataset 
     *            数据内容
     * @param out 
     *            文件输出流
     * @param pattern 
     *            设定时间类型输出格式。默认为"yyy-MM-dd" 
     */  
	public static <T> boolean exportExcel(String fileName, String[] headers, Collection<T> dataset, HttpServletRequest request, HttpServletResponse response , String pattern) throws Exception {
		// 声明一个工作薄
		XSSFWorkbook workbook = new XSSFWorkbook();
		// 生成一个表格
		XSSFSheet sheet = workbook.createSheet(fileName);
		// 设置表格默认列宽度为20个字节
		sheet.setDefaultColumnWidth((short) 20);
		// 生成一个样式
		XSSFCellStyle style = workbook.createCellStyle();
		// 设置样式
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		XSSFCell cell = null;
		// 设置表格标题行
		XSSFRow row = sheet.createRow(0);
		for (short i = 0; i < headers.length; i++) {
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(headers[i]);
		}

		// 遍历集合数据，设置数据内容
		Iterator<T> it = dataset.iterator();
		int index = 0;
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			T t = it.next();
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			Field[] fields = t.getClass().getDeclaredFields();
			int i=0;
			for (Field field:fields) {
				if(i==headers.length){
					break;
				}
				String fieldName = field.getName();
				//序列化ID不需要输出
				if(Constants.SERIAL_VERSION_UID.equals(fieldName)){
					continue;
				}
				cell = row.createCell(i++);//创建单元格
				cell.setCellStyle(style);
				//拼接get方法名
				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				Class<? extends Object> tCls = t.getClass();
				Method getMethod = tCls.getMethod(getMethodName, new Class[] {});//获取方法
				Object value = getMethod.invoke(t, new Object[] {});//调用方法返回值
				// 判断值的类型后进行强制类型转换
				if (value instanceof Date) {
					Date date = (Date) value;
					SimpleDateFormat format = new SimpleDateFormat(pattern);
					if(!StringUtils.isEmpty(value)){
						cell.setCellValue(format.format(date));
					}
				} else {
					// 其它数据类型都当作字符串简单处理
					if(!StringUtils.isEmpty(value)){
						cell.setCellValue(value.toString());
					}
				}
			}
		}
		//写入文件流
		boolean isSuccess = write(request, response , workbook ,fileName,dataset.size());
		return isSuccess;
    } 
	
	/** 
     * 利用JAVA的反射机制，导出excel文件
	 * @param <T>
	 * @param <T>
	 * @param <T>
     *  
     * @param fileName 
     *            文件名
     * @param headers 
     *            表格属性列名数组 
     * @param dataset 
     *            数据内容
     * @param out 
     *            文件输出流
     * @param pattern 
     *            设定时间类型输出格式。默认为"yyy-MM-dd" 
     */  
	public static <T> boolean exportExcel(String fileName, Map<String,String> headers, Collection<T> dataset, HttpServletRequest request, HttpServletResponse response , String pattern) throws Exception {
		// 声明一个工作薄
		XSSFWorkbook workbook = new XSSFWorkbook();
		// 生成一个表格
		XSSFSheet sheet = workbook.createSheet(fileName);
		// 设置表格默认列宽度为20个字节
		sheet.setDefaultColumnWidth((short) 20);
		// 生成一个样式
		XSSFCellStyle style = workbook.createCellStyle();
		// 设置样式
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		XSSFCell cell = null;
		// 设置表格标题行
		XSSFRow row = sheet.createRow(0);
		Set<String> headersSet = headers.keySet();
		int headerIndex = 0;
		for (String header : headersSet) {
			cell = row.createCell(headerIndex);
			cell.setCellStyle(style);
			cell.setCellValue(headers.get(header));
			headerIndex++;
		}

		// 遍历集合数据，设置数据内容
		Iterator<T> it = dataset.iterator();
		int index = 0;
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			T t = it.next();
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			int i=0;
			for (String header : headersSet) {
				String fieldName = header;
				//序列化ID不需要输出
				if(Constants.SERIAL_VERSION_UID.equals(fieldName)){
					continue;
				}
				cell = row.createCell(i++);//创建单元格
				cell.setCellStyle(style);
				//拼接get方法名
				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				Class<? extends Object> tCls = t.getClass();
				Method getMethod = tCls.getMethod(getMethodName, new Class[] {});//获取方法
				Object value = getMethod.invoke(t, new Object[] {});//调用方法返回值
				// 判断值的类型后进行强制类型转换
				if (value instanceof Date) {
					Date date = (Date) value;
					SimpleDateFormat format = new SimpleDateFormat(pattern);
					if(!StringUtils.isEmpty(value)){
						cell.setCellValue(format.format(date));
					}
				} else {
					// 其它数据类型都当作字符串简单处理
					if(!StringUtils.isEmpty(value)){
						cell.setCellValue(value.toString());
					}
				}
			}
		}
		//写入文件流
		boolean isSuccess = write(request, response , workbook ,fileName,dataset.size());
		return isSuccess;
    }  
	
	
}
