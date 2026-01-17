package com.ey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;


@SpringBootApplication

public class WeddingEventManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeddingEventManagementApplication.class, args);
		System.out.println("Go ahead");
	}

}
