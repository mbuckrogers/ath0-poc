package ch.emedicus.poc.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAutoConfiguration
@RestController
@EnableZuulProxy
//@ComponentScan(basePackages = {"com.auth0.spring.security.api"})
@Import({GatewaySecurityConfig.class})
@PropertySources({
		@PropertySource("classpath:auth0.properties")
})

public class ProxyApp {
	
	private final Logger log = LoggerFactory.getLogger(ProxyApp.class);
	
	public static void main(String[] args) {
		SpringApplication.run(ProxyApp.class, args);
	}
	
	@Bean
	public RequestLoggerZuul requestLogger() {
		return new RequestLoggerZuul();
	}	
}
