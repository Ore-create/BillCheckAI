package com.billcheck.ai;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class BillController {

    private final OpenAIService openAIService;
    private final PdfReportService pdfReportService;

    public BillController(
            OpenAIService openAIService,
            PdfReportService pdfReportService) {

        this.openAIService = openAIService;
        this.pdfReportService = pdfReportService;
    }


    /*
     * HOME PAGE
     */

    @GetMapping("/")
    public String home() {

        return "index";
    }


    /*
     * TEST AI
     */

    @GetMapping("/test-ai")
    @ResponseBody
    public String testAI() {

        return openAIService.testAI();
    }


    /*
     * ANALYZE BILL
     */

    @PostMapping("/analyze")
    @ResponseBody
    public String analyzeBill(
            @RequestParam("bill") MultipartFile bill)
            throws IOException {

        if (bill.isEmpty()) {

            return """
                    {
                        "error": "Please select a bill first."
                    }
                    """;
        }

        return openAIService.analyzeBill(bill);
    }


    /*
     * GENERATE PDF REPORT
     */

    @PostMapping("/download-report")
    public ResponseEntity<byte[]> downloadReport(
            @RequestBody String reportText) {

        try {

            byte[] pdf =
                    pdfReportService.generatePdf(reportText);


            return ResponseEntity
                    .ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"BillCheck-AI-Report.pdf\""
                    )
                    .contentType(
                            MediaType.APPLICATION_PDF
                    )
                    .body(pdf);


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();

        }

    }

}