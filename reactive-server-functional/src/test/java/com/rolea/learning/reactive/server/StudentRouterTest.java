package com.rolea.learning.reactive.server;

import com.rolea.learning.reactive.server.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

/**
 * See https://github.com/spring-projects/spring-boot/issues/10683
 */
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentRouterTest {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void getStudent_ok() {
		webTestClient.get()
				.uri("/students/{id}", 1)
				.exchange().expectStatus().isOk()
				.expectBody(Student.class).isEqualTo(Student.builder().id(1L).name("Foo").build());
	}

	@Test
	public void getStudent_error() {
		webTestClient.get()
				.uri("/students/{id}", 10)
				.exchange().expectStatus().isNotFound()
				.expectBody(String.class).isEqualTo("Student with id 10 does not exist");
	}

	@Test
	public void getStudents_ok() {
		webTestClient.get()
				.uri("/students")
				.exchange().expectStatus().isOk()
				.expectBodyList(Student.class).contains(
				Student.builder().id(1L).name("Foo").build(),
				Student.builder().id(2L).name("Bar").build(),
				Student.builder().id(3L).name("Baz").build()
		);
	}

	@Test
	public void createStudent_ok(){
		Student student = Student.builder()
				.id(4L)
				.name("Buz")
				.build();
		webTestClient.post()
				.uri("/students")
				.body(BodyInserters.fromValue(student))
				.exchange().expectStatus().isOk()
				.expectBody(Student.class).isEqualTo(Student.builder().id(4L).name("Buz").build()
		);
	}

}
