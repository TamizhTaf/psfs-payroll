package com.psfs.service.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.psfs.service.api.util.ServiceUtil;
import com.psfs.service.model.SecurityUser;
import com.psfs.service.model.User;
import com.psfs.service.repository.UserRepository;
import com.psfs.service.security.jwt.JwtTokenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserController {
	private static final Log LOG = LogFactory.getLog(UserController.class);

	private final UserRepository repo;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtTokenUtil jwtTokenUtil;

	public UserController(UserRepository repo, PasswordEncoder encoder) {
		this.repo = repo;
		this.passwordEncoder = encoder;
	}

	@GetMapping(value = "/liveness", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> liveness() {
		String json = "{\"status\":\"success\"}";
		LOG.info("Received liveness request ***");
		return ResponseEntity.ok(json);
	}

	@PostMapping("/api/auth/signin")
	public ResponseEntity<?> signin(@RequestBody User user) {
		HashMap<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		try {

			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getLoginId(), user.getPassword()));

			if (authentication.getPrincipal() instanceof SecurityUser) {
				SecurityUser userSession = (SecurityUser) authentication.getPrincipal();
				String jwtToken = jwtTokenUtil.generateToken(userSession);
				ServiceUtil.userContextCache.put(jwtToken, userSession);

				List<String> authorities = userSession.getAuthorities().stream().map(item -> item.getAuthority())
						.collect(Collectors.toList());

				response.put("authorities", authorities);
				response.put("jwtToken", jwtToken);
				response.put("name", userSession.getName());
				response.put("role", authorities.get(0));
				response.put("status", "success");

			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.put("message", e.getMessage());
		}

		return ResponseEntity.ok(response);
	}

	@PostMapping("/api/auth/signout")
	public ResponseEntity<?> signout() {
		HashMap<String, Object> response = new HashMap<>();
		response.put("status", "failed");

		try {

			String jwtToken = ServiceUtil.getServletRequest().getHeader(ServiceUtil.TOKEN_KEY);

			if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
				jwtToken = jwtToken.substring(7).trim(); // remove "Bearer " and trim spaces
			}

			jwtTokenUtil.removeToken(jwtToken);
			ServiceUtil.userContextCache.remove(jwtToken);
			SecurityContextHolder.clearContext();
			response.put("status", "success");

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.put("message", e.getMessage());
		}

		return ResponseEntity.ok(response);
	}

	@PostMapping("/api/auth/register")
	public Map<String, Object> register(@RequestBody User user) throws Exception {
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");

		try {

			User userFromDB = repo.findByLoginId(user.getLoginId());
			if (userFromDB != null && ServiceUtil.isNotEmpty(userFromDB.getLoginId())) {
				Exception e = new Exception("User already registered!");
				throw e;
			}

			user.setPassword(passwordEncoder.encode(user.getPassword()));
			repo.save(user);

			response.put("status", "success");
			response.put("message", "User registered successfully.");

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.put("message", e.getMessage());
		}

		return response;
	}

	@GetMapping("/api/auth/users")
	public List<User> listUsers() {
		return repo.findAll();
	}
}
