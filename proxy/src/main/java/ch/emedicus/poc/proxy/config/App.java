package ch.emedicus.poc.proxy.config;

import java.security.Principal;
import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAutoConfiguration
@RestController
@EnableZuulProxy
@ComponentScan(basePackages = {"com.auth0.spring.security.api"})
@Import({AppConfig.class})
@PropertySources({
		@PropertySource("classpath:auth0.properties")
})
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
	
	@Bean
	public RequestLoggerZuul requestLogger() {
		return new RequestLoggerZuul();
	}

//	@Configuration
//	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
//	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//		@Override
//		protected void configure(HttpSecurity http) throws Exception {
//			// @formatter:off
//			http.httpBasic().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
//			.and().authorizeRequests().antMatchers("/index.html", "/js/**", "/*.png", "/unsecured/**").permitAll()
//			.anyRequest().authenticated();
//			
//			// @formatter:on
//		}
//	}
	
	
	@RequestMapping("/secured")
	public String onlyAuthorized(Principal user) {
		String username = user != null ? user.getName() : "Principla is null";
		
		return "If you (" + username + ") see this then you just called a secured rest api";
	}
	
	@RequestMapping("/unsecured")
	public String publicRest() {
		return "Everyone can see this rest api";
	}
}
