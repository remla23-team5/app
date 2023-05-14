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
import java.util.Map;

@Controller
public class AppController {
    @GetMapping("/")
    public String getPage() {
        return "index";
    }

    @PostMapping("/")
    public String submitReview(@ModelAttribute Review review, Model model) {
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

                model.addAttribute("review", body.get("review"));
                model.addAttribute("sentiment", body.get("result"));

                return "index";
            } catch (IOException e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        }

        model.addAttribute("review", review.content());
        model.addAttribute("sentiment", -1);

        return "index";
    }

    @PostMapping(path="/agree")
    public String agreeWithSentiment(@RequestBody Review review) {
//        Store necessary data for monitoring
        System.out.println("Agree " + review.content() + " " + review.sentiment());
        return "index";
    }

    @PostMapping("/disagree")
    public String disagreeWithSentiment(@RequestBody Review review) {
//        Store necessary data for monitoring
        System.out.println("Disagree " + review.content() + " " + review.sentiment());
        return "index";
    }
}
