package com.psfs.service.api.util;

import java.util.HashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.psfs.service.model.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class ServiceUtil {

	public static final String DB_URL = "jdbc:mysql://localhost:3306/psfs";
	public static final String DB_USER = "root";
	public static final String DB_PASS = "root";
	public static final String TOKEN_KEY = "apitoken";

	public static HashMap<String, SecurityUser> userContextCache = new HashMap<>();

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static HttpServletRequest getServletRequest() {
		HttpServletRequest request = null;

		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
			request = ((ServletRequestAttributes) requestAttributes).getRequest();
		}

		return request;
	}

	public static SecurityUser getLoginSession() {
		SecurityUser userSession = null;

		if (getServletRequest() != null) {
			String jwtToken = getServletRequest().getHeader(ServiceUtil.TOKEN_KEY);

			if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
				jwtToken = jwtToken.substring(7).trim(); // remove "Bearer " and trim spaces
			}

			if (ConverterUtil.isNotEmpty(jwtToken))
				userSession = userContextCache.get(jwtToken);
		}

		return userSession;
	}

	// Utility to safely convert to String (null-safe and trimmed)
	public static String safeString(Object value) {
		return value != null ? String.valueOf(value).trim() : null;
	}

	public static boolean isNotEmpty(String value) {
		return value != null && value != "" && !"null".equalsIgnoreCase(value) ? true : false;
	}

	public static String removeEOL(String value) {
		if (isNotEmpty(value)) {
			value = value.replace("\r\n", "");
			value = value.replace("\n", "");
			value = value.replace("\r", "");
		}
		return value;
	}
}
