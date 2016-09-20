package ch.emedicus.poc.auth;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLogger extends GenericFilterBean {
	private final Logger logger = LoggerFactory.getLogger(AuthApplication.class);
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		StringBuffer buffer = new StringBuffer();
		HttpServletRequest request = (HttpServletRequest) req;
    	Collections.list(request.getHeaderNames()).forEach(key -> buffer.append(key + " : " + request.getHeader(key) + "\n"));
    	Collections.list(request.getSession().getAttributeNames()).forEach(key -> 
		buffer.append(key + " : " + request.getSession().getAttribute(key) + "\n"));
    	
    	logger.info("---------------------------------------------------------------------------------------");
		logger.info(buffer.toString());
		chain.doFilter(req, response);
	}

}
