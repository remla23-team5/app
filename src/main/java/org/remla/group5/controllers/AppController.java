package org.remla.group5.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.remla.group5.models.Review;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class AppController {
    int reviews;
    int positiveEvaluations;
    int negativeEvaluations;
    int errors;
    int truePositives;
    int falsePositives;
    int trueNegatives;
    int falseNegatives;
    LinkedHashMap<String, Integer> reviewLengths;

    public AppController() {
        this.reviewLengths = new LinkedHashMap<>();
        this.reviewLengths.put("10", 0);
        this.reviewLengths.put("30", 0);
        this.reviewLengths.put("50", 0);
        this.reviewLengths.put("100", 0);
        this.reviewLengths.put("+Inf", 0);
    }

    @GetMapping("/")
    public String getPage() {
        return "index";
    }

    @PostMapping("/")
    public String submitReview(@ModelAttribute Review review, Model model) {
        this.reviews++;

        int reviewLength = review.content().length();
        if (reviewLength < 10) {
            this.reviewLengths.put("10", this.reviewLengths.get("10") + 1);
        } else if (reviewLength < 30) {
            this.reviewLengths.put("30", this.reviewLengths.get("30") + 1);
        } else if (reviewLength < 50) {
            this.reviewLengths.put("50", this.reviewLengths.get("50") + 1);
        } else if (reviewLength < 100) {
            this.reviewLengths.put("100", this.reviewLengths.get("100") + 1);
        } else {
            this.reviewLengths.put("+Inf", this.reviewLengths.get("+Inf") + 1);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> map = new HashMap<>();
        map.put("review", review.content());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
        String modelServiceUrl = System.getenv("model_service_url");
        ResponseEntity<String> response = new RestTemplate().postForEntity((modelServiceUrl == null) ? "http://localhost:8080/predict" : modelServiceUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                Map<String, Object> body = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

                if ((int)body.get("result") == 0) {
                    this.negativeEvaluations++;
                } else {
                    this.positiveEvaluations++;
                }

                model.addAttribute("review", body.get("review"));
                model.addAttribute("sentiment", body.get("result"));

                return "index";
            } catch (IOException e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        }

        this.errors++;

        model.addAttribute("review", review.content());
        model.addAttribute("sentiment", -1);

        return "index";
    }

    @PostMapping(path="/agree")
    public String agreeWithSentiment(@RequestBody Review review) {
        if (review.sentiment()) {
            this.truePositives++;
        } else {
            this.trueNegatives++;
        }

        return "index";
    }

    @PostMapping("/disagree")
    public String disagreeWithSentiment(@RequestBody Review review) {
        if (review.sentiment()) {
            this.falsePositives++;
        } else {
            this.falseNegatives++;
        }

        return "index";
    }

    @GetMapping(value = "/metrics", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getMetrics() {
        int positiveFeedbacks = this.truePositives + this.trueNegatives;
        int negativeFeedbacks = this.falsePositives + this.falseNegatives;
        int numFeedbacks = positiveFeedbacks + negativeFeedbacks;

        StringBuilder result = new StringBuilder();

//        Add a counter metric for the number of received reviews
        result.append("# HELP num_reviews This counter metric depicts the number of received reviews.\n");
        result.append("# TYPE num_reviews counter\n");
        result.append("num_reviews ").append(this.reviews).append("\n");

//        Add a counter metric for the number of received feedbacks
        result.append("# HELP num_feedbacks This counter metric depicts the number of received feedbacks.\n");
        result.append("# TYPE num_feedbacks counter\n");
        result.append("num_feedbacks ").append(numFeedbacks).append("\n");

//        Add a gauge metric for the accuracy of the model
        result.append("# HELP model_accuracy This gauge metric depicts the accuracy of the model, based on the user feedback.\n");
        result.append("# TYPE model_accuracy gauge\n");
        result.append("model_accuracy ").append((numFeedbacks == 0) ? 1 : (double) positiveFeedbacks / numFeedbacks).append("\n");

//        Add a histogram metric for the number of reviews of different lengths
        result.append("# HELP reviews_of_length This histogram metric depicts the number of reviews of different lengths.\n");
        result.append("# TYPE reviews_of_length histogram\n");
        int acc = 0, i = 0;
        for (String length : this.reviewLengths.keySet()) {
            acc += this.reviewLengths.get(length);
            result.append("reviews_of_length_bucket{lt=\"").append(i).append("_").append(length).append("\"} ").append(acc).append("\n");
            i++;
        }
        result.append("reviews_of_length_count ").append(this.reviews).append("\n");

//        Add a summary for the evaluations based on their sentiment
        result.append("# HELP num_evaluations_per_sentiment This summary metric depicts the number of evaluations per sentiment.\n");
        result.append("# TYPE num_evaluations_per_sentiment summary\n");
        result.append("num_evaluations_per_sentiment{sentiment=\"Positive\"} ").append(this.positiveEvaluations).append("\n");
        result.append("num_evaluations_per_sentiment{sentiment=\"Negative\"} ").append(this.negativeEvaluations).append("\n");
        result.append("num_evaluations_per_sentiment{sentiment=\"Unsuccessful\"} ").append(this.errors).append("\n");
        result.append("num_evaluations_per_sentiment_count ").append(this.reviews).append("\n");

//        Add a summary for the evaluations based on their type
        result.append("# HELP num_evaluations_per_type This summary metric depicts the number of evaluations per type.\n");
        result.append("# TYPE num_evaluations_per_type summary\n");
        result.append("num_evaluations_per_type{type=\"true-positive\"} ").append(this.truePositives).append("\n");
        result.append("num_evaluations_per_type{type=\"false-positive\"} ").append(this.falsePositives).append("\n");
        result.append("num_evaluations_per_type{type=\"true-negatives\"} ").append(this.trueNegatives).append("\n");
        result.append("num_evaluations_per_type{type=\"false-negatives\"} ").append(this.falseNegatives).append("\n");
        result.append("num_evaluations_per_type_count ").append(numFeedbacks).append("\n");

        return result.toString();
    }
}
