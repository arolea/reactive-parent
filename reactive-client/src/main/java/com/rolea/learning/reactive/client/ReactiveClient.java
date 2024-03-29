package com.rolea.learning.reactive.client;

import com.rolea.learning.reactive.server.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Comparator;
import java.util.concurrent.CountDownLatch;

import static java.util.Arrays.asList;

@SpringBootApplication
public class ReactiveClient implements CommandLineRunner {

	@Autowired
	@Qualifier("annotationClient")
	private WebClient annotationClient;

	@Autowired
	@Qualifier("functionalClient")
	private WebClient functionalClient;

	private static final int ASYNC_PROCESS_COUNT = 9;
	private static final CountDownLatch ASYNC_LATCH = new CountDownLatch(ASYNC_PROCESS_COUNT);
	private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveClient.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ReactiveClient.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("Consuming annotation server");
//		findStudentSuccess(annotationClient);
//		findStudentError(annotationClient);
//		createStudent(annotationClient);
		findStudents(annotationClient);

//		LOGGER.info("Consuming functional server");
//		findStudentSuccess(functionalClient);
//		findStudentError(functionalClient);
//		createStudent(functionalClient);
//		findStudents(functionalClient);
//
//		LOGGER.info("Testing parallel requests");
//		executeParallelRequests();

		ASYNC_LATCH.await();
	}

	private void findStudentSuccess(WebClient webClient) {
		LOGGER.info("Invoking mono endpoint with valid id");
		Mono<Student> studentMono = webClient.get()
				.uri("/students/{id}", 1)
				.retrieve()
				.bodyToMono(Student.class);

		studentMono.subscribe(
				student -> LOGGER.info("Received student with id {}", student.getId()),
				exception -> {
					LOGGER.error("Exception detected", exception);
					ASYNC_LATCH.countDown();
				},
				ASYNC_LATCH::countDown);
	}

	private void findStudentError(WebClient webClient) {
		LOGGER.info("Invoking mono endpoint with invalid id");
		Mono<Student> studentMono = webClient.get()
				.uri("/students/{id}", 10)
				.retrieve()
				.bodyToMono(Student.class);

		studentMono.subscribe(
				student -> LOGGER.info("Received student with id {}", student.getId()),
				exception -> {
					LOGGER.error("Exception detected", exception);
					ASYNC_LATCH.countDown();
				},
				ASYNC_LATCH::countDown);
	}

	private void findStudents(WebClient webClient) {
		LOGGER.info("Invoking flux endpoint");
		Flux<Student> studentFlux = webClient.get()
				.uri("/students")
				.retrieve()
				.bodyToFlux(Student.class);

		studentFlux
//				.log()
				.subscribe(
						student -> LOGGER.info("Received student with id {}", student.getId()),
						exception -> {
							LOGGER.error("Exception detected", exception);
							ASYNC_LATCH.countDown();
						},
						ASYNC_LATCH::countDown
				);
	}

	private void createStudent(WebClient webClient) {
		LOGGER.info("Invoking POST endpoint");
		Mono<Student> studentMono = webClient.post()
				.uri("/students")
				.body(BodyInserters.fromValue(Student.builder()
						.id(4L)
						.name("Buz")
						.build()))
				.retrieve()
				.bodyToMono(Student.class);

		studentMono.subscribe(
				student -> LOGGER.info("Received student with id {}", student.getId()),
				exception -> {
					LOGGER.error("Exception detected", exception);
					ASYNC_LATCH.countDown();
				},
				ASYNC_LATCH::countDown);
	}

	private void executeParallelRequests() {
		Flux
				.fromIterable(asList(1L, 2L, 3L))
				.parallel()
				.runOn(Schedulers.elastic())
				.flatMap(id -> annotationClient.get()
						.uri("/students/{id}", id)
						.retrieve()
						.bodyToMono(Student.class))
				.ordered(Comparator.comparing(Student::getId))
				.subscribe(student -> LOGGER.info("Received student with id {}", student.getId()));

		Flux
				.merge(
						annotationClient.get()
								.uri("/students/{id}", 1L)
								.retrieve()
								.bodyToMono(Student.class),
						annotationClient.get()
								.uri("/students/{id}", 2L)
								.retrieve()
								.bodyToMono(Student.class)
				)
				.parallel()
				.runOn(Schedulers.elastic())
				.ordered(Comparator.comparing(Student::getId))
				.subscribe(student -> LOGGER.info("Received student with id {}", student.getId()));

		Flux
				.zip(
						annotationClient.get()
								.uri("/students/{id}", 1L)
								.retrieve()
								.bodyToMono(Student.class),
						annotationClient.get()
								.uri("/students/{id}", 2L)
								.retrieve()
								.bodyToMono(Student.class),
						(firstResult, secondResult) -> {
							// do something else with the results
							return asList(firstResult.getId(), secondResult.getId());
						}
				).subscribe(ids -> LOGGER.info("Received ids {}", ids));

		//assume consumption does not fail
		ASYNC_LATCH.countDown();
	}
}
