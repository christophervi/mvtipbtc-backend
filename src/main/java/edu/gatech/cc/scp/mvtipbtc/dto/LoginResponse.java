package edu.gatech.cc.scp.mvtipbtc.dto;

public class LoginResponse {
	private String token;
	private UserDto user;

	public LoginResponse() {
	}

	public LoginResponse(String token, UserDto user) {
		this.token = token;
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}
}
