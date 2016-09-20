package ch.emedicus.poc.proxy;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class RequestLoggerZuul extends ZuulFilter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
	    HttpServletRequest request = ctx.getRequest();
	    StringBuffer buffer = new StringBuffer();
	    Collections.list(request.getHeaderNames()).forEach(key -> buffer.append(key + " : " + request.getHeader(key) + "\n"));
    	logger.info("---------------------------------------------------------------------------------------");
		logger.info(buffer.toString());
		return null;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String filterType() {
		return "pre";
	}


}
