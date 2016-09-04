package ch.emedicus.poc.proxy.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.auth0.spring.security.api.Auth0SecurityConfig;


@Configuration
@EnableWebSecurity(debug = true)
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

    /**
     *  Our API Configuration - for Profile CRUD operations
     *
     *  Here we choose not to bother using the `auth0.securedRoute` property configuration
     *  and instead ensure any unlisted endpoint in our config is secured by default
     */
    @Override
    protected void authorizeRequests(final HttpSecurity http) throws Exception {
        // include some Spring Boot Actuator endpoints to check metrics
        // add others or remove as you choose, this is just a sample config to illustrate
        // most specific rules must come - order is important (see Spring Security docs)
//        http.authorizeRequests()
//                .antMatchers("/ping", "/pong").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/v1/**").authenticated();
     // @formatter:off
    	System.err.println("--------> AppConfig evaluating...");
     			http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
     			.and().authorizeRequests()
     			.antMatchers("/", "index.html", "/*.png", "/js/**", "/unsecured/**").permitAll()
     			.anyRequest().authenticated();
     			// @formatter:on
    }

}