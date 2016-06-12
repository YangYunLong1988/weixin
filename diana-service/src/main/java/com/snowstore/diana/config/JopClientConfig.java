package com.snowstore.diana.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.snowstore.diana.common.DianaConstants;
import com.snowstore.diana.common.Files;
import com.snowstore.poseidon.client.JopClient;

@Configuration
public class JopClientConfig {

	@Bean
	public JopClient client(){
		JopClient jopClien = new JopClient(DianaConstants.ATLANTIS_URL_PREFIX, 
				Files.getInputStream(System.getProperty("diana.certificate")+ "/diana.p12"),
				StringUtils.trim(System.getProperty("diana.certificate.keyStorePassword")),
				Files.getInputStream(System.getProperty("diana.certificate")+"/server.truststore"),
				StringUtils.trim(System.getProperty("diana.certificate.trustKeySotrePassword")));
		return jopClien;
		
	}
}
