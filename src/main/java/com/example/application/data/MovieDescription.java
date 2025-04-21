package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;

@Entity
public class MovieDescription extends AbstractEntity{

    @Lob
    private String description;

    //Getters'n Setters

    public String getText() {
        return description;
    }
    public void setText(String description) {
        this.description = description;
    }
}
