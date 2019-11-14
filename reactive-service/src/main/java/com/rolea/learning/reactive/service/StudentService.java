package com.rolea.learning.reactive.service;

import com.rolea.learning.reactive.server.model.Student;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StudentService {

	Mono<Student> findById(Long id);

	Flux<Student> findAll();

}
