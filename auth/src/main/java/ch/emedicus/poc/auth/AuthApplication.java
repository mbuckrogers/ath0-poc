package ch.emedicus.poc.auth;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.Auth0;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.Delegation;

@SpringBootApplication
@RestController
public class AuthApplication {

	private final Logger log = LoggerFactory.getLogger(AuthApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}
	
	@RequestMapping("/renew")
	public String renewIdToken(HttpServletRequest servletRequest) {
		String token = getToken(servletRequest);
		Auth0 auth0 = new Auth0("iRotCpUYyfUFLSZKcdOp9QxNptKjiHrx", "thelf.eu.auth0.com");
		AuthenticationAPIClient client = auth0.newAuthenticationAPIClient();
		Delegation delegation = client.delegationWithIdToken(token).execute();
		log.info("Renewed token");
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
