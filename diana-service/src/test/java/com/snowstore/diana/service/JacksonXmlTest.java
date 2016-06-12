package com.snowstore.diana.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.jaxb.XmlJaxbAnnotationIntrospector;
import com.snowstore.diana.vo.logistics.Item;
import com.snowstore.diana.vo.logistics.Receiver;
import com.snowstore.diana.vo.logistics.RequestOrder;
import com.snowstore.diana.vo.logistics.RequestOrders;
import com.snowstore.diana.vo.logistics.UpdateInfos;

public class JacksonXmlTest {
	@Test
	public void beanToXml() throws JsonProcessingException {
		RequestOrders requestOrders = new RequestOrders();
		RequestOrder requestOrder = new RequestOrder();
		requestOrder.setExpressCode("abc");
		requestOrder.setItemsValue("123");
		requestOrder.setMemberNO("5555");
		requestOrder.setOrderAmount("55555");
		requestOrder.setPayment("payment");
		requestOrder.setRemark("备注");
		requestOrder.setSaleOrderCode("224411");
		requestOrder.setTradeId("1");
		requestOrder.setWareHouseCode("234");
		Item item = new Item();
		item.setItemCount("1");
		item.setItemName("袜子");
		item.setItemValue("100");
		item.setSendType("123");
		item.setSpuCode("566666666666666666666666");
		item.setSendType("send");
		requestOrder.getItems().add(item);

		Receiver re = new Receiver();
		re.setAddress("中油 阳光");
		re.setCity("上海");
		re.setCounty("上海");
		re.setMobilePhone("18610522144");
		re.setName("高叔");
		re.setPostCode("123456");
		re.setProvince("湖南");
		requestOrder.setReceiver(re);

		requestOrders.getRequestOrders().add(requestOrder);

		requestOrder = new RequestOrder();
		requestOrder.setExpressCode("abc");
		requestOrder.setItemsValue("123");
		requestOrder.setMemberNO("5555");
		requestOrder.setOrderAmount("55555");
		requestOrder.setPayment("payment");
		requestOrder.setRemark("备注");
		requestOrder.setSaleOrderCode("224411");
		requestOrder.setTradeId("1");
		requestOrder.setWareHouseCode("234");
		item = new Item();
		item.setItemCount("1");
		item.setItemName("袜子");
		item.setItemValue("100");
		item.setSendType("123");
		item.setSpuCode("566666666666666666666666");
		item.setSendType("send");
		requestOrder.getItems().add(item);

		re = new Receiver();
		re.setAddress("中油阳光");
		re.setCity("上海");
		re.setCounty("上海");
		re.setMobilePhone("18610522144");
		re.setName("高叔");
		re.setPostCode("123456");
		re.setPhone("444");
		re.setProvince("湖南");
		requestOrder.setReceiver(re);

		// requestOrders.getRequestOrders().add(requestOrder);

		// and then configure, for example:
		XmlMapper xmlMapper = new XmlMapper();
		AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
		AnnotationIntrospector secondary = new XmlJaxbAnnotationIntrospector(xmlMapper.getTypeFactory());
		AnnotationIntrospector pair = AnnotationIntrospector.pair(primary, secondary);
		xmlMapper.setAnnotationIntrospector(pair);
		System.out.println(xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestOrders));
	}

	@Test
	public void testFawang() throws ClientProtocolException, IOException {
		RequestOrders requestOrders = new RequestOrders();
		RequestOrder requestOrder = new RequestOrder();
		requestOrder.setExpressCode("abc");
		requestOrder.setItemsValue("123");
		requestOrder.setMemberNO("0210020000");
		requestOrder.setOrderAmount("55555");
		requestOrder.setPayment("100");
		requestOrder.setRemark("memo");
		requestOrder.setSaleOrderCode("1");
		requestOrder.setTradeId("1");
		requestOrder.setWareHouseCode("1009");
		Item item = new Item();
		item.setItemCount("1");
		item.setItemName("袜子");
		item.setItemValue("100");
		item.setSpuCode("");
		item.setSendType("send");
		requestOrder.getItems().add(item);

		Receiver re = new Receiver();
		re.setAddress("浦东新区东方路969号");
		re.setCity("上海");
		re.setCounty("上海");
		re.setMobilePhone("18610522144");
		re.setName("高叔");
		re.setPostCode("123456");
		re.setPhone("444");
		re.setProvince("湖北");
		requestOrder.setReceiver(re);

		requestOrders.getRequestOrders().add(requestOrder);
		// and then configure, for example:
		XmlMapper xmlMapper = new XmlMapper();
		AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
		AnnotationIntrospector secondary = new XmlJaxbAnnotationIntrospector(xmlMapper.getTypeFactory());
		AnnotationIntrospector pair = AnnotationIntrospector.pair(primary, secondary);
		xmlMapper.setAnnotationIntrospector(pair);
		String xml = xmlMapper.writeValueAsString(requestOrders);
		String digest = DigestUtils.md5Hex(xml + "123321").toUpperCase();
		String requestString = "data_Isdigest=Y&logistics_interface=" + xml + "&data_digest=" + digest;
		System.out.println(requestString);

		String resultString = Request.Post("http://api.fineex.net:801/Interface/InterfaceWeb/InterfaceStandard.ashx").bodyString(requestString, ContentType.create("application/x-www-form-urlencoded", Charset.forName("GBK"))).execute().returnContent().asString();
		System.out.println(resultString);
	}

	@Test
	public void testEncode() throws UnsupportedEncodingException {
		String src = "<RequestOrders><RequestOrder><MemberNO>中午</MemberNO><WareHouseCode>234</WareHouseCode><SaleOrderCode>224411</SaleOrderCode><ExpressCode>abc</ExpressCode><Receiver><Name>高叔</Name><PostCode>123456</PostCode><Phone>444</Phone><MobilePhone>18610522144</MobilePhone><Province>湖南</Province><city>上海</city><County>上海</County><Address>中油阳光</Address></Receiver><ItemsValue>123</ItemsValue><OrderAmount>55555</OrderAmount><Payment>payment</Payment><Items><Item><SendType>send</SendType><SpuCode>566666666666666666666666</SpuCode><ItemName>袜子</ItemName><ItemCount>1</ItemCount><ItemValue>100</ItemValue></Item></Items><Remark>备注</Remark><TradeId>1</TradeId></RequestOrder></RequestOrders>";
		System.out.println(URLEncoder.encode(src, "GBK"));
	}

	@Test
	public void testdecode() throws UnsupportedEncodingException {
		String src = "&lt;UpdateInfos&gt;&lt;UpdateInfo&gt;&lt;MemberNO&gt;0210020000&lt;/MemberNO&gt;&lt;WareHouseCode&gt;1009&lt;/WareHouseCode&gt;&lt;SaleOrderCode&gt;224415&lt;/SaleOrderCode&gt;&lt;CarryCode&gt;201601180002&lt;/CarryCode&gt;&lt;ExpressCode&gt;SFEXPRESS-顺丰&lt;/ExpressCode&gt;&lt;InfoType&gt;STATUS&lt;/InfoType&gt;&lt;InfoContent&gt;SEND&lt;/InfoContent&gt;&lt;Remark /&gt;&lt;TrackDate&gt;2016-01-18 14:18:14&lt;/TrackDate&gt;&lt;Weight&gt;1.00&lt;/Weight&gt;&lt;ArriveTime&gt;2016-01-19 14:18:14&lt;/ArriveTime&gt;&lt;/UpdateInfo&gt;&lt;/UpdateInfos&gt;";
		System.out.println(StringEscapeUtils.unescapeHtml4(src));
	}

	@Test
	public void readFromXml() throws JsonParseException, JsonMappingException, IOException {
		String src = "<UpdateInfos><UpdateInfo><MemberNO>ANNTO</MemberNO><SaleOrderCode>2010062133573</SaleOrderCode><CarryCode>8862234422</CarryCode><InfoType>STATUS</InfoType><InfoContent>SEND </InfoContent><ExpressCode>ZJS-宅急送</ExpressCode><Remark>备注</Remark><Weight>重量</Weight></UpdateInfo><UpdateInfo><MemberNO>ANNTO</MemberNO><SaleOrderCode>2010062133573</SaleOrderCode><CarryCode>8862234422</CarryCode><InfoType>STATUS</InfoType><InfoContent>FAILED</InfoContent><ExpressCode>ZJS-宅急送</ExpressCode><Remark>备注</Remark><Weight>重量</Weight></UpdateInfo><UpdateInfo><MemberNO>ANNTO</MemberNO><SaleOrderCode>2010062133573</SaleOrderCode><CarryCode>8862234422</CarryCode><infoType>STATUS</infoType><InfoContent>ABNORMAL</InfoContent><ExpressCode>ZJS-宅急送</ExpressCode><Remark>收货人联系不上</Remark><Weight>重量</Weight><unkown>a</unkown></UpdateInfo></UpdateInfos>";
		XmlMapper xmlMapper = new XmlMapper();
		AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
		AnnotationIntrospector secondary = new XmlJaxbAnnotationIntrospector(xmlMapper.getTypeFactory());
		AnnotationIntrospector pair = AnnotationIntrospector.pair(primary, secondary);
		xmlMapper.setAnnotationIntrospector(pair);
		UpdateInfos updateInfos = xmlMapper.readValue(src, UpdateInfos.class);
		Assert.assertEquals(3, updateInfos.getUpdateInfos().size());
		Assert.assertEquals("ANNTO", updateInfos.getUpdateInfos().get(0).getMemberNO());
		Assert.assertEquals("备注", updateInfos.getUpdateInfos().get(0).getRemark());
	}

}
