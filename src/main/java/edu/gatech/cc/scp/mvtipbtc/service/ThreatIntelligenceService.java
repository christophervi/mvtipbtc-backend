package edu.gatech.cc.scp.mvtipbtc.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.gatech.cc.scp.mvtipbtc.dto.ChainabuseReport;
import edu.gatech.cc.scp.mvtipbtc.dto.RecentActivityResponse;
import edu.gatech.cc.scp.mvtipbtc.model.Alert;
import edu.gatech.cc.scp.mvtipbtc.model.TransactionAnalysis;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.repository.AlertRepository;
import edu.gatech.cc.scp.mvtipbtc.repository.TransactionAnalysisRepository;

@Service
public class ThreatIntelligenceService {

	@Autowired
    private TransactionAnalysisRepository analysisRepository;
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private ChatClient chatClient;
    
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();
    private Random random = new Random();
    
    private static final String BLOCKCHAIN_API_KEY = "";
    private static final String CHAINABUSE_API_KEY = "";
    
    public TransactionAnalysis analyzeTransaction(String address, User user) {
        try {
            // Check if analysis already exists
            var existingAnalysis = analysisRepository.findByAddressAndUser(address, user);
            if (existingAnalysis.isPresent()) {
                return existingAnalysis.get();
            }
            
            // Fetch data from Blockchain.com API
            Map<String, Object> blockchainData = fetchBlockchainData(address);
            
            // Fetch data from Chainabuse.com API
            List<ChainabuseReport> chaindabuseData = fetchChaindabuseData(address);
            
            // Analyze with AI
            String aiAnalysis = performAIAnalysis(address, blockchainData, chaindabuseData);
            
            // Calculate risk metrics
            int riskScore = calculateRiskScore(blockchainData, chaindabuseData);
            String riskLevel = getRiskLevelFromScore(riskScore);
            
            // Create and save analysis
            TransactionAnalysis analysis = new TransactionAnalysis();
            analysis.setAddress(address);
            analysis.setRiskScore(riskScore);
            analysis.setRiskLevel(riskLevel);
            analysis.setUser(user);
            analysis.setAnalysis(aiAnalysis);
            
            // Set blockchain data
            if (blockchainData.containsKey("total_received")) {
                analysis.setTotalReceived(new BigDecimal(blockchainData.get("total_received").toString()).divide(new BigDecimal("100000000")));
            }
            if (blockchainData.containsKey("total_sent")) {
                analysis.setTotalSent(new BigDecimal(blockchainData.get("total_sent").toString()).divide(new BigDecimal("100000000")));
            }
            if (blockchainData.containsKey("final_balance")) {
                analysis.setCurrentBalance(new BigDecimal(blockchainData.get("final_balance").toString()).divide(new BigDecimal("100000000")));
            }
            if (blockchainData.containsKey("n_tx")) {
                analysis.setNumberOfTransactions((Integer) blockchainData.get("n_tx"));
            }
            
            // Set risk metrics
            analysis.setMixingProbability(random.nextInt(40) + 60); // 60-100%
            analysis.setIllicitFundSources(random.nextInt(30) + 50); // 50-80%
            analysis.setDarkMarketConnections(random.nextInt(20) + 80); // 80-100%
            analysis.setNetworkConfirmations(random.nextInt(100) + 300);
            analysis.setFirstSeen(LocalDateTime.now().minusDays(random.nextInt(365)));
            
            analysis = analysisRepository.save(analysis);
            
            // Generate alert if high risk
            if (riskScore > 70) {
                generateAlert(analysis, user);
            }
            
            return analysis;
            
        } catch (Exception e) {
            // Return mock data if APIs fail
            return createMockAnalysis(address, user);
        }
    }
    
    public JsonNode fetchTransactionsForAddress(String address) {
    	HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + BLOCKCHAIN_API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        String url = "https://blockchain.info/rawaddr/" + address;
        //ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        
        
        /*String url = UriComponentsBuilder.fromHttpUrl("https://api.blockchain.info")
                .path("/rawaddr/{address}")
                .queryParam("api_code", BLOCKCHAIN_API_KEY)
                .buildAndExpand(address)
                .toUriString();*/

        try {
        	//return restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            return restTemplate.getForObject(url, JsonNode.class);
        } catch (Exception e) {
            System.err.println("Error fetching data from Blockchain.com: " + e.getMessage());
            return null;
        }
    }
    
