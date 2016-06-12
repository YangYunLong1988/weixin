package com.snowstore.diana.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

	/**
	 * GET方式提交请求
	 * @param url 请求地址
	 * @return 响应结果
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String sendRequest(String url) throws MalformedURLException, IOException {
		StringBuffer sb = new StringBuffer();
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(30000);
		conn.setUseCaches(false);
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String str = null;
		while ((str = reader.readLine()) != null) {
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * POST方式提交请求
	 * @param url   请求地址
	 * @param body  请求报文
	 * @return
	 * @throws IOException
	 */
	public static String sendPostRequest(String url, String body) throws IOException {
		StringBuffer sb = new StringBuffer();
		HttpURLConnection conn;
		BufferedReader reader = null;
		OutputStream os = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(30000);
			if (StringUtils.isNotEmpty(body)) {
				os = conn.getOutputStream();
				os.write(body.getBytes("UTF-8"));
			}
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
			String str = null;
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(os);
		}
	}

	/**
	 *
	 * @param utfString
	 * @return
	 */
	public static String convert(String utfString){
		StringBuilder sb = new StringBuilder();
		int i = -1;
		int pos = 0;
		
		while((i=utfString.indexOf("\\u", pos)) != -1){
			sb.append(utfString.substring(pos, i));
			if(i+5 < utfString.length()){
				pos = i+6;
				sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));
			}
		}
		
		return sb.toString();
	}

	/**
	 * 获取序列化后的参数列表
	 * @author wulinjie
	 * @return
	 */
	public static String getSerializeParams(Map<String, String> params){
		Object[] objs = params.keySet().toArray();
		Arrays.sort(objs);
		StringBuffer buffer = new StringBuffer();
		for( int i = 0; i < objs.length ; i++ ){
			buffer.append(objs[i].toString()).append("=").append(params.get(objs[i])).append("&");
		}
		String URI = buffer.toString();
		URI = URI.substring(0, URI.length()-1);
		return URI;
	}

	/**
	 * 发送POST请求
	 * @param url		请求地址
	 * @param params	报文参数
	 * @return
	 */
	public static String post(String url, Map<String, String> params){
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		String body = null;

		LOGGER.info("create Http Post : " + url);
		HttpPost post = postForm(url, params);

		body = invoke(httpclient, post);

		try {
			httpclient.close();
		} catch (IOException e) {
			LOGGER.error("post异常", e);
		}

		return body;
	}

	/**
	 * 发送GET请求
	 * @param url 请求地址
	 * @return
	 */
	public static String get(String url) {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		String body = null;

		LOGGER.info("create Http Get : " + url);
		HttpGet get = new HttpGet(url);
		body = invoke(httpclient, get);

		try {
			httpclient.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return body;
	}

	/**
	 * 执行请求
	 * @author wulinjie
	 * @param httpclient
	 * @param httpost
	 * @return
	 */
	private static String invoke(CloseableHttpClient httpclient, HttpUriRequest httpost) {
		CloseableHttpResponse response = sendRequest(httpclient, httpost);
		String body = parseResponse(response);
		return body;
	}

	/**
	 * 解析响应结果
	 * @author wulinjie
	 * @param response http响应
	 * @return
	 */
	private static String parseResponse(CloseableHttpResponse response) {
		LOGGER.info("get response from http server..");
		HttpEntity entity = response.getEntity();

		LOGGER.info("response status: " + response.getStatusLine());
		ContentType contentType = ContentType.getOrDefault(entity);
		Charset charset = contentType.getCharset();
		LOGGER.info(charset.toString());

		String body = null;
		try {
			body = EntityUtils.toString(entity);
			LOGGER.info(body);
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return body;
	}

	/**
	 * 发送请求
	 * @author wulinjie
	 * @param httpclient
	 * @param httpost
	 * @return
	 */
	private static CloseableHttpResponse sendRequest(CloseableHttpClient httpclient, HttpUriRequest httpost) {
		LOGGER.info("execute post...");
		CloseableHttpResponse response = null;

		try {
			response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
			LOGGER.error("ClientProtocolException",e);
		} catch (IOException e) {
			LOGGER.error("IOException",e);
		}
		return response;
	}

	/**
	 * 初始化POST请求参数
	 * @author wulinjie
	 * @param url		地址
	 * @param params	参数
	 * @return
	 */
	private static HttpPost postForm(String url, Map<String, String> params){

		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		Set<String> keySet = params.keySet();
		for(String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}

		LOGGER.info("set utf-8 form entity to httppost");
		httpost.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));

		return httpost;
	}

}