package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class NativeBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(NativeBootApplication.class, args);
	}

	@Bean
	ApplicationListener<AvailabilityChangeEvent<?>> availabilityChangeEventApplicationListener(){
		return event -> System.out.println(event.getResolvableType() + "----------->" + event.getState());
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent>  applicationReadyEventApplicationListener(CustomerRepository customerRepository){
		return event -> Flux.just("JeremyGilbert", "Elena Gilbert", "Damon Salvatore", "Stephen Salvatore")
				.map(name -> new Customer(null, name))
				.flatMap(customerRepository::save)
				.subscribe(System.out::println);
	}
}

@Controller
@ResponseBody
@RequiredArgsConstructor
class CustomerController{

	private final CustomerRepository customerRepository;

	@GetMapping("/customers")
	Flux<Customer> getAll(){
		return customerRepository.findAll();
	}
}


@RestController
@RequiredArgsConstructor
class AvailabilityHttpController {

	private final ApplicationContext context;

	@GetMapping("/down")
	public void down(){
		AvailabilityChangeEvent.publish(this.context, LivenessState.BROKEN);
	}

	@GetMapping("/slow")
	public void slow () throws Exception{
		Thread.sleep(10_000);
	}
}




interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}
record Customer (@Id  Integer id, String name){}
