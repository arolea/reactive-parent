package com.rolea.learning.reactive.server.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class LoggingFilter implements WebFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
		LOGGER.info("Started processing at {}", LocalDateTime.now());
		Mono<Void> result = webFilterChain.filter(serverWebExchange);
		// note that the actual consumption of a flux happens async
		LOGGER.info("Finished processing at {}", LocalDateTime.now());
		return result;
	}
}
