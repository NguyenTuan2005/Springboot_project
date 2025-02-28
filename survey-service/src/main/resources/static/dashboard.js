class SurveyDashboard {
    constructor() {
        this.responseRateChart = null
        this.trendChart = null
        this.surveyForm = document.getElementById("surveyForm")
        this.recentSurveysList = document.getElementById("recentSurveys")
    }

    init() {
        this.createResponseRateChart()
        this.createTrendChart()
        this.fetchRecentSurveys()
        this.setupEventListeners()
        this.createJourneyTimeline()
        this.initCROTracking()
    }

    createResponseRateChart() {
        const ctx = document.getElementById("responseRateChart").getContext("2d")
        this.responseRateChart = new Chart(ctx, {
            type: "bar",
            data: {
                labels: ["Survey 1", "Survey 2", "Survey 3", "Survey 4", "Survey 5"],
                datasets: [
                    {
                        label: "Response Rate",
                        data: [65, 59, 80, 81, 56],
                        backgroundColor: "rgba(75, 192, 192, 0.6)",
                        borderColor: "rgba(75, 192, 192, 1)",
                        borderWidth: 1,
                    },
                ],
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                    },
                },
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: "Survey Response Rates",
                    },
                },
            },
        })
    }

    createTrendChart() {
        const ctx = document.getElementById("trendChart").getContext("2d")
        this.trendChart = new Chart(ctx, {
            type: "line",
            data: {
                labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun"],
                datasets: [
                    {
                        label: "Trend Score",
                        data: [12, 19, 3, 5, 2, 3],
                        borderColor: "rgba(255, 99, 132, 1)",
                        tension: 0.1,
                    },
                ],
            },
            options: {
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: "Competitor Trend Scores",
                    },
                },
            },
        })
    }

    async fetchRecentSurveys() {
        try {
            const response = await fetch("/surveys/responses")
            const surveyResponses = await response.json()
            this.displayRecentSurveys(surveyResponses)
        } catch (error) {
            console.error("Error fetching recent surveys:", error)
        }
    }

    displayRecentSurveys(surveyResponses) {
        this.recentSurveysList.innerHTML = ""
        surveyResponses.slice(0, 5).forEach((surveyResponse) => {
            const li = document.createElement("li")
            li.className = "list-group-item"
            li.textContent = surveyResponse.survey.title
            this.recentSurveysList.appendChild(li)
        })
    }

    async createJourneyTimeline() {
        const journeyData = await this.fetchJourneyData(2) // Fetch data for survey with ID 1
        this.renderJourneyTimeline(journeyData)
    }

    async fetchJourneyData(surveyId) {
        try {
            const response = await fetch(`/journey/${surveyId}`)
            return await response.json()
        } catch (error) {
            console.error("Error fetching journey data:", error)
            return []
        }
    }

    renderJourneyTimeline(journeyData) {
        const margin = { top: 20, right: 20, bottom: 30, left: 50 }
        const width = 800 - margin.left - margin.right
        const height = 200 - margin.top - margin.bottom

        const svg = d3
            .select("#journeyTimeline")
            .append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", `translate(${margin.left},${margin.top})`)

        const x = d3
            .scaleTime()
            .range([0, width])
            .domain(d3.extent(journeyData, (d) => new Date(d.timestamp)))

        const y = d3.scalePoint().range([height, 0]).domain(["AWARENESS", "CONSIDERATION", "DECISION"])

        svg.append("g").attr("transform", `translate(0,${height})`).call(d3.axisBottom(x))

        svg.append("g").call(d3.axisLeft(y))

        svg
            .selectAll("circle")
            .data(journeyData)
            .enter()
            .append("circle")
            .attr("cx", (d) => x(new Date(d.timestamp)))
            .attr("cy", (d) => y(d.stage))
            .attr("r", 5)
            .attr("fill", "steelblue")

        svg
            .selectAll("text.label")
            .data(journeyData)
            .enter()
            .append("text")
            .attr("class", "label")
            .attr("x", (d) => x(new Date(d.timestamp)))
            .attr("y", (d) => y(d.stage) - 10)
            .attr("text-anchor", "middle")
            .text((d) => d.touchpoint)
    }

    setupEventListeners() {
        this.surveyForm.addEventListener("submit", this.handleSurveySubmit.bind(this))
    }

    async handleSurveySubmit(event) {
        event.preventDefault()
        const title = document.getElementById("surveyTitle").value
        const targetIndustry = document.getElementById("targetIndustry").value

        try {
            const response = await fetch("/surveys/submit", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ title, targetIndustry }),
            })

            if (response.ok) {
                alert("Survey created successfully!")
                this.surveyForm.reset()
                this.fetchRecentSurveys()
            } else {
                alert("Error creating survey. Please try again.")
            }
        } catch (error) {
            console.error("Error submitting survey:", error)
            alert("Error creating survey. Please try again.")
        }
    }

    initCROTracking() {
        document.querySelectorAll('[data-cta]').forEach(button => {
            button.addEventListener('click', (e) => {
                const buttonId = e.target.getAttribute('data-cta');
                if (window.hj) {
                    window.hj('event', `cta_click_${buttonId}`);
                }
                this.logCTAClick(buttonId);
            });
        });
    }

    async logCTAClick(buttonId) {
        try {
            await fetch('/cro/results', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    buttonId,
                    timestamp: new Date().toISOString()
                })
            });
        } catch (error) {
            console.error('Error logging CTA click:', error);
        }
    }

    async createABTest(testConfig) {
        try {
            const response = await fetch('/cro/tests', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(testConfig)
            });
            return await response.json();
        } catch (error) {
            console.error('Error creating A/B test:', error);
            throw error;
        }
    }
}

async function downloadAnalyticsReport(domain) {
    try {
        const response = await fetch(`/api/analytics/report?domain=${encodeURIComponent(domain)}`, {
            method: 'GET'
        });

        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `${domain}-analytics-report.pdf`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            a.remove();
        } else {
            throw new Error('Failed to generate report');
        }
    } catch (error) {
        console.error('Error downloading report:', error);
        alert('Error downloading analytics report. Please try again.');
    }
}

document.getElementById('downloadReport').addEventListener('click', () => {
    const domain = document.getElementById('domainInput').value;
    if (domain) {
        downloadAnalyticsReport(domain);
    } else {
        alert('Please enter a domain');
    }
});

const dashboard = new SurveyDashboard()
dashboard.init()

