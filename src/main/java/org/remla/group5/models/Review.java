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
    private Boolean userFeedback;
    @Column
    private Boolean isExperimental;

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

    public Boolean getUserFeedback() {
        return this.userFeedback;
    }

    public void setUserFeedback(Boolean userFeedback) {
        this.userFeedback = userFeedback;
    }

    public Boolean getIsExperimental() {
        return this.isExperimental;
    }

    public void setIsExperimental(Boolean isExperimental) {
        this.isExperimental = isExperimental;
    }
}
