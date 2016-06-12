package com.snowstore.diana.vo.logistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RequestOrders")
@XmlAccessorType(XmlAccessType.FIELD)
public class RequestOrders implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "RequestOrder")
	private List<RequestOrder> RequestOrders = new ArrayList<RequestOrder>();

	public List<RequestOrder> getRequestOrders() {
		return RequestOrders;
	}

	public void setRequestOrders(List<RequestOrder> requestOrders) {
		RequestOrders = requestOrders;
	}

}
