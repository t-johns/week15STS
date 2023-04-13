package com.promineotech.jeep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.promineotech.ComponentScanMarker;

@SpringBootApplication(scanBasePackageClasses = // scans sub packages also, i.e. controller, entity 
	{ ComponentScanMarker.class }) // specify component scan location
public class JeepSales {

	public static void main(String[] args) { // start @SprintBootApp, start component scan declared
		SpringApplication.run(JeepSales.class, args); // inversion of control start
	}

}
