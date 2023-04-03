package com.promineotech.jeep.contoller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.promineotech.jeep.entity.Jeep;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

@RequestMapping("/jeeps") // assign requests at portNum with '/jeeps' to controller
@OpenAPIDefinition(info = @Info(title = "Jeep Sales Service"), servers = {
		@Server(url = "http://localhost:8080", description = "Local server.")})

public interface JeepSalesController { // Manage requests to /jeeps uri
	// @formatter:off
	@Operation(
		summary = "Returns a list of Jeeps",
		description = "Returns a list of Jeeps given an optional model and/or trim",
		responses = {
			@ApiResponse(
				responseCode = "200", // 'OK', success response code
				description = "A list of Jeeps is returned", 
				content = @Content(
					mediaType = "application/json", 
					schema = @Schema(implementation = Jeep.class))), 
			@ApiResponse(
				responseCode = "400", // bad input/illegal request
				description = "The request parameters are invalid", 
				content = @Content(mediaType = "application/json")), 
			@ApiResponse(
				responseCode = "404", // not found error
				description = "No Jeeps were found with the input criteria :(", 
				content = @Content(mediaType = "application/json")), 
			@ApiResponse(
				responseCode = "500", // unplanned exception
				description = "An unplanned error occurred.", 
				content = @Content(mediaType = "application/json"))  
		},
		parameters = {
			@Parameter(name = "model", 
				allowEmptyValue = false, 
				required = false, 
				description = "The model name (i.e. 'WRANGLER')"),
			@Parameter(name = "trim", 
				allowEmptyValue = false, 
				required = false, 
				description = "The trim level (i.e., 'Sport')")
		}
	) 
	@GetMapping // assign to fetchJeeps method
	@ResponseStatus(code = HttpStatus.OK) // return OK if success
	List<Jeep> fetchJeeps(
			@RequestParam(required = false) // spring mapping
				String model,
			@RequestParam(required = false) 
				String trim);
	// @formatter:on
}
