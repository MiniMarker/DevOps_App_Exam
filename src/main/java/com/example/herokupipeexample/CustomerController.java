package com.example.herokupipeexample;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {

    private CustomerRepository customerRepository;
	private Random r;

	/*
	@Autowired
	public MetricRegistry metricRegistry;
	*/
	
	
    @Autowired
    public CustomerController(CustomerRepository customerRepository ) { // MetricRegistry mark
    	this.customerRepository = customerRepository;
    	this.r = new Random();
    }
    

    @RequestMapping("/")
    public String welcome() {
    	
	    int time = r.nextInt(3000);
	
	    //metricRegistry.timer("WelcomePageLoadTimer").time();
	
	    try {
		    sleep(time);
		
		    return "Welcome to this small REST service. It will accept a " +
				    "GET on /list with a request parameter lastName, and a " +
				    "POST to / with a JSON payload with firstName and lastName as values.";
		    
	    } finally {
		    //metricRegistry.meter("WelcomePageCount").mark();
	    	//metricRegistry.timer("WelcomePageLoadTimer").time().stop();
	    }
    }

    @RequestMapping("/list")
    public List<Customer> find(@RequestParam(value="lastName") String lastName) {
        return customerRepository.findByLastName(lastName);
    }

    @PostMapping("/")
    public Customer newCustomer(@RequestBody Customer customer) {
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
