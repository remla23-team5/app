function hideError() {
    document.getElementById("error").style.display = "none"
}

function changeUrl(newUrl) {
    window.location.href = newUrl
}

function sendUserFeedback(id, restaurantName, content, sentiment, correctness) {
    fetch("/evaluate", {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            "id": id,
            "restaurant": {
                "name": restaurantName
            },
            "content": content,
            "sentiment": sentiment,
            "correctness": correctness
        })
    }).then(_ => changeUrl("/view/reviews/" + restaurantName))
}
