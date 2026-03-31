package com.incident.tracker.infrastructure.web.vo.auth;

import java.util.List;

public record UserVo(String username, String password,  List<String> roles) {
	public UserVo(String username, String password ) {
		this(username, password, null);
	}
	public UserVo(String username,  List<String> roles) {
		this(username, null,  roles);
	}
}
