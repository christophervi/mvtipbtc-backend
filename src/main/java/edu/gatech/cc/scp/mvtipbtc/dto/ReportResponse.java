package edu.gatech.cc.scp.mvtipbtc.dto;

public class ReportResponse {
	private String reportUrl;

	public ReportResponse() {
	}

	public ReportResponse(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
}
