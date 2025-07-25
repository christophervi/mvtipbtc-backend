package edu.gatech.cc.scp.mvtipbtc.dto;

import java.math.BigDecimal;
import java.util.List;

public class TransactionSummary {
	private int transactionCount;
	private BigDecimal totalReceived;
	private BigDecimal totalSent;
	private BigDecimal finalBalance;
	private int totalInputs;
	private int totalOutputs;
	private List<String> notableTransactionHashes;

	public TransactionSummary(int transactionCount, BigDecimal totalReceived, BigDecimal totalSent,
			BigDecimal finalBalance, int totalInputs, int totalOutputs, List<String> notableTransactionHashes) {
		this.transactionCount = transactionCount;
		this.totalReceived = totalReceived;
		this.totalSent = totalSent;
		this.finalBalance = finalBalance;
		this.totalInputs = totalInputs;
		this.totalOutputs = totalOutputs;
		this.notableTransactionHashes = notableTransactionHashes;
	}

	public int getTransactionCount() {
		return transactionCount;
	}

	public void setTransactionCount(int transactionCount) {
		this.transactionCount = transactionCount;
	}

	public BigDecimal getTotalReceived() {
		return totalReceived;
	}

	public void setTotalReceived(BigDecimal totalReceived) {
		this.totalReceived = totalReceived;
	}

	public BigDecimal getTotalSent() {
		return totalSent;
	}

	public void setTotalSent(BigDecimal totalSent) {
		this.totalSent = totalSent;
	}

	public BigDecimal getFinalBalance() {
		return finalBalance;
	}

	public void setFinalBalance(BigDecimal finalBalance) {
		this.finalBalance = finalBalance;
	}

	public int getTotalInputs() {
		return totalInputs;
	}

	public void setTotalInputs(int totalInputs) {
		this.totalInputs = totalInputs;
	}

	public int getTotalOutputs() {
		return totalOutputs;
	}

	public void setTotalOutputs(int totalOutputs) {
		this.totalOutputs = totalOutputs;
	}

	public List<String> getNotableTransactionHashes() {
		return notableTransactionHashes;
	}

	public void setNotableTransactionHashes(List<String> notableTransactionHashes) {
		this.notableTransactionHashes = notableTransactionHashes;
	}
	
	@Override
    public String toString() {
        return "{" +
                "transactionCount=" + transactionCount +
                ", totalReceived=" + totalReceived +
                ", totalSent=" + totalSent +
                ", finalBalance=" + finalBalance +
                ", totalInputs=" + totalInputs +
                ", totalOutputs=" + totalOutputs +
                ", notableTransactionHashes=" + notableTransactionHashes +
                '}';
    }
}
