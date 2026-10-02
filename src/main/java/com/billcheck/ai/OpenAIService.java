package com.billcheck.ai;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OpenAIService {

    /*
     * DEMO MODE
     *
     * This version does not call the OpenAI API.
     * It allows BillCheck AI to work without API credits.
     */

    public String testAI() {

        return "Hello from BillCheck AI! Demo mode is working.";

    }


    public String analyzeBill(MultipartFile bill) {

        try {

            // Check that a file was actually uploaded
            if (bill == null || bill.isEmpty()) {

                return """
                        {
                            "error": "Please select a bill first."
                        }
                        """;
            }


            // Check that the uploaded file is an image
            String contentType = bill.getContentType();

            if (contentType == null ||
                    !contentType.startsWith("image/")) {

                return """
                        {
                            "error": "Please upload a JPG or PNG bill image."
                        }
                        """;
            }


            /*
             * DEMO BILL ANALYSIS
             *
             * This simulates the type of structured response
             * that the AI would return.
             */

            return """
                    {
                      "bill_info": {
                        "provider": "Example Utilities",
                        "bill_number": "BILL-2026-10482",
                        "bill_date": "25 September 2026",
                        "due_date": "15 October 2026",
                        "billing_period": "25 August 2026 - 24 September 2026",
                        "currency": "AED",
                        "total_amount": "AED 486.50",
                        "subtotal": "AED 463.33",
                        "tax_or_vat": "AED 23.17",
                        "previous_balance": "AED 0.00",
                        "amount_due": "AED 486.50"
                      },

                      "charges": [
                        {
                          "description": "Monthly service charge",
                          "amount": "AED 250.00",
                          "category": "Service"
                        },
                        {
                          "description": "Usage charges",
                          "amount": "AED 135.50",
                          "category": "Usage"
                        },
                        {
                          "description": "Additional services",
                          "amount": "AED 77.83",
                          "category": "Additional"
                        }
                      ],

                      "insights": [
                        {
                          "type": "info",
                          "title": "Bill received successfully",
                          "description": "The uploaded bill was successfully processed by BillCheck AI."
                        },
                        {
                          "type": "info",
                          "title": "VAT detected",
                          "description": "VAT of AED 23.17 was identified on the bill."
                        },
                        {
                          "type": "warning",
                          "title": "Check additional services",
                          "description": "Additional services account for AED 77.83 of the bill. Review these charges if they were not expected."
                        }
                      ],

                      "summary": "The bill has a total amount due of AED 486.50. The largest charge is the monthly service charge at AED 250.00, followed by usage charges of AED 135.50. VAT of AED 23.17 has also been applied."
                    }
                    """;


        } catch (Exception e) {

            return """
                    {
                        "error": "Bill analysis failed."
                    }
                    """;

        }

    }

}