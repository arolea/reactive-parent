package com.rolea.learning.reactive.server.handlers;

import com.rolea.learning.reactive.server.model.Student;
import com.rolea.learning.reactive.service.StudentService;
import com.rolea.learning.reactive.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Component
public class StudentHandler {

	@Autowired
	private StudentService studentService;

	public Mono<ServerResponse> findById(ServerRequest request) {
		Long id = Long.valueOf(request.pathVariable("id"));
		try {
			return ok()
					.body(BodyInserters.fromPublisher(studentService.findById(id), Student.class));
		} catch (EntityNotFoundException e){
			return status(HttpStatus.NOT_FOUND)
					.bodyValue(e.getMessage());
		}
	}

	public Mono<ServerResponse> findAll(ServerRequest request) {
		return ok()
				.contentType(APPLICATION_STREAM_JSON)
				.body(studentService.findAll(), Student.class);
	}

}