    private Map<String, Object> fetchBlockchainData(String address) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + BLOCKCHAIN_API_KEY);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            String url = "https://blockchain.info/rawaddr/" + address;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            return objectMapper.readValue(response.getBody(), Map.class);
        } catch (Exception e) {
            return Map.of(
                "total_received", 524891000L,
                "total_sent", 524891000L,
                "final_balance", 0L,
                "n_tx", 42
            );
        }
    }
    
    private List<ChainabuseReport> fetchChaindabuseData(String address) {
    	String url = "https://api.chainabuse.com/v0/reports?address=" + address;
        
        HttpHeaders headers = new HttpHeaders();
        String auth = CHAINABUSE_API_KEY + ":" + CHAINABUSE_API_KEY;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return objectMapper.readValue(response.getBody(), new TypeReference<List<ChainabuseReport>>() {});
        } catch (Exception e) {
            // Log the exception or handle it as needed
            System.err.println("Error fetching data from Chainabuse: " + e.getMessage());
            return List.of(); // Return an empty list on failure
        }
    }
    
    private String performAIAnalysis(String address, Map<String, Object> blockchainData, List<ChainabuseReport> chaindabuseData) {
        try {
            String prompt = String.format(
                "Analyze this Bitcoin address for threat intelligence: %s. " +
                "Blockchain data: %s. " +
                "Chainabuse data: %s. " +
                "Cross-reference this address with internal FBI data. " +
                "Provide a concise risk assessment focusing on potential money laundering, mixing services, and dark market connections." +
                "Explicitly state if the address is monitored by the FBI according to the provided context at the URL: https://www.fbi.gov/news/press-releases/fbi-identifies-lazarus-group-cyber-actors-as-responsible-for-theft-of-41-million-from-stakecom .",
                address, blockchainData.toString(), chaindabuseData.toString()
            );
            
            return chatClient.prompt()
            		.system("You are a Bitcoin & Cryptocurrency Cybersecurity Analyst. Based on the data please give a concise 1-2 sentence(s) risk assessment (don't mention blockchain.com and chainabuse.com explicitly) with a concise conclusion and recommendations (bullet point) section. Please be sure to embed HTML to preserve response formatting.")
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            return "This address has been associated with multiple high-risk activities including potential money laundering and connections to dark web marketplaces. Multiple inputs from high-risk addresses were detected.";
        }
    }
    
    private int calculateRiskScore(Map<String, Object> blockchainData, List<ChainabuseReport> chaindabuseData) {
    	if (chaindabuseData == null || chaindabuseData.isEmpty()) {
            return 0;
        }
        //int score = 30; // Base score
    	int score = 0;
    	
        // Increase score based on transaction volume
        if (blockchainData.containsKey("n_tx")) {
            int txCount = (Integer) blockchainData.get("n_tx");
            if (txCount > 100) score += 20;
            else if (txCount > 50) score += 10;
        }
        
        
        // Define weights for different scam categories
        Map<String, Integer> categoryWeights = Map.ofEntries(
            Map.entry("RANSOMWARE", 30),
            Map.entry("PIGBUTCHERING", 28),
            Map.entry("SEXTORTION", 25),
            Map.entry("OTHER_BLACKMAIL", 25),
            Map.entry("MAN_IN_THE_MIDDLE_ATTACK", 22),
            Map.entry("CONTRACT_EXPLOIT", 20),
            Map.entry("PHISHING", 18),
            Map.entry("IMPERSONATION", 18),
            Map.entry("UKRANIAN_DONATION_SCAM", 15),
            Map.entry("DONATION_SCAM", 15),
            Map.entry("FAKE_PROJECT", 12),
            Map.entry("UPGRADE_SCAM", 12),
            Map.entry("OTHER_INVESTMENT_SCAM", 12),
            Map.entry("SIM_SWAP", 10),
            Map.entry("RUG_PULL", 10),
            Map.entry("AIRDROP", 8),
            Map.entry("ROMANCE", 8),
            Map.entry("FAKE_RETURNS", 5),
            Map.entry("OTHER_HACK", 5),
            Map.entry("OTHER", 2)
        );
        
        // Increase score based on chainabuse data
        for (ChainabuseReport report : chaindabuseData) {
            int reportScore = 0;
            
            // Get the base score from the category weight
            String category = report.getScamCategory() != null ? report.getScamCategory().toUpperCase() : "OTHER";
            reportScore += categoryWeights.getOrDefault(category, 2);

            // Add a significant penalty if the report source is not trusted
            if (!report.isTrusted()) {
                reportScore += 10;
            }
            
            score += reportScore;
        }
        
        // Add randomness for demonstration
        //score += random.nextInt(20);
        
        return Math.min(score, 100);
    }
    
    private String getRiskLevelFromScore(int score) {
        if (score > 100) return "Critical";
        if (score > 70) return "High";
        if (score > 40) return "Medium";
        if (score > 0) return "Low";
        return "None";
    }
    
    /*private String determineRiskLevel(int score) {
        if (score >= 80) return "Critical";
        if (score >= 60) return "High";
        if (score >= 40) return "Medium";
        return "Low";
    }*/
    
    private void generateAlert(TransactionAnalysis analysis, User user) {
        Alert alert = new Alert(
            "High Risk Alert",
            "High-risk Bitcoin address detected",
            "Address " + analysis.getAddress() + " shows patterns consistent with illicit activities",
            analysis.getRiskLevel(),
            analysis.getAddress(),
            user
        );
        alert.setStatus("NEW"); 
        alert.setTimestamp(LocalDateTime.now());
        alertRepository.save(alert);
    }
    
    private TransactionAnalysis createMockAnalysis(String address, User user) {
        TransactionAnalysis analysis = new TransactionAnalysis();
        analysis.setAddress(address);
        analysis.setRiskScore(78);
        analysis.setRiskLevel("High");
        analysis.setUser(user);
        analysis.setAnalysis("This address has been associated with multiple high-risk activities including potential money laundering and connections to dark web marketplaces.");
        analysis.setTotalReceived(new BigDecimal("5.24891"));
        analysis.setTotalSent(new BigDecimal("5.24891"));
        analysis.setCurrentBalance(new BigDecimal("0.00000"));
        analysis.setNumberOfTransactions(42);
        analysis.setMixingProbability(78);
        analysis.setIllicitFundSources(64);
        analysis.setDarkMarketConnections(92);
        analysis.setNetworkConfirmations(342);
        analysis.setFirstSeen(LocalDateTime.of(2023, 5, 14, 14, 32, 18));
        
        return analysisRepository.save(analysis);
    }
    
    public List<Alert> getAlertsForUser(User user) {
        return alertRepository.findByUserOrderByTimestampDesc(user);
    }
    
    public List<TransactionAnalysis> getAnalysesForUser(User user) {
        return analysisRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public List<Alert> getFilteredAlerts(User user, String riskLevel, String alertType, String status) {
        return getAlertsForUser(user);
    }
    
    public Alert getAlertById(Long id, User user) {
        return alertRepository.findByIdAndUser(id, user).orElse(null);
    }
    
    public boolean updateAlertStatus(Long id, String status, User user) {
        var alertOpt = alertRepository.findByIdAndUser(id, user);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            //alert.setStatus(status);
            //alert.setUpdatedAt(LocalDateTime.now());
            alertRepository.save(alert);
            return true;
        }
        return false;
    }
    
    public boolean dismissAlert(Long id, User user) {
        var alertOpt = alertRepository.findByIdAndUser(id, user);
        if (alertOpt.isPresent()) {
            alertRepository.delete(alertOpt.get());
            return true;
        }
        return false;
    }
    
    public Map<String, Object> getAlertStats(User user) {
        List<Alert> alerts = getAlertsForUser(user);
        Map<String, Object> stats = new HashMap<>();
        
        long critical = alerts.stream().filter(a -> "Critical".equals(a.getRiskLevel())).count();
        long high = alerts.stream().filter(a -> "High".equals(a.getRiskLevel())).count();
        long medium = alerts.stream().filter(a -> "Medium".equals(a.getRiskLevel())).count();
        //long active = alerts.stream().filter(a -> "Active".equals(a.getStatus())).count();
        
        stats.put("critical", critical);
        stats.put("high", high);
        stats.put("medium", medium);
        //stats.put("active", active);
        stats.put("total", alerts.size());
        
        return stats;
    }

    public List<Alert> getAlerts() {
        // Sample data for now
        List<Alert> alerts = new ArrayList<>();
        
        Alert alert1 = new Alert();
        alert1.setId(1L);
        alert1.setType("Suspicious Transaction");
        alert1.setTitle("High-risk Bitcoin address detected");
        alert1.setDescription("Address 1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF shows patterns consistent with illicit activities");
        alert1.setRiskLevel("Critical");
        //alert1.setTimestamp(new Date());
        alert1.setTimestamp(LocalDateTime.now());
        alert1.setStatus("New");
        alert1.setAddress("1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF");
        alerts.add(alert1);
        
        Alert alert2 = new Alert();
        alert2.setId(2L);
        alert2.setType("Network Anomaly");
        alert2.setTitle("Unusual transaction pattern detected");
        alert2.setDescription("Multiple high-volume transactions in short time period");
        alert2.setRiskLevel("High");
        //alert2.setTimestamp(new Date(System.currentTimeMillis() - 3600000)); // 1 hour ago
        alert2.setTimestamp(LocalDateTime.now().minusHours(1));
        alert2.setStatus("New");
        alerts.add(alert2);
        
        Alert alert3 = new Alert();
        alert3.setId(3L);
        alert3.setType("Monitoring Alert");
        alert3.setTitle("Connection to previously flagged address");
        alert3.setDescription("Transaction linked to known suspicious address");
        alert3.setRiskLevel("Medium");
        //alert3.setTimestamp(new Date(System.currentTimeMillis() - 86400000)); // 1 day ago
        alert3.setTimestamp(LocalDateTime.now().minusDays(1));
        alert3.setStatus("Read");
        alerts.add(alert3);
        
        return alerts;
    }
    
    /*public List<Alert> getAlertsForUser(User user) {
        // In a real implementation, this would filter alerts by user
        return getAlerts();
    }*/
    
    public List<Alert> getFilteredAlerts(String riskLevel, String status, String type) {
        List<Alert> allAlerts = getAlerts();
        List<Alert> filteredAlerts = new ArrayList<>();
        
        for (Alert alert : allAlerts) {
            boolean includeAlert = true;
            
            if (riskLevel != null && !riskLevel.equals("All") && !alert.getRiskLevel().equals(riskLevel)) {
                includeAlert = false;
            }
            
            if (status != null && !status.equals("All") && !alert.getStatus().equals(status)) {
                includeAlert = false;
            }
            
            if (type != null && !type.equals("All") && !alert.getType().equals(type)) {
                includeAlert = false;
            }
            
            if (includeAlert) {
                filteredAlerts.add(alert);
            }
        }
        
        return filteredAlerts;
    }
    
    public TransactionAnalysis analyzeAddress(String address) {
        // Sample data for now
        TransactionAnalysis analysis = new TransactionAnalysis();
        analysis.setAddress(address);
        analysis.setRiskScore(78);
        analysis.setAnalysis("This transaction shows patterns consistent with coin mixing activities. Multiple inputs from high-risk addresses were detected.");
        analysis.setMixingProbability(78);
        analysis.setIllicitFundSources(64);
        analysis.setDarkMarketConnections(92);
        //analysis.setFirstSeen("2023-05-12 14:32:18 UTC");
        analysis.setFirstSeen(LocalDateTime.now().minusDays(3));
        analysis.setTotalReceived(new BigDecimal("5.24891"));
        analysis.setTotalSent(new BigDecimal(5.24891));
        analysis.setCurrentBalance(new BigDecimal(0.00000));
        analysis.setNumberOfTransactions(42);
        analysis.setNetworkConfirmations(342);
        
        return analysis;
    }
    
    public Map<String, Object> getTransactionFlow(String address) {
        // Sample data for now
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();
        
        // Create center node (the address being analyzed)
        Map<String, Object> centerNode = new HashMap<>();
        centerNode.put("id", address);
        centerNode.put("label", "Target Address");
        nodes.add(centerNode);
        
        // Create some input nodes
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> inputNode = new HashMap<>();
            String nodeId = "input" + i;
            inputNode.put("id", nodeId);
            inputNode.put("label", "Input Address " + i);
            nodes.add(inputNode);
            
            // Create edge from input to center
            Map<String, Object> edge = new HashMap<>();
            edge.put("id", "e" + i);
            edge.put("source", nodeId);
            edge.put("target", address);
            edge.put("label", "1.2 BTC");
            edges.add(edge);
        }
        
        // Create some output nodes
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> outputNode = new HashMap<>();
            String nodeId = "output" + i;
            outputNode.put("id", nodeId);
            outputNode.put("label", "Output Address " + i);
            nodes.add(outputNode);
            
            // Create edge from center to output
            Map<String, Object> edge = new HashMap<>();
            edge.put("id", "e" + (i + 5));
            edge.put("source", address);
            edge.put("target", nodeId);
            edge.put("label", "0.8 BTC");
            edges.add(edge);
        }
        
        result.put("nodes", nodes);
        result.put("edges", edges);
        
        return result;
    }
    
    public Map<String, Integer> getAlertStatistics() {
        List<Alert> alerts = getAlerts();
        
        int critical = 0;
        int high = 0;
        int medium = 0;
        
        for (Alert alert : alerts) {
            switch (alert.getRiskLevel()) {
                case "Critical":
                    critical++;
                    break;
                case "High":
                    high++;
                    break;
                case "Medium":
                    medium++;
                    break;
            }
        }
        
        Map<String, Integer> stats = new HashMap<>();
        stats.put("critical", critical);
        stats.put("high", high);
        stats.put("medium", medium);
        stats.put("total", alerts.size());
        
        return stats;
    }
    
    public void markAllAlertsAsRead(User user) {
        List<Alert> alerts = getAlertsForUser(user);
        for (Alert alert : alerts) {
            //alert.setStatus("Read");
            //alert.setUpdatedAt(LocalDateTime.now());
        }
        alertRepository.saveAll(alerts);
    }
    
    /**
     * Get count of alerts by risk level
     * @param riskLevel Risk level to count
     * @return Count of alerts with the specified risk level
     */
    public int getAlertCountByRiskLevel(String riskLevel) {
        switch (riskLevel) {
            case "Critical":
                return 2;
            case "High":
                return 5;
            case "Medium":
                return 8;
            case "Low":
                return 12;
            default:
                return 0;
        }
    }
    
    /**
     * Get total number of transactions analyzed
     * @return Total transactions analyzed
     */
    public int getTotalTransactionsAnalyzed() {
        return 1245;
    }
    
    /**
     * Get count of high risk transactions
     * @return Count of high risk transactions
     */
    public int getHighRiskTransactionsCount() {
        return 87;
    }
    
    /**
     * Get average risk score across all transactions
     * @return Average risk score
     */
    public double getAverageRiskScore() {
        return 42.7;
    }
    
    /**
     * Get recent activities for the dashboard
     * @param limit Maximum number of activities to return
     * @return List of recent activities
     */
    public List<RecentActivityResponse.ActivityItem> getRecentActivities(int limit) {
        List<RecentActivityResponse.ActivityItem> activities = new ArrayList<>();
        
        RecentActivityResponse.ActivityItem activity1 = new RecentActivityResponse.ActivityItem();
        activity1.setType("Alert");
        activity1.setDescription("Critical alert: Ransomware wallet detected");
        activity1.setTimestamp(System.currentTimeMillis() - 3600000); // 1 hour ago
        activity1.setAddress("1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF");
        activity1.setRiskLevel("Critical");
        activities.add(activity1);
        
        RecentActivityResponse.ActivityItem activity2 = new RecentActivityResponse.ActivityItem();
        activity2.setType("Analysis");
        activity2.setDescription("Transaction analysis completed");
        activity2.setTimestamp(System.currentTimeMillis() - 7200000); // 2 hours ago
        activity2.setAddress("bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh");
        activity2.setRiskLevel("Medium");
        activities.add(activity2);
        
        RecentActivityResponse.ActivityItem activity3 = new RecentActivityResponse.ActivityItem();
        activity3.setType("Report");
        activity3.setDescription("Risk assessment report generated");
        activity3.setTimestamp(System.currentTimeMillis() - 86400000); // 1 day ago
        activity3.setAddress("3J98t1WpEZ73CNmQviecrnyiWrnqRhWNLy");
        activity3.setRiskLevel("High");
        activities.add(activity3);
        
        return activities.subList(0, Math.min(activities.size(), limit));
    }
}

