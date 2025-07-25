package edu.gatech.cc.scp.mvtipbtc.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "alerts")
public class Alert {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alert_seq")
	@SequenceGenerator(name = "alert_seq", sequenceName = "ALERT_ID_SEQ", allocationSize = 1)
	private Long id;

	@Column(nullable = false)
	private String type;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "CLOB")
	private String description;

	@Column(nullable = false)
	private String riskLevel;

	@Column
	private String address;

	@Column(nullable = false)
	private LocalDateTime timestamp;

	@Column(nullable = false)
	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	/*@ManyToOne
    @JoinColumn(name = "analysis_id")
    @JsonBackReference
    private TransactionAnalysis transactionAnalysis;*/
	
	@PrePersist
	protected void onCreate() {
		if (timestamp == null) {
			timestamp = LocalDateTime.now();
		}
	}

	public Alert() {
	}

	public Alert(String type, String title, String description, String riskLevel, String address, User user) {
		this.type = type;
		this.title = title;
		this.description = description;
		this.riskLevel = riskLevel;
		this.address = address;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/*public TransactionAnalysis getTransactionAnalysis() {
		return transactionAnalysis;
	}

	public void setTransactionAnalysis(TransactionAnalysis transactionAnalysis) {
		this.transactionAnalysis = transactionAnalysis;
	}*/
}
