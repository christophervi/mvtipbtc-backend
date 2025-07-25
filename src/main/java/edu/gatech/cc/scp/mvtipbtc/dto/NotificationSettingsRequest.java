package edu.gatech.cc.scp.mvtipbtc.dto;

public class NotificationSettingsRequest {
	private Boolean emailAlerts;
	private Boolean pushNotifications;
	private Boolean criticalAlerts;
	private Boolean highRiskAlerts;
	private Boolean mediumRiskAlerts;
	private Boolean reportGeneration;
	private Boolean weeklyDigest;

	public NotificationSettingsRequest() {
	}

	public Boolean getEmailAlerts() {
		return emailAlerts;
	}

	public void setEmailAlerts(Boolean emailAlerts) {
		this.emailAlerts = emailAlerts;
	}

	public Boolean getPushNotifications() {
		return pushNotifications;
	}

	public void setPushNotifications(Boolean pushNotifications) {
		this.pushNotifications = pushNotifications;
	}

	public Boolean getCriticalAlerts() {
		return criticalAlerts;
	}

	public void setCriticalAlerts(Boolean criticalAlerts) {
		this.criticalAlerts = criticalAlerts;
	}

	public Boolean getHighRiskAlerts() {
		return highRiskAlerts;
	}

	public void setHighRiskAlerts(Boolean highRiskAlerts) {
		this.highRiskAlerts = highRiskAlerts;
	}

	public Boolean getMediumRiskAlerts() {
		return mediumRiskAlerts;
	}

	public void setMediumRiskAlerts(Boolean mediumRiskAlerts) {
		this.mediumRiskAlerts = mediumRiskAlerts;
	}

	public Boolean getReportGeneration() {
		return reportGeneration;
	}

	public void setReportGeneration(Boolean reportGeneration) {
		this.reportGeneration = reportGeneration;
	}

	public Boolean getWeeklyDigest() {
		return weeklyDigest;
	}

	public void setWeeklyDigest(Boolean weeklyDigest) {
		this.weeklyDigest = weeklyDigest;
	}
}
