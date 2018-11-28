package com.example.herokupipeexample;

import java.util.List;
import java.util.Random;


import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.codahale.metrics.MetricRegistry.name;


@RestController
public class CustomerController {
	
	private CustomerRepository customerRepository;
	private Random r;
	private final Counter pendingJobs;
	
	@Autowired
	public MetricRegistry metricRegistry;
	
	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
		this.r = new Random();
		pendingJobs = metricRegistry.counter(name(Customer.class, "PostRequestCounter"));
	}
	
	
	@RequestMapping("/")
	public String welcome() {
		
		//setup and start the timer
		final Timer responses = metricRegistry.timer(name(Customer.class, "WelcomePageTimer"));
		final Timer.Context context = responses.time();
		
		//set random delay
		int time = r.nextInt(1000);
		
		try {
			sleep(time);
			
			return "Welcome to this small REST service. It will accept a GET on /list with a request parameter lastName, and a POST to / with a JSON payload with firstName and lastName as values.";
			
		} finally {
			
			//stop the timer
			context.stop();
			
			//mark in meter
			metricRegistry.meter("WelcomePageCount").mark();
		}
	}
	
	@RequestMapping("/list")
	public List<Customer> find(@RequestParam(value="lastName") String lastName) {
		return customerRepository.findByLastName(lastName);
	}
	
	@PostMapping("/")
	Customer newCustomer(@RequestBody Customer customer) {
		
		pendingJobs.inc();
		System.out.println(customer);
		return customerRepository.save(customer);
	}
	
	private void sleep(int x) {
		try {
			Thread.sleep(x);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}