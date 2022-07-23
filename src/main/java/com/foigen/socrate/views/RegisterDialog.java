package com.foigen.socrate.views;

import com.foigen.socrate.entities.User;
import com.foigen.socrate.enums.Role;
import com.foigen.socrate.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;

import java.util.List;

public class RegisterDialog extends Div {
    final TextField username = new TextField("Username");
    final PasswordField password = new PasswordField("Password");
    final TextField firstname = new TextField("Firstname");
    final TextField lastname = new TextField("Lastname");
    final TextField patronymic = new TextField("Patronymic");
    final TextField passportSeries = new TextField("Passport series");
    final TextField passportId = new TextField("Passport ID");
    final TextField email = new TextField("Email");

    Dialog dialog = new Dialog();
    Button showDialog = new Button("Register");

    Binder<User> binder = new BeanValidationBinder<>(User.class);

    UserService userService;

    User user = new User();


    public RegisterDialog(UserService userService) {
        this.userService = userService;

//        binder.bindInstanceFields(this);
        configureBinder();
        password.setRequiredIndicatorVisible(true);
        List.of(username, firstname, lastname, patronymic, passportSeries, passportId, email)
                .forEach(x -> x.setRequiredIndicatorVisible(true));
        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(new H3("Registration"), dialogLayout);
        var createButton = createSaveButton();
        binder.addStatusChangeListener(e -> createButton.setEnabled(binder.isValid()));
        var cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.add(new HorizontalLayout(createButton, cancelButton));
        showDialog.addClickListener(e -> {
            dialog.open();
            showDialog.setEnabled(true);
        });
        add(dialog, showDialog);

    }

    private VerticalLayout createDialogLayout() {
        VerticalLayout dialogLayout = new VerticalLayout(
                username, password, firstname, lastname, patronymic,
                passportSeries, passportId, email);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "30rem").set("max-width", "100%");
        return dialogLayout;
    }

    private Button createSaveButton() {
        Button registerButton = new Button("register", e -> register());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return registerButton;
    }

    private boolean writeUser() {
        user = new User();
        var ret = binder.writeBeanIfValid(user);
        if (ret) {
            user.setRate(300);
            user.setRole(Role.ROLE_USER);
        }
        return ret;
    }

    private void configureBinder() {
        binder.forField(username)
                .withValidator(name -> userService.findUserByUsername(name).isEmpty() && name.length() > 3, "this username already taken or name lesser 3 symbols")
                .bind(User::getUsername, User::setUsername);
        binder.forField(password)
                .withValidator(str -> str.length() >= 1, "non empty")
                .bind(User::getPassword, User::setPassword);
        binder.forField(firstname)
                .withValidator(str -> str.length() >= 1, "non empty")
                .bind(User::getFirstname, User::setFirstname);
        binder.forField(lastname)
                .withValidator(str -> str.length() >= 1, "non empty")
                .bind(User::getLastname, User::setLastname);
        binder.forField(patronymic)
                .withValidator(str -> str.length() >= 1, "non empty")
                .bind(User::getPatronymic, User::setPatronymic);
        binder.forField(passportSeries)
                .withValidator(str -> str.matches("[0-9]{4}"), "only 4 numbers")
                .bind(User::getPassportSeries, User::setPassportSeries);
        binder.forField(passportId)
                .withValidator(str -> str.matches("[0-9]{6}"), "only 6 numbers")
                .bind(User::getPassportID, User::setPassportID);
        binder.forField(email).withValidator(new EmailValidator("incorrect email")).bind(User::getEmail, User::setEmail);
    }

    private void register() {
        if (writeUser()) {
            if (userService.createUser(user))
                dialog.close();
            else passportSeries.setValue("this series and id already taken");
        }
    }
}