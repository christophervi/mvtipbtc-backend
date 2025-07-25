package edu.gatech.cc.scp.mvtipbtc.dto;

public class TransactionAnalysisRequest {
	private String address;

	public TransactionAnalysisRequest() {
	}

	public TransactionAnalysisRequest(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
