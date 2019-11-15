package com.rolea.learning.reactive.server.controller;

import com.rolea.learning.reactive.server.model.Student;
import com.rolea.learning.reactive.service.StudentService;
import com.rolea.learning.reactive.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/students")
public class StudentController {

	@Autowired
	private StudentService studentService;

	@GetMapping(value = "/{id}")
	public Mono<Student> getStudent(@PathVariable Long id) {
		return studentService.findById(id);
	}

	@GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<Student> getStudents() {
		return studentService.findAll();
	}

	@PostMapping
	public Mono<Student> createStudent(@RequestBody Student student){
		return studentService.save(student);
	}

	@ExceptionHandler
	public ResponseEntity<String> handle(EntityNotFoundException e){
		return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	}

}
