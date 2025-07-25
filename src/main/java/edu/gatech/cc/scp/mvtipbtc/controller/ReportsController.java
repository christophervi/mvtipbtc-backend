package edu.gatech.cc.scp.mvtipbtc.controller;

import edu.gatech.cc.scp.mvtipbtc.service.PdfReportService;
import edu.gatech.cc.scp.mvtipbtc.service.S3Service;
import edu.gatech.cc.scp.mvtipbtc.service.ThreatIntelligenceService;
import edu.gatech.cc.scp.mvtipbtc.service.AuthService;
import edu.gatech.cc.scp.mvtipbtc.model.TransactionAnalysis;
import edu.gatech.cc.scp.mvtipbtc.model.Report;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.repository.ReportRepository;
import edu.gatech.cc.scp.mvtipbtc.repository.TransactionAnalysisRepository;
import edu.gatech.cc.scp.mvtipbtc.dto.ReportGenerationRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.ReportResponse;
import edu.gatech.cc.scp.mvtipbtc.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
//@CrossOrigin(origins = "*")
public class ReportsController {
    
	@Autowired
	private S3Service s3Service;
	
	@Autowired
    private PdfReportService pdfReportService;
    
    @Autowired
    private ThreatIntelligenceService threatService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private TransactionAnalysisRepository analysisRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/generate")
    public ResponseEntity<ReportResponse> generateReport(
            @RequestBody ReportGenerationRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            // Get or create analysis for the address
            TransactionAnalysis analysis = analysisRepository.findByAddressAndUser(request.getAddress(), user)
                    .orElseGet(() -> threatService.analyzeTransaction(request.getAddress(), user));
            
            // Generate PDF report
            String reportUrl = pdfReportService.generateTransactionAnalysisReport(analysis, user);
            
            return ResponseEntity.ok(new ReportResponse(reportUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Report>> getUserReports(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            List<Report> reports = reportRepository.findByUserOrderByCreatedAtDesc(user);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/address/{address}")
    public ResponseEntity<List<Report>> getReportsByAddress(
            @PathVariable String address,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            List<Report> reports = reportRepository.findByAddressAndUser(address, user);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{filename}/url")
    public ResponseEntity<Map<String, String>> getReportUrl(@PathVariable String filename) {
        try {
        	String s3ObjectKey = "reports/" + filename;
            String url = s3Service.generatePresignedUrl(s3ObjectKey);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Could not generate URL"));
        }
    }
}

