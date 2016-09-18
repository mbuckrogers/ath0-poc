package ch.emedicus.poc.proxy.config;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.Auth0;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.Credentials;

import ch.emedicus.poc.proxy.domain.TokenData;

@SpringBootApplication
@EnableAutoConfiguration
@RestController
@EnableZuulServer
//@ComponentScan(basePackages = {"com.auth0.spring.security.api"})
@Import({GatewaySecurityConfig.class})
@PropertySources({
		@PropertySource("classpath:auth0.properties")
})
//@RequestMapping("/api")
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
	
	
	@RequestMapping(value="/mycallback")
	public void mycallback(HttpServletResponse httpServletResponse) {
		System.err.println("My callback");
		httpServletResponse.setHeader("Location", "https://thelf.eu.auth0.com/continue");
	}
	
	@RequestMapping("/secured")
	public String onlyAuthorized(HttpSession session, Principal user) {
		String username = user != null ? user.getName() : "Principal is null!";
		
		return "If you (" + username + ") see this then you just called a secured rest api";
	}
	
	@RequestMapping("/unsecured")
	public String publicRest(HttpSession session, Principal user) {
		String username = user != null ? user.getName() : "Principal is null";
		return "Everyone can see this rest api " + username;
		
	}
	
	@RequestMapping(value="/redirect", method = RequestMethod.GET)
	public void redirect(HttpServletResponse response, HttpServletRequest request, 
			@RequestParam String code) throws IOException {
		System.err.println("Request is " + code);
		Auth0 auth0 = new Auth0("iRotCpUYyfUFLSZKcdOp9QxNptKjiHrx", "thelf.eu.auth0.com");
		AuthenticationAPIClient client = auth0.newAuthenticationAPIClient();
		Credentials credentials = client.token(code, request.getRequestURL().toString())
				.setClientSecret("16VgT3QBrTD-BLAbecZl3cmSCrl0WvbvSpRspC6nmbFleYEo4yyoOA6a7pZgo_s2")
				.execute();
		Cookie cookieRefresh = new Cookie("refreshToken", credentials.getRefreshToken());
		cookieRefresh.setMaxAge(12 * 3600);
		cookieRefresh.setHttpOnly(true);
		response.addCookie(cookieRefresh);
		response.addCookie(createIdTokenCookie(credentials.getIdToken()));
		response.sendRedirect("/");
	}
	
	@RequestMapping(value="/storerefresh", method = RequestMethod.POST)
	public void storerefresh(HttpServletResponse response, @RequestBody TokenData tokenData) {
		System.err.println("Thanks for the refresh Token " + tokenData.getRefreshToken());
		Cookie cookieRefresh = new Cookie("refreshToken", tokenData.getRefreshToken());
		cookieRefresh.setMaxAge(12 * 3600);
		cookieRefresh.setHttpOnly(true);
		response.addCookie(cookieRefresh);
		response.addCookie(createIdTokenCookie(tokenData.getId_token()));
	}
	
	

	private Cookie createIdTokenCookie(String jwt) {
		Cookie cookieIdToken = new Cookie("jwt", jwt);
		cookieIdToken.setMaxAge(30);
		return cookieIdToken;
	}
	
	
	@RequestMapping(value="/renewtoken", method = RequestMethod.POST)
	public void renewIdToken(HttpServletResponse response, @CookieValue("refreshToken") String refreshToken) {
		String renewedIdToken = null;
		if(refreshToken != null) {
			Auth0 auth0 = new Auth0("iRotCpUYyfUFLSZKcdOp9QxNptKjiHrx", "thelf.eu.auth0.com");
			AuthenticationAPIClient client = auth0.newAuthenticationAPIClient();
			renewedIdToken = client.delegationWithRefreshToken(refreshToken)
					.setScope("openid email user_metadata xsrf_token")
					.execute().getIdToken();
			response.addCookie(createIdTokenCookie(renewedIdToken));
		}
		
	}
	
}
