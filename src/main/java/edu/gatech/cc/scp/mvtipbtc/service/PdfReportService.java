package edu.gatech.cc.scp.mvtipbtc.service;

import edu.gatech.cc.scp.mvtipbtc.model.TransactionAnalysis;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.model.Report;
import edu.gatech.cc.scp.mvtipbtc.repository.ReportRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfReportService {
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private S3Service s3Service;
    
    private PdfWriter writer;
    
    public String generateTransactionAnalysisReport(TransactionAnalysis analysis, User user) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer = PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Add header
        addHeader(document, analysis);
        
        // Add executive summary
        addExecutiveSummary(document, analysis);
        
        // Add detailed analysis
        addDetailedAnalysis(document, analysis);
        
        // Add risk metrics
        addRiskMetrics(document, analysis);
        
        // Add recommendations
        addRecommendations(document, analysis);
        
        // Add footer
        addFooter(document);
        
        document.close();
        
        // Upload to S3
        String fileName = generateFileName(analysis);
        String s3Key = s3Service.uploadReport(baos.toByteArray(), fileName);
        
        // Save report record
        Report report = new Report(fileName, s3Key, analysis.getAddress(), "TRANSACTION_ANALYSIS", user);
        reportRepository.save(report);
        
        return s3Service.getReportUrl(s3Key);
    }
    
    private void addHeader(Document document, TransactionAnalysis analysis) throws DocumentException {
        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Bitcoin Threat Intelligence Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Subtitle
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, BaseColor.GRAY);
        Paragraph subtitle = new Paragraph("Transaction Analysis for Address: " + analysis.getAddress(), subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(30);
        document.add(subtitle);
        
        // Report metadata
        PdfPTable metaTable = new PdfPTable(2);
        metaTable.setWidthPercentage(100);
        metaTable.setSpacingAfter(20);
        
        addMetaRow(metaTable, "Report Generated:", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addMetaRow(metaTable, "Analysis Date:", analysis.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addMetaRow(metaTable, "Risk Level:", analysis.getRiskLevel());
        addMetaRow(metaTable, "Risk Score:", analysis.getRiskScore() + "/100");
        
        document.add(metaTable);
    }
    
    private void addExecutiveSummary(Document document, TransactionAnalysis analysis) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
        Paragraph sectionTitle = new Paragraph("Executive Summary", sectionFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
        
        /*Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
        Paragraph summary = new Paragraph(analysis.getAnalysis(), bodyFont);
        summary.setSpacingAfter(20);
        summary.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(summary);*/
        
        ByteArrayInputStream htmlStream = new ByteArrayInputStream(analysis.getAnalysis().getBytes(StandardCharsets.UTF_8));
        try {
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, htmlStream, null, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        // Risk level indicator
        BaseColor riskColor = getRiskColor(analysis.getRiskLevel());
        Font riskFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, riskColor);
        Paragraph riskIndicator = new Paragraph("RISK LEVEL: " + analysis.getRiskLevel().toUpperCase(), riskFont);
        riskIndicator.setAlignment(Element.ALIGN_CENTER);
        riskIndicator.setSpacingBefore(20);
        riskIndicator.setSpacingAfter(20);
        document.add(riskIndicator);
    }
    
    private void addDetailedAnalysis(Document document, TransactionAnalysis analysis) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
        Paragraph sectionTitle = new Paragraph("Detailed Transaction Analysis", sectionFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
        
        PdfPTable detailTable = new PdfPTable(2);
        detailTable.setWidthPercentage(100);
        detailTable.setSpacingAfter(20);
        
        addDetailRow(detailTable, "Bitcoin Address", analysis.getAddress());
        addDetailRow(detailTable, "Total Received", analysis.getTotalReceived() + " BTC");
        addDetailRow(detailTable, "Total Sent", analysis.getTotalSent() + " BTC");
        addDetailRow(detailTable, "Current Balance", analysis.getCurrentBalance() + " BTC");
        addDetailRow(detailTable, "Number of Transactions", analysis.getNumberOfTransactions().toString());
        addDetailRow(detailTable, "Network Confirmations", analysis.getNetworkConfirmations().toString());
        addDetailRow(detailTable, "First Seen", analysis.getFirstSeen().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        document.add(detailTable);
    }
    
    private void addRiskMetrics(Document document, TransactionAnalysis analysis) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
        Paragraph sectionTitle = new Paragraph("Risk Assessment Metrics", sectionFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
        
        PdfPTable riskTable = new PdfPTable(2);
        riskTable.setWidthPercentage(100);
        riskTable.setSpacingAfter(20);
        
        addRiskRow(riskTable, "Overall Risk Score", analysis.getRiskScore() + "/100");
        addRiskRow(riskTable, "Mixing Probability", analysis.getMixingProbability() + "%");
        addRiskRow(riskTable, "Illicit Fund Sources", analysis.getIllicitFundSources() + "%");
        addRiskRow(riskTable, "Dark Market Connections", analysis.getDarkMarketConnections() + "%");
        
        document.add(riskTable);
    }
    
    private void addRecommendations(Document document, TransactionAnalysis analysis) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
        Paragraph sectionTitle = new Paragraph("Recommendations", sectionFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
        
        Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
        
        String recommendations = getRecommendations(analysis.getRiskLevel(), analysis.getRiskScore());
        Paragraph recParagraph = new Paragraph(recommendations, bodyFont);
        recParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
        recParagraph.setSpacingAfter(20);
        document.add(recParagraph);
    }
    
    private void addFooter(Document document) throws DocumentException {
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
        Paragraph footer = new Paragraph("This report was generated by the AI-Driven Multi-Vector Threat Intelligence Platform for Bitcoin. " +
                "The analysis is based on blockchain data, threat intelligence feeds, and AI-powered risk assessment algorithms.", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
    }
    
    private void addMetaRow(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
        
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    private void addDetailRow(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.DARK_GRAY);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
        
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        labelCell.setPadding(8);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(8);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    private void addRiskRow(PdfPTable table, String metric, String value) {
        Font metricFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);
        
        PdfPCell metricCell = new PdfPCell(new Phrase(metric, metricFont));
        metricCell.setBorder(Rectangle.BOTTOM);
        metricCell.setPadding(8);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setPadding(8);
        
        table.addCell(metricCell);
        table.addCell(valueCell);
    }
    
    private BaseColor getRiskColor(String riskLevel) {
        switch (riskLevel.toLowerCase()) {
            case "critical": return BaseColor.RED;
            case "high": return new BaseColor(255, 87, 34);
            case "medium": return BaseColor.ORANGE;
            case "low": return BaseColor.GREEN;
            default: return BaseColor.GRAY;
        }
    }
    
    private String getRecommendations(String riskLevel, int riskScore) {
        switch (riskLevel.toLowerCase()) {
            case "critical":
                return "IMMEDIATE ACTION REQUIRED: This address poses a critical risk and should be flagged for immediate investigation. " +
                       "Consider blocking transactions and reporting to relevant authorities. Enhanced due diligence is strongly recommended.";
            case "high":
                return "HIGH RISK DETECTED: This address requires enhanced monitoring and additional verification procedures. " +
                       "Consider implementing additional compliance checks and transaction limits.";
            case "medium":
                return "MODERATE RISK: While not immediately dangerous, this address should be monitored more closely. " +
                       "Consider periodic reviews and standard compliance procedures.";
            case "low":
                return "LOW RISK: This address appears to have minimal risk indicators. Standard monitoring procedures are sufficient.";
            default:
                return "Risk assessment completed. Please review the detailed analysis above for specific recommendations.";
        }
    }
    
    private String generateFileName(TransactionAnalysis analysis) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String addressPrefix = analysis.getAddress().substring(0, Math.min(8, analysis.getAddress().length()));
        return String.format("threat_analysis_%s_%s.pdf", addressPrefix, timestamp);
    }
}

