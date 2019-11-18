package com.rolea.learning.reactive.persistence.config;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Flux;

@Configuration
@EnableR2dbcRepositories(basePackages = {"com.rolea.learning.reactive.persistence.repositories"})
public class PersistenceConfig extends AbstractR2dbcConfiguration {

	@Bean
	public H2ConnectionFactory connectionFactory() {
		return new H2ConnectionFactory(
				H2ConnectionConfiguration.builder()
						.url("mem:testdb;DB_CLOSE_DELAY=-1;")
						.username("sa")
						.build()
		);
	}

	@Bean
	public CommandLineRunner initDatabase(ConnectionFactory cf) {
		return (args) ->
				Flux.from(cf.create())
						.flatMap(c ->
								Flux.from(c.createBatch()
										.add("drop table if exists STUDENT")
										.add("create table STUDENT(id BIGINT PRIMARY KEY, name VARCHAR(255))")
										.add("insert into STUDENT(id,name) values(1,'Foo')")
										.add("insert into STUDENT(id,name) values(2,'Bar')")
										.add("insert into STUDENT(id,name) values(3,'Baz')")
										.execute())
										.doFinally((st) -> c.close())
						)
						.log()
						.blockLast();
	}

}
