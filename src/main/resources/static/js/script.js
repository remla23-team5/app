function sendAgreement(sentiment, agreement) {
    fetch("/" + ((agreement) ? "agree" : "disagree"), {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            "content": document.getElementById("review-field").value,
            "sentiment": sentiment === 1
        })
    })
        .then(response => response.text())
        .then(data => document.write(data))
}

function skip() {
    fetch("/", {
        method: "GET"
    })
        .then(response => response.text())
        .then(data => document.write(data))
}
