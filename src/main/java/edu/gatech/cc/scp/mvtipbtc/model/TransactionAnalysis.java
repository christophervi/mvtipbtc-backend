package edu.gatech.cc.scp.mvtipbtc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_analyses")
public class TransactionAnalysis {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analysis_seq")
	@SequenceGenerator(name = "analysis_seq", sequenceName = "ANALYSIS_ID_SEQ", allocationSize = 1)
    private Long id;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private Integer riskScore;
    
    @Column(nullable = false)
    private String riskLevel;
    
    @Column(precision = 19, scale = 8)
    private BigDecimal transactionVolume;
    
    @Column
    private Integer networkConfirmations;
    
    @Column
    private LocalDateTime firstSeen;
    
    @Column(precision = 19, scale = 8)
    private BigDecimal totalReceived;
    
    @Column(precision = 19, scale = 8)
    private BigDecimal totalSent;
    
    @Column(precision = 19, scale = 8)
    private BigDecimal currentBalance;
    
    @Column
    private Integer numberOfTransactions;
    
    @Column(columnDefinition = "CLOB")
    private String analysis;
    
    @Column
    private Integer mixingProbability;
    
    @Column
    private Integer illicitFundSources;
    
    @Column
    private Integer darkMarketConnections;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /*@OneToMany(mappedBy = "transactionAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Alert> alerts;*/
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public TransactionAnalysis() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public BigDecimal getTransactionVolume() { return transactionVolume; }
    public void setTransactionVolume(BigDecimal transactionVolume) { this.transactionVolume = transactionVolume; }
    
    public Integer getNetworkConfirmations() { return networkConfirmations; }
    public void setNetworkConfirmations(Integer networkConfirmations) { this.networkConfirmations = networkConfirmations; }
    
    public LocalDateTime getFirstSeen() { return firstSeen; }
    public void setFirstSeen(LocalDateTime firstSeen) { this.firstSeen = firstSeen; }
    
    public BigDecimal getTotalReceived() { return totalReceived; }
    public void setTotalReceived(BigDecimal totalReceived) { this.totalReceived = totalReceived; }
    
    public BigDecimal getTotalSent() { return totalSent; }
    public void setTotalSent(BigDecimal totalSent) { this.totalSent = totalSent; }
    
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    
    public Integer getNumberOfTransactions() { return numberOfTransactions; }
    public void setNumberOfTransactions(Integer numberOfTransactions) { this.numberOfTransactions = numberOfTransactions; }
    
    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    
    public Integer getMixingProbability() { return mixingProbability; }
    public void setMixingProbability(Integer mixingProbability) { this.mixingProbability = mixingProbability; }
    
    public Integer getIllicitFundSources() { return illicitFundSources; }
    public void setIllicitFundSources(Integer illicitFundSources) { this.illicitFundSources = illicitFundSources; }
    
    public Integer getDarkMarketConnections() { return darkMarketConnections; }
    public void setDarkMarketConnections(Integer darkMarketConnections) { this.darkMarketConnections = darkMarketConnections; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

	/*public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}*/
}

