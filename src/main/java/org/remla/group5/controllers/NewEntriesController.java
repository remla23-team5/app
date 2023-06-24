package org.remla.group5.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.remla.group5.models.Restaurant;
import org.remla.group5.models.Review;
import org.remla.group5.services.RestaurantService;
import org.remla.group5.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/add")
public class NewEntriesController {
    private final RestaurantService restaurantService;
    private final ReviewService reviewService;

    @Autowired
    public NewEntriesController(RestaurantService restaurantService, ReviewService reviewService) {
        this.restaurantService = restaurantService;
        this.reviewService = reviewService;
    }

    @GetMapping("/restaurant")
    public String addRestaurantPage() {
        return "addRestaurant";
    }

    @PostMapping("/restaurant")
    public String addRestaurant(@ModelAttribute Restaurant restaurant, Model model) {
        if (restaurant.getName().isBlank()) {
            model.addAttribute("previousName", "");
            return "addRestaurant";
        }

        try {
            restaurantService.addRestaurant(restaurant);
        } catch (RuntimeException e) {
            model.addAttribute("previousName", restaurant.getName());
            return "addRestaurant";
        }

        return "redirect:/view/restaurants";
    }

    @GetMapping("/review/{restaurantName}")
    public String addReviewPage(@PathVariable @NonNull String restaurantName, Model model) {
        Optional<Restaurant> restaurant = restaurantService.getRestaurantByName(restaurantName);

        if (restaurant.isPresent()) {
            model.addAttribute("restaurant", restaurant.get());
        } else {
            model.addAttribute("errorMessage", "A restaurant with name '" + restaurantName + "' does not exist in the system.");
            return "error";
        }

        return "addReview";
    }

    @PostMapping("/review/{restaurantName}")
    public String addReview(@PathVariable @NonNull String restaurantName, @ModelAttribute Review review, Model model) {
        Optional<Restaurant> restaurant = restaurantService.getRestaurantByName(restaurantName);

        if (restaurant.isEmpty()) {
            model.addAttribute("errorMessage", "A restaurant with name '" + restaurantName + "' does not exist in the system.");
            return "error";
        }

        model.addAttribute("restaurant", restaurant.get());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> map = new HashMap<>();
        map.put("review", review.getContent());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
        String modelServiceUrl = System.getenv("model_service_url");
        ResponseEntity<String> response = new RestTemplate().postForEntity((modelServiceUrl == null) ? "http://localhost:8080/predict" : modelServiceUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                Map<String, Object> body = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

                review.setRestaurant(restaurant.get());
                review.setSentiment((Integer) body.get("result") != 0);
                Review savedReview = reviewService.addReview(review);

                model.addAttribute("review", savedReview);
                model.addAttribute("sentiment", body.get("result"));

                return "addReview";
            } catch (IOException e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        }

        model.addAttribute("review", review.getContent());
        model.addAttribute("sentiment", -1);

        return "addReview";
    }
}
