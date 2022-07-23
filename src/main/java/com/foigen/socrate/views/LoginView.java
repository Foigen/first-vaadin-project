package com.foigen.socrate.views;

import com.foigen.socrate.repositories.UserRepository;
import com.foigen.socrate.services.UserService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("/login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    UserService userService;
    @Autowired
    private UserRepository userRepository;

    private final LoginForm login = new LoginForm();

    public LoginView(UserService service) {
        userService = service;
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);
        add(new H1("Social rate security"), login,
                new RegisterDialog(userService));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}