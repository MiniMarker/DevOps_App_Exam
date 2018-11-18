package com.example.herokupipeexample;

import java.util.List;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    private CustomerRepository customerRepository;
	//private Meter meter;

	@Autowired
	public MetricRegistry metricRegistry;
	
    @Autowired
    public CustomerController(CustomerRepository customerRepository, MetricRegistry mark) {
      this.customerRepository = customerRepository;
      //this.meter = mark.meter("name");
      
    }

    @RequestMapping("/")
    public String welcome() {
    	metricRegistry.meter("WelcomePageCount").mark();
    	//meter.mark();
        return "Welcome to this small REST service. It will accept a GET on /list with a request parameter lastName, and a POST to / with a JSON payload with firstName and lastName as values.";
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

}
