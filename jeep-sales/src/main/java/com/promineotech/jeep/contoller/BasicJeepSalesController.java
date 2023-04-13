package com.promineotech.jeep.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.service.JeepSalesService;

import lombok.extern.slf4j.Slf4j;

@RestController // assign rest controller
@Slf4j
public class BasicJeepSalesController implements JeepSalesController { // gain function from inversion
																	   // of control

	@Autowired // inject object
	private JeepSalesService jeepSalesService;
	
	@Override // receive control from inversion of control
	public List<Jeep> fetchJeeps(String model, String trim) {
		log.debug("model={}, trim={}", model, trim);
		return jeepSalesService.fetchJeeps(model, trim);
	}

}
