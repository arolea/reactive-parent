package com.rolea.learning.reactive.service.impl;

import com.rolea.learning.reactive.server.model.Student;
import com.rolea.learning.reactive.service.StudentService;
import com.rolea.learning.reactive.service.exception.EntityNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.time.Duration.ofMillis;
import static java.util.stream.Collectors.toMap;

@Component
public class StudentServiceImpl implements StudentService {

	private Map<Long, Student> studentMap;

	@PostConstruct
	public void initData() {
		studentMap = Stream.of(
				Student.builder().id(1L).name("Foo").build(),
				Student.builder().id(2L).name("Bar").build(),
				Student.builder().id(3L).name("Baz").build()
		).collect(toMap(Student::getId, Function.identity()));
	}

	@Override
	public Mono<Student> findById(Long id) {
		if(!studentMap.containsKey(id)){
			throw new EntityNotFoundException(String.format("Student with id %d does not exist", id));
		}
		return Mono.just(studentMap.get(id));
	}

	@Override
	public Flux<Student> findAll() {
		return Flux.fromIterable(studentMap.values())
				.zipWith(Flux.interval(ofMillis(2000)))
				.map(Tuple2::getT1);
	}
}
