package ch.emedicus.poc.proxy.config;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.Auth0;
import com.auth0.authentication.AuthenticationAPIClient;

@SpringBootApplication
@EnableAutoConfiguration
@RestController
@EnableZuulProxy
@ComponentScan(basePackages = {"com.auth0.spring.security.api"})
@Import({AppConfig.class})
@PropertySources({
		@PropertySource("classpath:auth0.properties")
})
@RequestMapping("/api")
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
	public String onlyAuthorized(HttpSession session, Principal user) {
		String username = user != null ? user.getName() : "Principal is null";
		
		return "If you (" + username + ") see this then you just called a secured rest api";
	}
	
	@RequestMapping("/unsecured")
	public String publicRest(HttpSession session, Principal user) {
		String username = user != null ? user.getName() : "Principal is null";
		return "Everyone can see this rest api " + username;
	}
	
	@RequestMapping(value="/storerefresh", method = RequestMethod.POST)
	public void storerefresh(HttpSession session, @RequestBody String refreshToken) {
		System.err.println("Thanks for the refresh Token " + refreshToken);
		if(session != null) {
			String[] parts = refreshToken.split("=");
			String tokenValue = parts[1];
			session.setAttribute("refreshToken", tokenValue);
		}
	}
	
	@RequestMapping("/renewtoken")
	public String renewIdToken(HttpSession session) {
		String refreshToken = (String) session.getAttribute("refreshToken");
		String renewedIdToken = null;
		if(refreshToken != null) {
			Auth0 auth0 = new Auth0("iRotCpUYyfUFLSZKcdOp9QxNptKjiHrx", "thelf.eu.auth0.com");
			AuthenticationAPIClient client = auth0.newAuthenticationAPIClient();
			renewedIdToken = client.delegationWithRefreshToken(refreshToken)
					.setScope("openid email user_metadata")
					.execute().getIdToken();
		}
		return renewedIdToken;
	}
	
}
