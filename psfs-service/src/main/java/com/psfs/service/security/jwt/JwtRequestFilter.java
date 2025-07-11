package com.psfs.service.security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.psfs.service.api.util.ConverterUtil;
import com.psfs.service.api.util.ServiceUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	private static final Log LOG = LogFactory.getLog(JwtRequestFilter.class);

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String jwtToken = request.getHeader(ServiceUtil.TOKEN_KEY);

		String servletPath = request.getServletPath();
		LOG.info("servletPath->" + servletPath);

		if ("/liveness".equalsIgnoreCase(servletPath) || "/api/auth/register".equalsIgnoreCase(servletPath)
				|| "/api/auth/signin".equalsIgnoreCase(servletPath)
				|| "/api/auth/signout".equalsIgnoreCase(servletPath)) {
			chain.doFilter(request, response);
		}

		else {

			Map<String, Object> validTokenMap = new HashMap<>();
			if (!ServiceUtil.isNotEmpty(jwtToken)) {
				LOG.info("Token is empty " + servletPath);
			} else {

				String username = "";
				if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
					jwtToken = jwtToken.substring(7).trim(); // remove "Bearer " and trim spaces
				}

				try {
					username = jwtTokenUtil.getUsernameFromToken(jwtToken);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					validTokenMap.put("status", "failed");
					validTokenMap.put("message", e.getMessage());
					writeErrorDetail(response, validTokenMap);
					return;
				}

				// Once we get the token validate it.
				if (ServiceUtil.isNotEmpty(username)) {
					UserDetails userDetails = ServiceUtil.userContextCache.get(jwtToken);

					if (userDetails == null) {

						validTokenMap.put("status", "failed");
						validTokenMap.put("message", "User not loggedin!");
						writeErrorDetail(response, validTokenMap);
						return;

					} else if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

						UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						usernamePasswordAuthenticationToken
								.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

					} else {

						validTokenMap.put("status", "failed");
						validTokenMap.put("message", "Token expired!");
						writeErrorDetail(response, validTokenMap);
						return;
					}

				} else {

					validTokenMap.put("status", "failed");
					validTokenMap.put("message", "User not found!");
					writeErrorDetail(response, validTokenMap);
					return;
				}

			}

			chain.doFilter(request, response);
		}
	}

	private void writeErrorDetail(HttpServletResponse response, Map<String, Object> validTokenMap) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		String json = ConverterUtil.getJSONFromObject(validTokenMap);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(json);
	}

}