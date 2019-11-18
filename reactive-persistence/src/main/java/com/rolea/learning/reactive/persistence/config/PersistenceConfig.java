package com.rolea.learning.reactive.persistence.config;

import com.rolea.learning.reactive.server.model.Student;
import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Mono;

import static java.util.Arrays.asList;

@Configuration
@EnableR2dbcRepositories(basePackages = {"com.rolea.learning.reactive.persistence.repositories"})
public class PersistenceConfig extends AbstractR2dbcConfiguration {

	@Bean
	public H2ConnectionFactory connectionFactory() {
		H2ConnectionFactory cf = new H2ConnectionFactory(
				H2ConnectionConfiguration.builder()
						.url("mem:testdb;DB_CLOSE_DELAY=-1;")
						.username("sa")
						.build()
		);

		DatabaseClient client = DatabaseClient.create(cf);
		client.execute().sql("CREATE TABLE STUDENT(id BIGINT PRIMARY KEY, name VARCHAR(255))").fetch()
				.rowsUpdated()
				.as(Mono::block);

		asList(
				Student.builder().id(1L).name("Foo").build(),
				Student.builder().id(2L).name("Bar").build(),
				Student.builder().id(3L).name("Baz").build()
		).forEach(student -> client.insert()
				.into(Student.class)
				.using(student)
				.then()
				.as(Mono::block)
		);

		return cf;
	}

}
