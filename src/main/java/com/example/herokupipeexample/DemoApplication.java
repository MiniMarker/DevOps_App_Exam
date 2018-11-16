package com.example.herokupipeexample;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@Configuration
@SpringBootApplication
public class DemoApplication {
	
	@Value("${graphite.host}")
	private String graphiteHost;
	
	@Value("${graphite.apiKey}")
	private String graphiteApiKey;
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@Bean
	@Primary
	public DataSource dataSource() throws URISyntaxException {
		
		URI dbUri = new URI(System.getenv("DATABASE_URL"));
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

		return DataSourceBuilder.create()
				.url(dbUrl)
				.username(username)
				.password(password)
				.build();
	}
	
	@Bean
	public MetricRegistry getRegistry() {
		return new MetricRegistry();
	}
	
	@Bean
	public GraphiteReporter getReporter(MetricRegistry registry) {
		Graphite graphite = new Graphite(new InetSocketAddress(System.getenv("GRAPHITE_HOST"), 2003));
		
		System.out.println("Passed host: " + graphiteHost);
		System.out.println("Passed apiKey: " + graphiteApiKey);
		
		GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
				.prefixedWith(System.getenv("GRAPHITE_APIKEY"))
				.convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS)
				.filter(MetricFilter.ALL)
				.build(graphite);
		reporter.start(1, TimeUnit.SECONDS);
		return reporter;
	}
}
