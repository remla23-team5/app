package remla.group23.app.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import remla.group23.app.models.Review;

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
        ResponseEntity<String> response = new RestTemplate().postForEntity("http://localhost:8080/predict", request, String.class);

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
}
