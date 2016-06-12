package com.snowstore.diana.service.logistics;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.jaxb.XmlJaxbAnnotationIntrospector;
import com.snowstore.diana.domain.Logistics;
import com.snowstore.diana.domain.Logistics.LogisticsStatus;
import com.snowstore.diana.domain.Order;
import com.snowstore.diana.domain.Package;
import com.snowstore.diana.domain.UserGift;
import com.snowstore.diana.repository.LogisticsDao;
import com.snowstore.diana.repository.PackageDao;
import com.snowstore.diana.repository.UserGiftDao;
import com.snowstore.diana.vo.logistics.Item;
import com.snowstore.diana.vo.logistics.Receiver;
import com.snowstore.diana.vo.logistics.RequestOrder;
import com.snowstore.diana.vo.logistics.RequestOrders;
import com.snowstore.diana.vo.logistics.Response;
import com.snowstore.diana.vo.logistics.UpdateInfo;
import com.snowstore.diana.vo.logistics.UpdateInfos;

@Service
@Transactional
public class LogisticsService {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Value("${fawang.endpoint.memberno:0210020000}")
	private String fawangtEndpointMenberNo;
	@Value("${fawang.endpoint.url:http://api.fineex.net:801/Interface/InterfaceWeb/InterfaceStandard.ashx}")
	private String fawangEndpointUrl;
	@Value("${fawang.endpoint.security:123321}")
	private String fawangEndpointSecurity;
	@Value("${fawang.endpoint.ware-house-code:1009}")
	private String fawangEndpointWareHouseCode;

	@Autowired
	private LogisticsDao logisticsDao;

	@Autowired
	private PackageDao packageDao;

	@Autowired
	private UserGiftDao userGiftDao;

	public void deliver(Order order) {
		Logistics logistics = new Logistics();
		logistics.setOrder(order);
		deliverOut(logistics);
		logisticsDao.save(logistics);
	}

	/*
	 * 调用发网接口
	 */
	public Logistics deliverOut(Logistics logistics) {
		LOGGER.info("调用发网[发货单接口]开始,订单号:" + logistics.getOrder().getId());
		RequestOrders requestOrders = new RequestOrders();
		RequestOrder requestOrder = new RequestOrder();
		requestOrder.setExpressCode("");
		requestOrder.setItemsValue(logistics.getOrder().getAmount().toPlainString());
		requestOrder.setMemberNO(fawangtEndpointMenberNo);
		requestOrder.setOrderAmount(logistics.getOrder().getAmount().toPlainString());
		requestOrder.setPayment("0");
		requestOrder.setRemark("");
		requestOrder.setSaleOrderCode(logistics.getOrder().getId().toString());
		requestOrder.setTradeId(logistics.getOrder().getId().toString());
		requestOrder.setWareHouseCode(fawangEndpointWareHouseCode);

		List<UserGift> giftlist = userGiftDao.findByOrderId(logistics.getOrder().getId());
		if (CollectionUtils.isEmpty(giftlist)) {
			throw new RuntimeException("没有礼品");
		}
		UserGift userGift = giftlist.get(0);
		Item item = new Item();
		item.setItemCount("1");
		item.setItemName(userGift.getGiftName());
		item.setItemValue(logistics.getOrder().getAmount().toPlainString());
		item.setSendType("Send");
		item.setSpuCode(userGift.getSpuCode());
		requestOrder.getItems().add(item);
		List<Package> packages = packageDao.findPackageByOrderId(logistics.getOrder().getId());
		Package package1 = null;
		if (packages != null && packages.size() > 0) {
			package1 = packages.get(0);
		}
		if (package1 == null) {
			package1 = new Package();
		}
		Receiver re = new Receiver();
		re.setAddress(StringUtils.remove(package1.getAddress(), " "));
		re.setCity(package1.getCity());
		re.setCounty(package1.getArea());
		re.setMobilePhone(package1.getMobile());
		re.setName(package1.getRecipients());
		re.setPostCode("");
		re.setProvince(package1.getProvince());
		requestOrder.setReceiver(re);

		requestOrders.getRequestOrders().add(requestOrder);
		// send data to fawang
		XmlMapper xmlMapper = new XmlMapper();
		AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
		AnnotationIntrospector secondary = new XmlJaxbAnnotationIntrospector(xmlMapper.getTypeFactory());
		AnnotationIntrospector pair = AnnotationIntrospector.pair(primary, secondary);
		xmlMapper.setAnnotationIntrospector(pair);
		String xml;
		try {
			xml = xmlMapper.writeValueAsString(requestOrders);

			String digest = DigestUtils.md5Hex(xml + fawangEndpointSecurity).toUpperCase();
			String requestString = "data_Isdigest=Y&logistics_interface=" + xml + "&data_digest=" + digest;
			LOGGER.info("reqeust: " + requestString);
			String resultString = Request.Post(fawangEndpointUrl).bodyString(requestString, ContentType.create("application/x-www-form-urlencoded", Charset.forName("GBK"))).execute().returnContent().asString();
			LOGGER.info("response: " + resultString);
			Response response = xmlMapper.readValue(resultString, Response.class);
			if (!response.getSuccess()) {
				logistics.setStatus(null);
				logistics.setMemo(response.getReason());
			}
		} catch (IOException e) {
			LOGGER.warn(e.getMessage(), e);
			logistics.setStatus(null);
			logistics.setMemo(e.getMessage());
		}
		LOGGER.info("调用发网[发货单接口]结束,订单号:" + logistics.getOrder().getId());
		return logistics;
	}

	public void updateInfos(UpdateInfos updateInfos) {
		for (UpdateInfo updateInfo : updateInfos.getUpdateInfos()) {
			Logistics logistics = logisticsDao.findOneByOrderId(Long.valueOf(updateInfo.getSaleOrderCode()));
			if (logistics == null) {
				throw new RuntimeException("B13[订单不存在]");
			}
			if (updateInfo.getExpressCode().contains("-")) {
				logistics.setCompany(updateInfo.getExpressCode().split("-")[1]);
			} else {
				logistics.setCompany(updateInfo.getExpressCode());
			}
			logistics.setSn(updateInfo.getCarryCode());
			if (updateInfo.getInfoContent().contains("PACKAGED")) {
				logistics.setStatus(LogisticsStatus.拣货中);
			} else {
				logistics.setStatus(LogisticsStatus.已发货);
			}
			logistics.setMemo(updateInfo.getRemark());
		}

	}

	public Logistics findOneByOrderId(Long orderId) {
		return logisticsDao.findOneByOrderId(orderId);
	}
}
