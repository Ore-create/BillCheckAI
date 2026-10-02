console.log("BillCheck AI JavaScript loaded");


const fileInput = document.getElementById("billFile");
const fileName = document.getElementById("fileName");
const analyzeButton = document.getElementById("analyzeButton");

const loading = document.getElementById("loading");
const results = document.getElementById("results");
const resultContent = document.getElementById("resultContent");


/* ============================
   FILE SELECTION
============================ */

fileInput.addEventListener("change", function () {

    console.log("File selected");

    if (this.files.length > 0) {

        const file = this.files[0];

        console.log("File:", file.name);

        fileName.textContent = "Selected: " + file.name;

        analyzeButton.disabled = false;

    }

});


/* ============================
   ANALYZE BUTTON
============================ */

analyzeButton.addEventListener("click", async function () {

    console.log("ANALYZE BUTTON CLICKED");


    if (!fileInput.files.length) {

        alert("Please select a bill first.");

        return;

    }


    const file = fileInput.files[0];

    console.log("Uploading:", file.name);


    const formData = new FormData();

    formData.append("bill", file);


    loading.style.display = "block";

    results.style.display = "none";

    analyzeButton.disabled = true;


    try {

        console.log("Sending POST request to /analyze");


        const response = await fetch("/analyze", {

            method: "POST",

            body: formData

        });


        console.log("Response status:", response.status);


        const responseText = await response.text();

        console.log("Response:", responseText);


        let data;

        try {

            data = JSON.parse(responseText);

        } catch (error) {

            console.error("JSON error:", error);

            alert("The server returned an invalid response.");

            return;

        }


        if (data.error) {

            alert(data.error);

            return;

        }


        displayResults(data);


    } catch (error) {

        console.error("ERROR:", error);

        alert(
            "Something went wrong while analyzing the bill."
        );

    }


    finally {

        loading.style.display = "none";

        analyzeButton.disabled = false;

    }

});


/* ============================
   DISPLAY RESULTS
============================ */

function displayResults(data) {

    const info = data.bill_info || {};

    let html = `

        <div class="result-card">

            <h3>Bill Information</h3>

            <p>
                <strong>Provider:</strong>
                ${info.provider || "Not found"}
            </p>

            <p>
                <strong>Bill Number:</strong>
                ${info.bill_number || "Not found"}
            </p>

            <p>
                <strong>Bill Date:</strong>
                ${info.bill_date || "Not found"}
            </p>

            <p>
                <strong>Due Date:</strong>
                ${info.due_date || "Not found"}
            </p>

            <p>
                <strong>Billing Period:</strong>
                ${info.billing_period || "Not found"}
            </p>

            <p>
                <strong>Currency:</strong>
                ${info.currency || "Not found"}
            </p>

            <p>
                <strong>Subtotal:</strong>
                ${info.subtotal || "Not found"}
            </p>

            <p>
                <strong>VAT / Tax:</strong>
                ${info.tax_or_vat || "Not found"}
            </p>

            <p>
                <strong>Total:</strong>
                ${info.total_amount || "Not found"}
            </p>

            <p>
                <strong>Amount Due:</strong>
                ${info.amount_due || "Not found"}
            </p>

        </div>
    `;


    if (data.charges && data.charges.length > 0) {

        html += `

            <div class="result-card">

                <h3>Charges</h3>

                <ul>
        `;


        data.charges.forEach(function (charge) {

            html += `

                <li>
                    <strong>
                        ${charge.description || "Unknown charge"}
                    </strong>

                    — ${charge.amount || ""}

                    <span>
                        ${charge.category || ""}
                    </span>
                </li>

            `;

        });


        html += `

                </ul>

            </div>
        `;

    }


    if (data.insights && data.insights.length > 0) {

        html += `

            <div class="result-card">

                <h3>AI Insights</h3>
        `;


        data.insights.forEach(function (insight) {

            html += `

                <div class="insight">

                    <strong>
                        ${insight.title || "Insight"}
                    </strong>

                    <p>
                        ${insight.description || ""}
                    </p>

                </div>

            `;

        });


        html += `</div>`;

    }


    if (data.summary) {

        html += `

            <div class="result-card">

                <h3>Summary</h3>

                <p>
                    ${data.summary}
                </p>

            </div>

        `;

    }


    resultContent.innerHTML = html;

    results.style.display = "block";

}