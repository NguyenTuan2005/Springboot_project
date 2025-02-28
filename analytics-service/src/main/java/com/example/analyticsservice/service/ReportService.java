package com.example.analyticsservice.service;

import com.example.analyticsservice.dto.SEMrushDataDTO;
import com.example.analyticsservice.model.CompetitorTrend;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final SEMrushService semrushService;

    private final CompetitorTrendService competitorTrendService;

    public ReportService(SEMrushService semrushService, CompetitorTrendService competitorTrendService) {
        this.semrushService = semrushService;
        this.competitorTrendService = competitorTrendService;
    }

    public byte[] generateAnalyticsReport(String domain) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            SEMrushDataDTO semrushData = semrushService.getDomainAnalytics(domain);
            List<CompetitorTrend> trends = competitorTrendService.getTrendsForKeyword(domain);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                // Add title
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 16);
                content.newLineAtOffset(50, 750);
                content.showText("Analytics Report - " + domain);
                content.endText();

                // Add SEMrush data
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, 700);
                content.showText("Traffic: " + semrushData.getTraffic());
                content.newLineAtOffset(0, -20);
                content.showText("Keywords: " + semrushData.getKeywords());
                content.newLineAtOffset(0, -20);
                content.showText("Traffic Cost: $" + semrushData.getTrafficCost());
                content.endText();

                // Add competitor trends
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.newLineAtOffset(50, 600);
                content.showText("Competitor Trends");
                content.endText();

                float y = 550;
                for (CompetitorTrend trend : trends) {
                    content.beginText();
                    content.setFont(PDType1Font.HELVETICA, 12);
                    content.newLineAtOffset(50, y);
                    content.showText(trend.toString());
                    content.endText();
                    y -= 20;
                }
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                document.save(baos);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report: " + e.getMessage());
        }
    }
}