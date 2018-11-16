package com.example.herokupipeexample;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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

    public CustomerController(CustomerRepository customerRepository) {
      this.customerRepository = customerRepository;
    }
    
    private MetricRegistry registry = new MetricRegistry();

    @RequestMapping("/")
    public String welcome() {
        registry.counter("welcome1").inc();
        registry.meter("welcome").mark();
        System.out.println("Entered root page");
        return "Welcome to this small REST service. It will accept a GET on /list with a request parameter lastName, and a POST to / with a JSON payload with firstName and lastName as values.";
    }

    @RequestMapping("/list")
    public List<Customer> find(@RequestParam(value="lastName") String lastName) {
        registry.meter("list").mark();
        return customerRepository.findByLastName(lastName);
    }

    @PostMapping("/")
    	Customer newCustomer(@RequestBody Customer customer) {
        System.out.println(customer);
    		return customerRepository.save(customer);
    	}

}
