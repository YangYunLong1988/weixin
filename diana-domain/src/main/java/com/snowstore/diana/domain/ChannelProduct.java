package com.snowstore.diana.domain;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Created by wulinjie on 2015/12/31.
 */
@Entity
@Table(name = "diana_channel_product",uniqueConstraints=@UniqueConstraint(columnNames = { "channelId","productType"}))
@EntityListeners(AuditingEntityListener.class)
public class ChannelProduct extends AbstractEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4814768047912339564L;

	private Long channelId;		//渠道ID

	private String productType;	//产品类型

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
}
