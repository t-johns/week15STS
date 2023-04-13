package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.jdbc.JdbcTestUtils;

import com.promineotech.jeep.controller.support.FetchJeepTestSupport;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // override default logging, originate from yaml
@Sql(scripts = {"classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
		"classpath:flyway/migrations/V1.1__Jeep_Data.sql"})

@Sql(
	scripts = { "classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
	"classpath:flyway/migrations/V1.1__Jeep_Data.sql" },
	config = @SqlConfig(encoding = "utf-8"))

class FetchJeepTest extends FetchJeepTestSupport { // Fetches Jeep Test
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Test
	void testDb() {
		int numrows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "customers");
		System.out.println("num=" + numrows);
		
	}

	@Disabled
	@Test
	void testThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
		// Given: a valid model, trim and URI
		JeepModel model = JeepModel.WRANGLER; // set model
		String trim = "Sport"; // trim
		String uri = 
				String.format("%s?model=%s&trim=%s", getBaseUri(), model, trim); // uri format

		System.out.println(uri); // test print

		// When: a connection is made to the URI
		ResponseEntity<List<Jeep>> response = // returning Jeep entity, as JSON(?)
				getRestTemplate().exchange(uri, HttpMethod.GET, null, 
					new ParameterizedTypeReference<>() {}); // inject test rest template for uri/Jeep class

		// Then: a success (OK - 200) status code is returned
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); // assert check

		// And: the actual list returned is the same as the expected list
		List<Jeep> expected = buildExpected();
		//System.out.println(expected);
		assertThat(response.getBody()).isEqualTo(expected);
	}


}
