package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doThrow;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.promineotech.jeep.Constants;
import com.promineotech.jeep.controller.support.FetchJeepTestSupport;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.service.JeepSalesService;



class FetchJeepTest extends FetchJeepTestSupport { // Fetches Jeep Test
	
	@Nested
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
	@ActiveProfiles("test") // override default logging, originate from yaml
	@Sql(
		scripts = {
			"classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
			"classpath:flyway/migrations/V1.1__Jeep_Data.sql" },
		config = @SqlConfig(encoding = "utf-8"))
	class TestsThatDoNotPolluteTheApplicationContext extends FetchJeepTestSupport {
		/**
		 * 
		 */
		@Test
		void testThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
			// Return 200 Success Code
			// Given: a valid model, trim and URI
			JeepModel model = JeepModel.WRANGLER; // set model
			String trim = "Sport"; // trim
			String uri = 
					String.format("%s?model=%s&trim=%s", getBaseUri(), model, trim); // uri format

			//System.out.println(uri); // test print

			// When: a connection is made to the URI
			ResponseEntity<List<Jeep>> response = // returning Jeep entity, as JSON(?)
					getRestTemplate().exchange(uri, HttpMethod.GET, null, 
						new ParameterizedTypeReference<>() {}); // inject test rest template for uri/Jeep class

			// Then: a success (OK - 200) status code is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); // assert check

			// And: the actual list returned is the same as the expected list
			List<Jeep> actual = response.getBody();
			List<Jeep> expected = buildExpected();
			
			assertThat(actual).isEqualTo(expected);
		}

		/*
		 * 
		 */
		@Test
		void testThatAnErrorMessageIsReturnedWhenAnUnknownTrimIsSupplied() {
			// Return 404 Error Code 
			// Given: a valid model, trim and URI
			JeepModel model = JeepModel.WRANGLER; // set model
			String trim = "Unknown Value"; // trim
			String uri = 
					String.format("%s?model=%s&trim=%s", getBaseUri(), model, trim); // uri format

			//System.out.println(uri); // test print

			// When: a connection is made to the URI
			ResponseEntity<Map<String, Object>> response = getRestTemplate() // returning Jeep entity, as JSON(?)
						.exchange(uri, HttpMethod.GET, null, 
						new ParameterizedTypeReference<>() {}); // inject test rest template for uri/Jeep class

			// Then: a not found (404) status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); // assert check

			// And: an error message is returned
			Map<String, Object> error = response.getBody(); // get body
		
			assertErrorMessageValid(error, HttpStatus.NOT_FOUND);
		}
		
		/*
		 * 
		 */
		@ParameterizedTest
		@MethodSource("com.promineotech.jeep.controller.FetchJeepTest#parametersForInvalidInput")
		void testThatAnErrorMessageIsReturnedWhenAnInvalidValueIsSupplied(
				String model, String trim, String reason) {
			// Return 404 Error Code 
			// Given: a valid model, trim and URI
			String uri = 
					String.format("%s?model=%s&trim=%s", getBaseUri(), model, trim); // uri format

			//System.out.println(uri); // test print

			// When: a connection is made to the URI
			ResponseEntity<Map<String, Object>> response = getRestTemplate() // returning Jeep entity, as JSON(?)
						.exchange(uri, HttpMethod.GET, null, 
						new ParameterizedTypeReference<>() {}); // inject test rest template for uri/Jeep class

			// Then: a not found (404) status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST); // assert check

			// And: an error message is returned
			Map<String, Object> error = response.getBody(); // get body
		
			assertErrorMessageValid(error, HttpStatus.BAD_REQUEST);
		}

	}
	
	static Stream<Arguments> parametersForInvalidInput() {
		// @formatter:off
		return Stream.of(
				arguments("WRANGLER", "*#&^#&@", "Trim contains non-alpha-numeric chars"),
				arguments("WRANGLER", "C".repeat(Constants.TRIM_MAX_LENGTH + 1), "Trim length to long."),
				arguments("INVALID", "Sport", "Model is not enum value")
		// @formatter:on
		);
	}
	
	@Nested
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
	@ActiveProfiles("test") // override default logging, originate from yaml
	@Sql(
		scripts = {
			"classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
			"classpath:flyway/migrations/V1.1__Jeep_Data.sql" },
		config = @SqlConfig(encoding = "utf-8"))
	class TestsThatPollutheApplicationContext extends FetchJeepTestSupport {
		@MockBean
		private JeepSalesService jeepSalesService;
		
		/*
		 * 
		 */
		@Test
		void testThatAnUnplannedErrorResultsInA500Status() {
			// Return 404 Error Code 
			// Given: a valid model, trim and URI
			JeepModel model = JeepModel.WRANGLER; // set model
			String trim = "Invalid"; // trim
			String uri = 
					String.format("%s?model=%s&trim=%s", getBaseUri(), model, trim); // uri format

			doThrow(new RuntimeException("Ouch!")).when(jeepSalesService).fetchJeeps(model, trim);
			//System.out.println(uri); // test print

			// When: a connection is made to the URI
			ResponseEntity<Map<String, Object>> response = getRestTemplate() // returning Jeep entity, as JSON(?)
						.exchange(uri, HttpMethod.GET, null, 
						new ParameterizedTypeReference<>() {}); // inject test rest template for uri/Jeep class


			// Then: an internal server error (500) is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR); // assert check

			// And: an error message is returned
			Map<String, Object> error = response.getBody(); // get body
		
			assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}

}
