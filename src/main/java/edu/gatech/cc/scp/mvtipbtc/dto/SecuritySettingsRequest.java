package edu.gatech.cc.scp.mvtipbtc.dto;

public class SecuritySettingsRequest {
	private Boolean twoFactorAuth;
	private Integer sessionTimeout;
	private Integer passwordExpiry;
	private Boolean loginNotifications;

	public SecuritySettingsRequest() {
	}

	public Boolean getTwoFactorAuth() {
		return twoFactorAuth;
	}

	public void setTwoFactorAuth(Boolean twoFactorAuth) {
		this.twoFactorAuth = twoFactorAuth;
	}

	public Integer getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(Integer sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public Integer getPasswordExpiry() {
		return passwordExpiry;
	}

	public void setPasswordExpiry(Integer passwordExpiry) {
		this.passwordExpiry = passwordExpiry;
	}

	public Boolean getLoginNotifications() {
		return loginNotifications;
	}

	public void setLoginNotifications(Boolean loginNotifications) {
		this.loginNotifications = loginNotifications;
	}
}
