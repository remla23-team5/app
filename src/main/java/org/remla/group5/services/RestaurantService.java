package org.remla.group5.services;

import org.remla.group5.models.Restaurant;
import org.remla.group5.repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> getRestaurantByName(String restaurantName) {
        return restaurantRepository.findByName(restaurantName);
    }

    public Restaurant addRestaurant(Restaurant restaurant) {
        String restaurantName = restaurant.getName();

        if (restaurantName != null && restaurantRepository.existsByName(restaurantName)) {
            throw new RuntimeException(String.format("A restaurant with name %s already exists in the system.", restaurantName));
        }

        return restaurantRepository.saveAndFlush(restaurant);
    }

    public Restaurant updateRestaurant(Restaurant restaurant) {
        String restaurantName = restaurant.getName();

        if (restaurantName != null && !restaurantRepository.existsByName(restaurantName)) {
            throw new RuntimeException(String.format("A restaurant with name %s does not exist in the system.", restaurantName));
        }

        return restaurantRepository.saveAndFlush(restaurant);
    }
}
