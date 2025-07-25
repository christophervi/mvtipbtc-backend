package edu.gatech.cc.scp.mvtipbtc.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import edu.gatech.cc.scp.mvtipbtc.config.JwtUtil;
import edu.gatech.cc.scp.mvtipbtc.dto.Edge;
import edu.gatech.cc.scp.mvtipbtc.dto.Node;
import edu.gatech.cc.scp.mvtipbtc.dto.TransactionAnalysisRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.TransactionFlow;
import edu.gatech.cc.scp.mvtipbtc.model.TransactionAnalysis;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.service.AuthService;
import edu.gatech.cc.scp.mvtipbtc.service.ThreatIntelligenceService;

@RestController
@RequestMapping("/api/analysis")
//@CrossOrigin(origins = "*")
public class AnalysisController {
    
    @Autowired
    private ThreatIntelligenceService threatService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/transaction")
    public ResponseEntity<TransactionAnalysis> analyzeTransaction(
            @RequestBody TransactionAnalysisRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            TransactionAnalysis analysis = threatService.analyzeTransaction(request.getAddress(), user);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/flow/{address}")
    public ResponseEntity<TransactionFlow> getTransactionFlow(@PathVariable String address) {
        JsonNode response = threatService.fetchTransactionsForAddress(address);

        if (response == null) {
            return ResponseEntity.status(500).build();
        }

        TransactionFlow flowData = new TransactionFlow();
        Set<Node> nodes = new HashSet<>();
        Set<Edge> edges = new HashSet<>();
        AtomicLong edgeIdCounter = new AtomicLong(0);
        final int NODE_LIMIT = 10;

        // Add the main address as the central node
        nodes.add(new Node(address, "Central Address"));

        JsonNode txs = response.path("txs");
        if (txs.isArray()) {
            for (JsonNode tx : txs) {
                // Stop processing if we've reached our node limit
                if (nodes.size() >= NODE_LIMIT) {
                    break;
                }

                // Process inputs
                tx.path("inputs").forEach(input -> {
                    if (nodes.size() < NODE_LIMIT && input.has("prev_out") && input.path("prev_out").has("addr")) {
                        String inputAddress = input.path("prev_out").path("addr").asText();
                        if (!inputAddress.equals(address)) {
                            nodes.add(new Node(inputAddress, "Input Address"));
                            edges.add(new Edge(String.valueOf(edgeIdCounter.getAndIncrement()), inputAddress, address));
                        }
                    }
                });

                // Process outputs
                tx.path("out").forEach(output -> {
                    if (nodes.size() < NODE_LIMIT && output.has("addr")) {
                        String outputAddress = output.path("addr").asText();
                        if (!outputAddress.equals(address)) {
                            nodes.add(new Node(outputAddress, "Output Address"));
                            edges.add(new Edge(String.valueOf(edgeIdCounter.getAndIncrement()), address, outputAddress));
                        }
                    }
                });
            }
        }

        flowData.setNodes(nodes);
        flowData.setEdges(edges);

        return ResponseEntity.ok(flowData);
    }
}

