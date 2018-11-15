package com.example.herokupipeexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@SpringBootApplication
public class DemoApplication {
	
	
	/*
	URI:        postgres://cynzpiozskmril:0a48d79a9fa96a872915cb5eb1b920b273bd7022ad269fbf5071805c12908ba6@ec2-79-125-124-30.eu-west-1.compute.amazonaws.com:5432/dcbtgk1u06j8eo
	USER:       cynzpiozskmril
	PASSWORD:   0a48d79a9fa96a872915cb5eb1b920b273bd7022ad269fbf5071805c12908ba6
	 */
	
	@Bean
	@Primary
	public DataSource dataSource() throws URISyntaxException {
		
		URI dbUri = new URI(System.getenv("DATABASE_URL"));
		System.out.println("Uri: " + dbUri.toString());
		System.out.println("Uri.host: " + dbUri.getPath());
		System.out.println("Uri.port " + dbUri.getPort());
		System.out.println("Uri.path: " + dbUri.getPath());
		String username = dbUri.getUserInfo().split(":")[0];
		System.out.println("Username: " + username);
		String password = dbUri.getUserInfo().split(":")[1];
		System.out.println("Password: " + password);
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

		return DataSourceBuilder.create()
				.url(dbUrl)
				.username(username)
				.password(password)
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
