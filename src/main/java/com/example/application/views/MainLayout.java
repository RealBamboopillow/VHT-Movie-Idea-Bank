package com.example.application.views;

import com.example.application.data.User;
import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private H1 viewTitle;
    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        //addHeader();
    }
    // private void addHeader(){
    //     HorizontalLayout horizontalLayout2 = new HorizontalLayout();
    //     H1 h1 = new H1();
    //     h1.setText("Hello everybody and welcome to Movie Idea Bank!");
    //     addToNavbar(horizontalLayout2);
    // }



    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        // Oikea puoli: käyttäjätiedot tai kirjautumislinkki
        Component userInfoComponent;
        Optional<User> maybeUser = authenticatedUser.get();

        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(user.getAvatarName());
            div.add(new Icon("lumo", "dropdown"));
            div.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.Gap.SMALL
            );
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> authenticatedUser.logout());

            userInfoComponent = userMenu;
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            userInfoComponent = loginLink;
        }

        // Header-layout
        HorizontalLayout headerLayout = new HorizontalLayout(toggle, viewTitle, userInfoComponent);
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.expand(viewTitle); // Puskee käyttäjätiedot oikealle

        addToNavbar(true, headerLayout);
    }

    private void addDrawerContent() {
        Span appName = new Span("Movie Idea Bank");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            if (entry.icon() != null) {
                nav.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
            } else {
                nav.addItem(new SideNavItem(entry.title(), entry.path()));
            }
        });

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        H6 h6 = new H6();
        h6.setText("All rights are reserved!");
        layout.add(h6);
        // Optional<User> maybeUser = authenticatedUser.get();
        // if (maybeUser.isPresent()) {
        //     User user = maybeUser.get();
    
        //     MenuBar userMenu = new MenuBar();
        //     userMenu.setThemeName("tertiary-inline contrast");
    
        //     MenuItem userName = userMenu.addItem("");
        //     Div div = new Div();
        //     div.add(user.getAvatarName());
        //     div.add(new Icon("lumo", "dropdown"));
        //     div.addClassNames(
        //         LumoUtility.Display.FLEX,
        //         LumoUtility.AlignItems.CENTER,
        //         LumoUtility.Gap.SMALL
        //     );
        //     userName.add(div);
        //     userName.getSubMenu().addItem("Sign out", e -> {
        //         authenticatedUser.logout();
        //     });
    
        //     layout.add(userMenu);
        // } else {
        //     Anchor loginLink = new Anchor("login", "Sign in");
        //     layout.add(loginLink);
        // }
    
        return layout;
    }
    
    

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}
