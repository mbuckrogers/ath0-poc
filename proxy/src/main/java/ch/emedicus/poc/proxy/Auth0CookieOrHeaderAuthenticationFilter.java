package ch.emedicus.poc.proxy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.auth0.spring.security.api.Auth0AuthenticationFilter;
import com.auth0.spring.security.api.Auth0JWTToken;

public class Auth0CookieOrHeaderAuthenticationFilter extends Auth0AuthenticationFilter {

	@Autowired
    public AuthenticationManager authenticationManager;
	
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        if (request.getMethod().equals("OPTIONS")) {
            // CORS request
            chain.doFilter(request, response);
            return;
        }
        final String jwt = getToken(request);
        if (jwt != null) {
            try {
                final Auth0JWTToken token = new Auth0JWTToken(jwt);
                final Authentication authResult = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            } catch (AuthenticationException failed) {
                SecurityContextHolder.clearContext();
//                entryPoint.commence(request, response, failed);
//                return;
            }
        }
        chain.doFilter(request, response);
    }
	
	 /**
     * Looks at the authorization bearer and extracts the JWT
     */
    protected String getToken(HttpServletRequest httpRequest) {
    	
	    String tokenInHeader = super.getToken(httpRequest);
    	String tokenInCookie = null;
    	if(httpRequest.getCookies() != null) {
	    	Optional<Cookie> optCookie =Arrays.asList(httpRequest.getCookies()).stream()
	    		.filter(cookie -> "jwt".equals(cookie.getName()))
	    		.findFirst();
	    	if(optCookie.isPresent()) {
	    		tokenInCookie = optCookie.get().getValue();
	    	}
	    	// if both are not null we want them to be equals
	    	if(tokenInCookie != null && tokenInHeader != null) {
	    		if(!tokenInCookie.equals(tokenInHeader)) {
	    			return null;
	    		} else {
	    			// can return any of them
	    			return tokenInCookie;
	    		}
	    	}
    	}
    	
    	return tokenInCookie != null? tokenInCookie : tokenInHeader;
    }

}
