package ch.emedicus.poc.proxy.config;

import java.security.Principal;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.Auth0;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.Delegation;

@SpringBootApplication
@EnableAutoConfiguration
@RestController
@EnableZuulServer
//@ComponentScan(basePackages = {"com.auth0.spring.security.api"})
@Import({GatewaySecurityConfig.class})
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
	
	@RequestMapping("/renew")
	public String renewIdToken(HttpServletRequest servletRequest) {
		String token = getToken(servletRequest);
		Auth0 auth0 = new Auth0("iRotCpUYyfUFLSZKcdOp9QxNptKjiHrx", "thelf.eu.auth0.com");
		AuthenticationAPIClient client = auth0.newAuthenticationAPIClient();
		Delegation delegation = client.delegationWithIdToken(token).execute();
		System.err.println("Renewed token");
		return delegation.getIdToken();
		
	}
	
	private String getToken(HttpServletRequest httpRequest) {
        final String authorizationHeader = httpRequest.getHeader("authorization");
        if (authorizationHeader == null) {
            // "Unauthorized: No Authorization header was found"
            return null;
        }
        
        final String[] parts = authorizationHeader.split(" ");
        if (parts.length != 2) {
            // "Unauthorized: Format is Authorization: Bearer [token]"
            return null;
        }
        final String scheme = parts[0];
        final String credentials = parts[1];
        final Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(scheme).matches() ? credentials : null;
    }

	
}
