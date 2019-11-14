package com.rolea.learning.reactive.server.controller;

import com.rolea.learning.reactive.server.model.Student;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.time.Duration.ofMillis;
import static java.util.stream.Collectors.toMap;

@RestController
@RequestMapping("/students")
public class StudentController {

	private Map<Long, Student> studentMap;

	@PostConstruct
	public void initData() {
		studentMap = Stream.of(
				Student.builder().id(1L).name("Foo").build(),
				Student.builder().id(2L).name("Bar").build(),
				Student.builder().id(3L).name("Baz").build()
		).collect(toMap(Student::getId, Function.identity()));
	}

	@GetMapping(value = "/{id}")
	public Mono<Student> getStudent(@PathVariable Long id) {
		return Mono.just(studentMap.get(id));
	}

	@GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<Student> getStudents() {
		return Flux.fromIterable(studentMap.values())
				.zipWith(Flux.interval(ofMillis(2000)))
				.map(Tuple2::getT1);
	}

}
