package com.example.analyticsservice.service;

import com.example.analyticsservice.dto.SEMrushDataDTO;
import com.example.analyticsservice.model.CompetitorTrend;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.util.List;

public interface ReportService {
    SEMrushDataDTO getSemrushData(String domain);

    void addContentToPdf(PDDocument document, String domain,
                                   SEMrushDataDTO semrushData,
                                   List<CompetitorTrend> trends) throws IOException;

    byte[] generateAnalyticsReport(String domain);
}