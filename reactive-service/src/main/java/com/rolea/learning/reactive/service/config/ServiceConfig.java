package com.rolea.learning.reactive.service.config;

import com.rolea.learning.reactive.persistence.config.PersistenceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {PersistenceConfig.class})
@ComponentScan(basePackages = {"com.rolea.learning.reactive.service"})
public class ServiceConfig {
}
