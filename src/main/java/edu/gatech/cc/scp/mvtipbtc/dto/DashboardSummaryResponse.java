package edu.gatech.cc.scp.mvtipbtc.dto;

public class DashboardSummaryResponse {
	private int criticalAlerts;
	private int highRiskAlerts;
	private int mediumRiskAlerts;
	private int lowRiskAlerts;
	private int totalTransactionsAnalyzed;
	private int highRiskTransactions;
	private double averageRiskScore;

	public int getCriticalAlerts() {
		return criticalAlerts;
	}

	public void setCriticalAlerts(int criticalAlerts) {
		this.criticalAlerts = criticalAlerts;
	}

	public int getHighRiskAlerts() {
		return highRiskAlerts;
	}

	public void setHighRiskAlerts(int highRiskAlerts) {
		this.highRiskAlerts = highRiskAlerts;
	}

	public int getMediumRiskAlerts() {
		return mediumRiskAlerts;
	}

	public void setMediumRiskAlerts(int mediumRiskAlerts) {
		this.mediumRiskAlerts = mediumRiskAlerts;
	}

	public int getLowRiskAlerts() {
		return lowRiskAlerts;
	}

	public void setLowRiskAlerts(int lowRiskAlerts) {
		this.lowRiskAlerts = lowRiskAlerts;
	}

	public int getTotalTransactionsAnalyzed() {
		return totalTransactionsAnalyzed;
	}

	public void setTotalTransactionsAnalyzed(int totalTransactionsAnalyzed) {
		this.totalTransactionsAnalyzed = totalTransactionsAnalyzed;
	}

	public int getHighRiskTransactions() {
		return highRiskTransactions;
	}

	public void setHighRiskTransactions(int highRiskTransactions) {
		this.highRiskTransactions = highRiskTransactions;
	}

	public double getAverageRiskScore() {
		return averageRiskScore;
	}

	public void setAverageRiskScore(double averageRiskScore) {
		this.averageRiskScore = averageRiskScore;
	}
}
