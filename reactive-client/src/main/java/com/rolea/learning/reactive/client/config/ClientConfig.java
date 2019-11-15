package com.rolea.learning.reactive.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@PropertySource(value = {"classpath:client-config.properties"})
public class ClientConfig {

	@Value("${annotation.server.url}")
	private String annotationServerUrl;

	@Value("${functional.server.url}")
	private String functionalServerUrl;

	@Bean("annotationClient")
	public WebClient annotationClient(){
		return WebClient.create(annotationServerUrl);
	}

	@Bean("functionalClient")
	public WebClient functionalClient(){
		return WebClient.create(functionalServerUrl);
	}

}
