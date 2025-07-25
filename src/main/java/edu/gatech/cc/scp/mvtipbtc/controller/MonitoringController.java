package edu.gatech.cc.scp.mvtipbtc.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/monitoring")
//@CrossOrigin(origins = "*")
public class MonitoringController {
    
    private RestTemplate restTemplate = new RestTemplate();
    
    /*private static final int MAX_DATA_POINTS = 10;
    private final List<Double> historicalTPS = new ArrayList<>();
    private final List<String> historicalLabels = new ArrayList<>();*/
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMempoolStats() {
    	String mempoolApiUrl = "https://mempool.space/api/blocks";
        try {
            JsonNode[] response = restTemplate.getForObject(mempoolApiUrl, JsonNode[].class);
            if (response != null && response.length > 0) {
                List<Integer> txCounts = new ArrayList<>();
                List<String> labels = new ArrayList<>();

                // Loop through the blocks (API returns most recent first)
                for (JsonNode block : response) {
                    if (block.has("tx_count") && block.has("timestamp")) {
                        txCounts.add(block.get("tx_count").asInt());
                        long timestamp = block.get("timestamp").asLong();
                        labels.add(timeFormatter.format(Instant.ofEpochSecond(timestamp)));
                    }
                }
                
                // The API returns recent blocks first, so we reverse the lists
                // to show time progressing from left to right on the chart.
                Collections.reverse(txCounts);
                Collections.reverse(labels);

                Map<String, Object> chartData = new LinkedHashMap<>();
                chartData.put("labels", labels);
                chartData.put("data", Collections.singletonList(Map.of(
                    "data", txCounts,
                    "label", "Transactions per Block over Time",
                    "fill", true,
                    "tension", 0.4,
                    "borderColor", "#28a745",
                    "backgroundColor", "rgba(40, 167, 69, 0.3)"
                )));

                return ResponseEntity.ok(chartData);
            }
        } catch (Exception e) {
            System.err.println("Error fetching block stats: " + e.getMessage());
        }
        return ResponseEntity.status(500).build();
    }
    
    /*private synchronized void updateHistoricalData(double tps, String label) {
        if (historicalTPS.size() >= MAX_DATA_POINTS) {
            historicalTPS.remove(0);
            historicalLabels.remove(0);
        }
        historicalTPS.add(tps);
        historicalLabels.add(label);
    }
    
	@GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRealTimeStats() {
        try {
            // Fetch from mempool.space API
            String mempoolUrl = "https://mempool.space/api/mempool";
            Map<String, Object> mempoolData = restTemplate.getForObject(mempoolUrl, Map.class);
            
            // Mock additional stats
            Map<String, Object> stats = Map.of(
                "mempool", mempoolData != null ? mempoolData : Map.of("count", 15432, "vsize", 45000000),
                "fees", Map.of("fastestFee", 45, "halfHourFee", 35, "hourFee", 25),
                "network", Map.of("hashrate", "150 EH/s", "difficulty", "25.05T"),
                "anomalies", new Object[]{
                    Map.of("type", "fee_spike", "description", "Average fees increased by 200%", "severity", "warning"),
                    Map.of("type", "high_volume", "description", "50% above normal levels", "severity", "info")
                }
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Return mock data if API fails
            Map<String, Object> mockStats = Map.of(
                "mempool", Map.of("count", 15432, "vsize", 45000000),
                "fees", Map.of("fastestFee", 45, "halfHourFee", 35, "hourFee", 25),
                "network", Map.of("hashrate", "150 EH/s", "difficulty", "25.05T"),
                "anomalies", new Object[]{
                    Map.of("type", "fee_spike", "description", "Average fees increased by 200%", "severity", "warning")
                }
            );
            return ResponseEntity.ok(mockStats);
        }
    }*/
}

