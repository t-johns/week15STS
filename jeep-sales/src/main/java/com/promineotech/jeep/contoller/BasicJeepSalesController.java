package com.promineotech.jeep.contoller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.promineotech.jeep.entity.Jeep;

@RestController // assign rest controller
public class BasicJeepSalesController implements JeepSalesController {

	@Override
	public List<Jeep> fetchJeeps(String model, String trim) {
		return null;
	}

}
