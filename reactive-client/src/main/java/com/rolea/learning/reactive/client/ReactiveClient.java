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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class ReactiveClient implements CommandLineRunner {

	@Autowired
	@Qualifier("annotationClient")
	private WebClient annotationClient;

	@Autowired
	@Qualifier("functionalClient")
	private WebClient functionalClient;

	private static final int ASYNC_PROCESS_COUNT = 6;
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
		consumeMonoSuccess(annotationClient);
		consumeMonoError(annotationClient);
		consumeFlux(annotationClient);

		LOGGER.info("Consuming functional server");
		consumeMonoSuccess(functionalClient);
		consumeMonoError(functionalClient);
		consumeFlux(functionalClient);

		ASYNC_LATCH.await();
	}

	private void consumeMonoSuccess(WebClient annotationClient) {
		LOGGER.info("Invoking mono endpoint with valid id");
		Mono<Student> studentMono = annotationClient.get()
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

	private void consumeMonoError(WebClient annotationClient) {
		LOGGER.info("Invoking mono endpoint with invalid id");
		Mono<Student> studentMono = annotationClient.get()
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

	private void consumeFlux(WebClient annotationClient) {
		LOGGER.info("Invoking flux endpoint");
		Flux<Student> studentFlux = annotationClient.get()
				.uri("/students")
				.retrieve()
				.bodyToFlux(Student.class);
		studentFlux.subscribe(
				student -> LOGGER.info("Received student with id {}", student.getId()),
				exception -> {
					LOGGER.error("Exception detected", exception);
					ASYNC_LATCH.countDown();
				},
				ASYNC_LATCH::countDown
		);
	}
}
