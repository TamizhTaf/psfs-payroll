package com.psfs.service.security.jwt;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.psfs.service.api.util.ServiceUtil;

@Component
public class MeCORSFilter implements Filter {
	private static final Log log = LogFactory.getLog(MeCORSFilter.class);

	public MeCORSFilter() {
		log.info("CORSFilter init..");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		try {
			HttpServletResponse response = (HttpServletResponse) res;
			// CORS headers to allow cross-origin requests
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Headers", ServiceUtil.TOKEN_KEY + ",content-type");
			response.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT");
			response.setHeader("Access-Control-Max-Age", "3600");

			// Continue the chain of filters
			chain.doFilter(req, res);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) {
		log.info("init() of CORSFilter invoked");
	}

	@Override
	public void destroy() {
		log.info("destroy() of CORSFilter invoked");
	}
}
