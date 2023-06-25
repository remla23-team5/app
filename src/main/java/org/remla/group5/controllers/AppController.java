package org.remla.group5.controllers;

import org.remla.group5.models.Review;
import org.remla.group5.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class AppController {
    ReviewService reviewService;

    @Autowired
    public AppController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public String getHomepage() {
        System.out.println(new Date() + " [MY LOG]: get homepage");
        return "redirect:/view/restaurants";
    }

    @PostMapping(path="/evaluate")
    public String evaluatePrediction(@RequestBody Review review, Model model) {
        System.out.println(new Date() + " [MY LOG]: evaluate");
        try {
            reviewService.updateReview(review);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Error!");
            return "error";
        }

        return "redirect:/view/reviews/" + review.getRestaurant().getName();
    }

    @GetMapping(value = "/metrics", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getMetrics() {
        System.out.println(new Date() + " [MY LOG]: get metrics");
        List<Review> allReviews = reviewService.getAllReviews();

        int reviews = allReviews.size();
        int truePositives = 0;
        int falsePositives = 0;
        int trueNegatives = 0;
        int falseNegatives = 0;
        int positiveEvaluations = 0;
        int negativeEvaluations = 0;
        int errors = 0;

        LinkedHashMap<String, Integer> reviewLengths = new LinkedHashMap<>();
        reviewLengths.put("10", 0);
        reviewLengths.put("30", 0);
        reviewLengths.put("50", 0);
        reviewLengths.put("100", 0);
        reviewLengths.put("+Inf", 0);

        for (Review review : allReviews) {
            if (review.getSentiment() == null) {
                errors++;
            } else if(review.getSentiment()) {
                positiveEvaluations++;

                if (review.getCorrectness() != null) {
                    if (review.getCorrectness()) {
                        truePositives++;
                    } else {
                        falsePositives++;
                    }
                }
            } else {
                negativeEvaluations++;

                if (review.getCorrectness() != null) {
                    if (review.getCorrectness()) {
                        trueNegatives++;
                    } else {
                        falseNegatives++;
                    }
                }
            }

            int reviewLength = review.getContent().length();

            if (reviewLength < 10) {
                reviewLengths.put("10", reviewLengths.get("10") + 1);
            } else if (reviewLength < 30) {
                reviewLengths.put("30", reviewLengths.get("30") + 1);
            } else if (reviewLength < 50) {
                reviewLengths.put("50", reviewLengths.get("50") + 1);
            } else if (reviewLength < 100) {
                reviewLengths.put("100", reviewLengths.get("100") + 1);
            } else {
                reviewLengths.put("+Inf", reviewLengths.get("+Inf") + 1);
            }
        }

        int positiveFeedbacks = truePositives + trueNegatives;
        int negativeFeedbacks = falsePositives + falseNegatives;
        int numFeedbacks = positiveFeedbacks + negativeFeedbacks;

        StringBuilder result = new StringBuilder();

//        Add a counter metric for the number of received reviews
        result.append("# HELP num_reviews This counter metric depicts the number of received reviews.\n");
        result.append("# TYPE num_reviews counter\n");
        result.append("num_reviews ").append(reviews).append("\n");

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
        for (String length : reviewLengths.keySet()) {
            acc += reviewLengths.get(length);
            result.append("reviews_of_length_bucket{lt=\"").append(i).append("_").append(length).append("\"} ").append(acc).append("\n");
            i++;
        }
        result.append("reviews_of_length_count ").append(reviews).append("\n");

//        Add a summary for the evaluations based on their sentiment
        result.append("# HELP num_evaluations_per_sentiment This summary metric depicts the number of evaluations per sentiment.\n");
        result.append("# TYPE num_evaluations_per_sentiment summary\n");
        result.append("num_evaluations_per_sentiment{sentiment=\"Positive\"} ").append(positiveEvaluations).append("\n");
        result.append("num_evaluations_per_sentiment{sentiment=\"Negative\"} ").append(negativeEvaluations).append("\n");
        result.append("num_evaluations_per_sentiment{sentiment=\"Unsuccessful\"} ").append(errors).append("\n");
        result.append("num_evaluations_per_sentiment_count ").append(reviews).append("\n");

//        Add a summary for the evaluations based on their type
        result.append("# HELP num_evaluations_per_type This summary metric depicts the number of evaluations per type.\n");
        result.append("# TYPE num_evaluations_per_type summary\n");
        result.append("num_evaluations_per_type{type=\"true-positive\"} ").append(truePositives).append("\n");
        result.append("num_evaluations_per_type{type=\"false-positive\"} ").append(falsePositives).append("\n");
        result.append("num_evaluations_per_type{type=\"true-negatives\"} ").append(trueNegatives).append("\n");
        result.append("num_evaluations_per_type{type=\"false-negatives\"} ").append(falseNegatives).append("\n");
        result.append("num_evaluations_per_type_count ").append(numFeedbacks).append("\n");

        return result.toString();
    }
}
