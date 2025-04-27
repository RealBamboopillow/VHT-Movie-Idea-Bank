package com.example.application.views.ideas;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.application.data.Genres;
import com.example.application.data.MovieIdea;
import com.example.application.services.MovieIdeaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import jakarta.persistence.criteria.*;


public class Filters extends Div implements Specification<MovieIdea>{

    private TextField movieTitle = new TextField("Movie title");

    private ComboBox<String> genre = new ComboBox<>("Genre");

    private Button searchButton = new Button("Search");

    private Button clearButton = new Button("Clear");

    public Filters(Runnable onSearch, MovieIdeaService movieIdeaService){

        clearButton.addClickListener( e-> {
            movieTitle.clear();
            genre.clear();
            onSearch.run();
         });

        searchButton.addClickListener(e->{
            onSearch.run();
            Notification.show("Search button pressed!");
        });

        HorizontalLayout layout = new HorizontalLayout();
        searchButton.getStyle().setBackground("blue");
        searchButton.getStyle().setColor("white");
        clearButton.getStyle().setBackground("red");
        clearButton.getStyle().setColor("white");
        genre.setItems(Genres.GENRES);
        layout.add(movieTitle, genre, searchButton, clearButton);
        add(layout);

    }

    @Override
    public Predicate toPredicate(Root<MovieIdea> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();

        if (!movieTitle.isEmpty()){
            Predicate movieTitlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("movieTitle")),
                    "%" + movieTitle.getValue().toLowerCase() + "%");
                    predicateList.add(movieTitlePredicate);
        }
        if(!genre.isEmpty()){
            Predicate genrePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("genre")),
            genre.getValue().toLowerCase() + "%");
                predicateList.add(genrePredicate);
        }

        return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));

        // throw new UnsupportedOperationException("Unimplemented method 'toPredicate'");
    }
}
