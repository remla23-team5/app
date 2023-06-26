package org.remla.group5.models;

import jakarta.persistence.*;

@Entity
@Table
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESTAURANT_NAME")
    private Restaurant restaurant;
    @Column
    private String content;
    @Column
    private Boolean sentiment;
    @Column
    private Boolean correctness;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Restaurant getRestaurant() {
        return this.restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getSentiment() {
        return this.sentiment;
    }

    public void setSentiment(Boolean sentiment) {
        this.sentiment = sentiment;
    }

    public Boolean getCorrectness() {
        return correctness;
    }

    public void setCorrectness(Boolean correctness) {
        this.correctness = correctness;
    }
}
