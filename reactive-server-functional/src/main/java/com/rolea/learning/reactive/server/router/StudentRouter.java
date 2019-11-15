package com.rolea.learning.reactive.server.router;

import com.rolea.learning.reactive.server.handlers.StudentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Component
public class StudentRouter {

	@Autowired
	private StudentHandler handler;

	@Bean
	public RouterFunction<ServerResponse> route() {
		return RouterFunctions
				.route(GET("/students/{id}").and(accept(TEXT_PLAIN)), handler::findById)
				.andRoute(GET("/students").and(accept(APPLICATION_JSON)), handler::findAll)
				.andRoute(POST("/students").and(accept(APPLICATION_JSON)), handler::createStudent);
	}

}
