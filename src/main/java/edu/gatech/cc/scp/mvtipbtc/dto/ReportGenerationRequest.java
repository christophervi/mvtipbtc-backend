package edu.gatech.cc.scp.mvtipbtc.dto;

public class ReportGenerationRequest {
	private String address;

	public ReportGenerationRequest() {
	}

	public ReportGenerationRequest(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
