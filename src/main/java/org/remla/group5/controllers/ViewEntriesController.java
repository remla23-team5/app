package org.remla.group5.controllers;

import org.remla.group5.models.Restaurant;
import org.remla.group5.services.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/view")
public class ViewEntriesController {
    private final RestaurantService restaurantService;

    @Autowired
    public ViewEntriesController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/restaurants")
    public String viewRestaurants(Model model) {
        System.out.println(LocalDateTime.now() + " [LOG]: viewRestaurants");

        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "viewRestaurants";
    }

    @GetMapping("/reviews/{restaurantName}")
    public String viewReviewsForRestaurant(@PathVariable @NonNull String restaurantName, Model model) {
        System.out.println(LocalDateTime.now() + " [LOG]: viewReviewsForRestaurant");

        Optional<Restaurant> restaurant = restaurantService.getRestaurantByName(restaurantName);

        if (restaurant.isPresent()) {
            model.addAttribute("restaurant", restaurant.get());
        } else {
            model.addAttribute("errorMessage", "A restaurant with name '" + restaurantName + "' does not exist in the system.");
            return "error";
        }

        return "viewReviews";
    }
}
