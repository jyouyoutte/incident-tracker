package com.incident.tracker.auth.infrastructure.web.vo;

import java.util.List;

public record UserVo(String username, String password,  List<String> roles) {
	public UserVo(String username, String password ) {
		this(username, password, null);
	}
	public UserVo(String username,  List<String> roles) {
		this(username, null,  roles);
	}
}
