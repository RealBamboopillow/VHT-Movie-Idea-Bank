package com.example.application.views.ideas;

import com.example.application.data.Genres;
import com.example.application.data.MovieDescription;
import com.example.application.data.MovieIdea;
import com.example.application.services.MovieIdeaService;
import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationBinder;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Ideas")
@Route("collaborative-master-detail/:movieIdeaID?/:action?(edit)")
@Menu(order = 1, icon = LineAwesomeIconUrl.COLUMNS_SOLID)
@RolesAllowed("USER")
public class IdeasView extends Div implements BeforeEnterObserver {

    private final String MOVIEIDEA_ID = "movieIdeaID";
    private final String MOVIEIDEA_EDIT_ROUTE_TEMPLATE = "collaborative-master-detail/%s/edit";

    private final Grid<MovieIdea> grid = new Grid<>(MovieIdea.class, false);

    //CollaborationAvatarGroup avatarGroup;

    private TextField userName;
    private TextField movieTitle;
    //private TextField genre;
    private ComboBox<String> genre;
    private DatePicker dateAdded;
    private TextArea description;
    private Filters filters;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<MovieIdea> binder = new BeanValidationBinder<>(MovieIdea.class);


    private MovieIdea movieIdea;

    
    //public ArrayList<String> genreItems = new ArrayList<>();
  

    private final MovieIdeaService movieIdeaService;

    public IdeasView(MovieIdeaService movieIdeaService) {
       // this.binder.bindInstanceFields(this);
        this.movieIdeaService = movieIdeaService;
        addClassNames("ideas-view");

        // UserInfo is used by Collaboration Engine and is used to share details
        // of users to each other to able collaboration. Replace this with
        // information about the actual user that is logged, providing a user
        // identifier, and the user's real name. You can also provide the users
        // avatar by passing an url to the image as a third parameter, or by
        // configuring an `ImageProvider` to `avatarGroup`.
       // UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        // avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        // avatarGroup.getStyle().set("visibility", "hidden");

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("userName").setAutoWidth(true);
        grid.addColumn("movieTitle").setAutoWidth(true);
        grid.addColumn("genre").setAutoWidth(true);
        grid.addColumn("dateAdded").setAutoWidth(true);
        grid.setItems(query -> movieIdeaService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(MOVIEIDEA_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(IdeasView.class);
            }
        });

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.movieIdea == null) {
                    this.movieIdea = new MovieIdea();
                }
                binder.writeBean(this.movieIdea);

                if (this.movieIdea.getDescription() == null) {
                        this.movieIdea.setDescription(new MovieDescription());
                    }
                this.movieIdea.getDescription().setText(description.getValue());


                movieIdeaService.save(this.movieIdea);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(IdeasView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> movieIdeaId = event.getRouteParameters().get(MOVIEIDEA_ID).map(Long::parseLong);
        if (movieIdeaId.isPresent()) {
            Optional<MovieIdea> movieIdeaFromBackend = movieIdeaService.get(movieIdeaId.get());
            if (movieIdeaFromBackend.isPresent()) {
                populateForm(movieIdeaFromBackend.get());
            } else {
                Notification.show(String.format("The requested movieIdea was not found, ID = %d", movieIdeaId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(IdeasView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        userName = new TextField("User Name");
        movieTitle = new TextField("Movie Title");

        genre = new ComboBox<>("Genre");
        genre.setItems(Genres.GENRES);
        genre.setClearButtonVisible(true);
        genre.setPlaceholder("Select genre");
        genre.setAllowCustomValue(false);

        dateAdded = new DatePicker("Date Added");

        description = new TextArea("Plot");


        binder.forField(userName)
            .asRequired("Username is required")
            .bind(MovieIdea::getUserName, MovieIdea::setUserName);

        binder.forField(movieTitle)
            .asRequired("Movie title is required")
            .bind(MovieIdea::getMovieTitle, MovieIdea::setMovieTitle);

        binder.forField(genre)
            .asRequired("Genre is required")
            .bind(MovieIdea::getGenre, MovieIdea::setGenre);

        binder.forField(dateAdded)
            .asRequired("Date is required")
            .bind(MovieIdea::getDateAdded, MovieIdea::setDateAdded);



        binder.forField(description)
        .withConverter(
            text -> {
                MovieDescription desc = new MovieDescription();
                desc.setText(text);
                return desc;
            },
            desc -> desc != null ? desc.getText() : ""
        )
        .bind(MovieIdea::getDescription, MovieIdea::setDescription);
        
        


        // if (movieIdea != null) {
        //     binder.readBean(movieIdea);
        //     description.setValue(getDescriptionText(movieIdea));
        // } else {
        //     binder.readBean(new MovieIdea()); // tai jätä lukematta
        //     description.clear();
        // }

        if (movieIdea != null) {
            binder.readBean(movieIdea);
        } else {
            binder.readBean(new MovieIdea());
        }
        
            

        formLayout.add(userName, movieTitle, genre, dateAdded, description);

        editorDiv.add(formLayout);

        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    // private void createGridLayout(SplitLayout splitLayout) {
    //     Div wrapper = new Div();
    //     wrapper.setClassName("grid-wrapper");
    //     splitLayout.addToPrimary(wrapper);
    //     wrapper.add(grid);
    // }

    private String getDescriptionText(MovieIdea idea) {
        if (idea != null && idea.getDescription() != null) {
            return idea.getDescription().getText();
        }
        return "";
    }
    

    private void createGridLayout(SplitLayout splitLayout) {
        VerticalLayout verticalLayout = new VerticalLayout();
        filters = new Filters(this::refreshGrid, this.movieIdeaService);
        Div wrapper = new Div();
        wrapper.setHeightFull();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(verticalLayout);
        verticalLayout.add(filters, wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        Specification<MovieIdea> spec = filters;
        List<MovieIdea> filtered = movieIdeaService.findAll(spec);
        grid.setItems(filtered);
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(MovieIdea value) {
        this.movieIdea = value;
        binder.readBean(this.movieIdea);

    }

    // private void populateForm(MovieIdea value) {
    //     this.movieIdea = value;
    //     String topic = null;
    //     if (this.movieIdea != null && this.movieIdea.getId() != null) {
    //         topic = "movieIdea/" + this.movieIdea.getId();
    //         avatarGroup.getStyle().set("visibility", "visible");
    //     } else {
    //         avatarGroup.getStyle().set("visibility", "hidden");
    //     }
    //     binder.setTopic(topic, () -> this.movieIdea);
    //     avatarGroup.setTopic(topic);

    // }
}
