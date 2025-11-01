package com.rezeptapp.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@Import(TestBeans.class)
class BackendApplicationTests {

	@Mock
	private JdbcTemplate jdbcTemplate;

	@TestConfiguration
	static class JdbcTemplateConfig {
		@Bean
		JdbcTemplate jdbcTemplate() { return org.mockito.Mockito.mock(JdbcTemplate.class); }
	}

	@Test
	void contextLoads() {
	}

}
