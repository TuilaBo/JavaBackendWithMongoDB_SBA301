package com.se170395.orchid;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@SpringBootTest
@Import(OrchidApplicationTests.TestConfig.class)
class OrchidApplicationTests {

	@Test
	void contextLoads() {
	}

	@TestConfiguration
	static class TestConfig {
		@Bean
		EmbeddingModel embeddingModel() {
			return mock(EmbeddingModel.class);
		}
	}

}
