package com.billcheck.ai;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfReportService {

    public byte[] generatePdf(String reportText) throws Exception {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content =
                    new PDPageContentStream(document, page);

            // Title
            content.beginText();

            content.setFont(
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA_BOLD
                    ),
                    22
            );

            content.newLineAtOffset(50, 750);

            content.showText("BillCheck AI");

            content.endText();


            // Subtitle
            content.beginText();

            content.setFont(
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA
                    ),
                    12
            );

            content.newLineAtOffset(50, 725);

            content.showText("Bill Analysis Report");

            content.endText();


            // Divider
            content.setLineWidth(1);

            content.moveTo(50, 710);

            content.lineTo(545, 710);

            content.stroke();


            // Report text
            float y = 685;

            String[] lines =
                    reportText.split("\\r?\\n");

            for (String line : lines) {

                // Remove characters PDFBox Helvetica cannot display
                String cleanLine = line
                        .replace("•", "-")
                        .replace("—", "-")
                        .replace("–", "-")
                        .replace("₹", "INR")
                        .replace("€", "EUR")
                        .replace("’", "'")
                        .replace("“", "\"")
                        .replace("”", "\"");

                // Remove any remaining unsupported characters
                cleanLine = cleanLine.replaceAll(
                        "[^\\x20-\\x7E]",
                        ""
                );

                if (cleanLine.length() == 0) {
                    y -= 10;
                    continue;
                }

                // Split long lines
                while (cleanLine.length() > 85) {

                    String part =
                            cleanLine.substring(0, 85);

                    content.beginText();

                    content.setFont(
                            new PDType1Font(
                                    Standard14Fonts.FontName.HELVETICA
                            ),
                            10
                    );

                    content.newLineAtOffset(
                            50,
                            y
                    );

                    content.showText(part);

                    content.endText();

                    y -= 16;

                    cleanLine =
                            cleanLine.substring(85);

                    if (y < 60) {

                        content.close();

                        page =
                                new PDPage(PDRectangle.A4);

                        document.addPage(page);

                        content =
                                new PDPageContentStream(
                                        document,
                                        page
                                );

                        y = 750;
                    }
                }

                content.beginText();

                content.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA
                        ),
                        10
                );

                content.newLineAtOffset(
                        50,
                        y
                );

                content.showText(cleanLine);

                content.endText();

                y -= 16;

                if (y < 60) {

                    content.close();

                    page =
                            new PDPage(PDRectangle.A4);

                    document.addPage(page);

                    content =
                            new PDPageContentStream(
                                    document,
                                    page
                            );

                    y = 750;
                }
            }

            content.close();

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            document.save(output);

            return output.toByteArray();
        }
    }
}