package com.rolea.learning.reactive.persistence.repositories;

import com.rolea.learning.reactive.server.model.Student;
import org.springframework.data.r2dbc.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StudentRepository extends ReactiveCrudRepository<Student, Long> {

	@Query("select id, name from student where name = $1")
	Flux<Student> findAllByName(String name);

}
