package com.rolea.learning.reactive.service.impl;

import com.rolea.learning.reactive.persistence.repositories.StudentRepository;
import com.rolea.learning.reactive.server.model.Student;
import com.rolea.learning.reactive.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class StudentServiceImpl implements StudentService {

	@Autowired
	private StudentRepository studentRepository;

	@Override
	public Mono<Student> findById(Long id) {
		return studentRepository.findById(id);
	}

	@Override
	public Flux<Student> findAll() {
		return studentRepository.findAll();
		// uncomment for simulated delays
		/**
				.zipWith(Flux.interval(ofMillis(1000)))
				.map(Tuple2::getT1);
		 */
	}

	@Override
	public Mono<Student> save(Student student) {
		return studentRepository.save(student);
	}

}
