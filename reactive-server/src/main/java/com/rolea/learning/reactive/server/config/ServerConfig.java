package com.rolea.learning.reactive.server.config;

import com.rolea.learning.reactive.service.config.ServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {ServiceConfig.class})
public class ServerConfig {

}
