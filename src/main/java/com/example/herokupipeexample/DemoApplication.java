package com.example.herokupipeexample;

import com.codahale.metrics.MetricAttribute;
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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricAttribute.*;

@Configuration
@SpringBootApplication
public class DemoApplication {
	
	/*
	@Value("${graphite.host}")
	private String graphiteHost;
	
	@Value("${graphite.apiKey}")
	private String graphiteApiKey;
	*/
	
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
		Graphite graphite = new Graphite(new InetSocketAddress(System.getenv("GRAHITE_HOST"), 2003));
		
		System.out.println("Passed host: " + System.getenv("GRAPHITE_HOST"));
		System.out.println("Passed apiKey: " + System.getenv("GRAHITE_APIKEY"));
		
		Set<MetricAttribute> excludeSet = new HashSet<>();
		excludeSet.add(M1_RATE);
		excludeSet.add(M15_RATE);
		
		GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
				.prefixedWith(System.getenv("GRAHITE_APIKEY"))
				.convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS)
				.filter(MetricFilter.ALL)
				.disabledMetricAttributes(excludeSet)
				.build(graphite);
		reporter.start(1, TimeUnit.SECONDS);
		return reporter;
	}
}

//tests