package com.psfs.service.security;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.psfs.service.model.SecurityUser;
import com.psfs.service.model.User;
import com.psfs.service.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private static final Log LOG = LogFactory.getLog(UserDetailsServiceImpl.class);

	private final UserRepository repo;

	public UserDetailsServiceImpl(UserRepository repo) {
		this.repo = repo;
	}

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		User user = null;
		try {

			LOG.info("loadUserByUsername loginId ->" + loginId);
			user = repo.findByLoginId(loginId);

			if (user == null) {
				throw new UsernameNotFoundException("User not found: " + loginId);
			}

			boolean enabled = true;

			Collection<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(user.getRole()));

			SecurityUser securityUser = new SecurityUser(user.getLoginId(), user.getPassword(), enabled, true, true,
					true, authorities);

			securityUser.setId(user.getId());
			securityUser.setName(user.getName());
			return securityUser;

		} catch (Exception e) {
			String message = e.getMessage();
			if (user == null) {
				message = "User not found!";
			}
			LOG.error(message, e);
			throw e;
		}

	}
}