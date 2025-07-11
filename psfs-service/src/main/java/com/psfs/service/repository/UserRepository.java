package com.psfs.service.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.psfs.service.model.User;

import java.util.List;

@Repository
public class UserRepository {

	private final JdbcTemplate jdbc;

	public UserRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void save(User user) {
		jdbc.update("INSERT INTO user (login_id, name, password, role) VALUES (?, ?, ?, ?)", user.getLoginId(),
				user.getName(), user.getPassword(), user.getRole());
	}

	public User findByLoginId(String loginid) {
		List<User> users = jdbc.query("SELECT * FROM user WHERE login_id = ?", (rs, rowNum) -> {
			User u = new User();
			u.setId(rs.getLong("id"));
			u.setLoginId(rs.getString("login_id"));
			u.setName(rs.getString("name"));
			u.setPassword(rs.getString("password"));
			u.setRole(rs.getString("role"));
			return u;
		}, loginid);
		return users.isEmpty() ? null : users.get(0);
	}

	public List<User> findAll() {
		return jdbc.query("SELECT * FROM user", (rs, rowNum) -> {
			User u = new User();
			u.setId(rs.getLong("id"));
			u.setLoginId(rs.getString("login_id"));
			u.setName(rs.getString("name"));
			u.setPassword(rs.getString("password"));
			u.setRole(rs.getString("role"));
			return u;
		});
	}
}
