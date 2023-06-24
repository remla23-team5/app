package org.remla.group5.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table
public class Restaurant {
    @Id
    private String name;
    @OneToMany(mappedBy = "restaurant")
    private List<Review> reviews;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Review> getReviews() {
        return this.reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
