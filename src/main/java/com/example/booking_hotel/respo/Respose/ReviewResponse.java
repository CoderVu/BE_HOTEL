package com.example.booking_hotel.respo.Respose;

public class ReviewResponse {
    private String userEmail;
    private int stars;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String comment;

    public ReviewResponse() {
        this.userEmail = userEmail;
        this.stars = stars;
        this.comment = comment;
    }

}