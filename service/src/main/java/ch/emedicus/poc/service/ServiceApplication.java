package ch.emedicus.poc.service;

import java.security.Principal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}
	
	@RequestMapping("/secured")
	public String onlyAuthorized(Principal user) {
		String username = user != null ? user.getName() : "Principal is null!";
		
		return "If you (" + username + ") see this then you just called a secured rest api";
	}
	
	@RequestMapping("/unsecured")
	public String publicRest(Principal user) {
		String username = user != null ? user.getName() : "Principal is null";
		return "Everyone can see this rest api " + username;
	}	
}
