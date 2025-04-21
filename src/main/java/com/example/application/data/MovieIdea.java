package com.example.application.data;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class MovieIdea extends AbstractEntity {

    private String userName;
    private String movieTitle;
    private String genre;
    private LocalDate dateAdded;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "description_id", referencedColumnName = "id")
    private MovieDescription description;

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getMovieTitle() {
        return movieTitle;
    }
    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public LocalDate getDateAdded() {
        return dateAdded;
    }
    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }
    public MovieDescription getDescription() {
        return description;
    }
    public void setDescription(MovieDescription description) {
        this.description = description;
    }

}
