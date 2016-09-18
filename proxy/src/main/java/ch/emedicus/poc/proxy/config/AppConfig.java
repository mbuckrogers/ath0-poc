package ch.emedicus.poc.proxy.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import com.auth0.spring.security.api.Auth0AuthenticationEntryPoint;
import com.auth0.spring.security.api.Auth0SecurityConfig;


//@Configuration
//@EnableWebSecurity(debug = true)
// @EnableGlobalMethodSecurity(prePostEnabled = false)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class AppConfig extends Auth0SecurityConfig {

    /**
     * Provides Auth0 API access
     */
    @Bean
    public Auth0Client auth0Client() {
        return new Auth0Client(clientId, issuer);
    }
    
  
    public Auth0CookieOrHeaderAuthenticationFilter auth0AuthenticationFilter2(final Auth0AuthenticationEntryPoint entryPoint) {
    	final Auth0CookieOrHeaderAuthenticationFilter filter = new Auth0CookieOrHeaderAuthenticationFilter();
        filter.setEntryPoint(entryPoint);
        return filter;
    }
    
    @Bean(name = "auth0AuthenticationFilterRegistration")
    public FilterRegistrationBean auth0AuthenticationFilter(final Auth0CookieOrHeaderAuthenticationFilter filter) {
        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }
    
    
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // Add Auth0 Authentication Filter
    	
        http.addFilterAfter(auth0AuthenticationFilter2(auth0AuthenticationEntryPoint()), SecurityContextPersistenceFilter.class)
                .addFilterBefore(simpleCORSFilter(), Auth0CookieOrHeaderAuthenticationFilter.class);

        // Apply the Authentication and Authorization Strategies your application endpoints require
        authorizeRequests(http);

        // STATELESS - we want re-authentication of JWT token on every request
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        http.csrf().disable();
		//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        
    }
    
    @Override
    protected void authorizeRequests(final HttpSecurity http) throws Exception {
     			http.authorizeRequests()
     			.antMatchers("/", "index.html", "/*.png", "/js/**", "/renewtoken/**", "/unsecured/**").permitAll()
     			.anyRequest().authenticated();
     			// @formatter:on
    }

}