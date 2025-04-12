package com.example.application.data;

import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class MovieIdea extends AbstractEntity {

    private String userName;
    private String movieTitle;
    private String genre;
    private LocalDate dateAdded;

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

}
