package com.sdd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"com.sdd"})
public class SpringBootJspApplication extends SpringBootServletInitializer {


	public static void main(String[] args) {
		SpringApplication.run(SpringBootJspApplication.class, args);
	}
}