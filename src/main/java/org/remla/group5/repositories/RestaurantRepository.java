package org.remla.group5.repositories;

import org.remla.group5.models.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
    boolean existsByName(String name);
    Optional<Restaurant> findByName(String name);
}
