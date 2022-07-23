package com.foigen.socrate.views;

import com.foigen.socrate.entities.RateCondition;
import com.foigen.socrate.entities.User;
import com.foigen.socrate.services.ConditionService;
import com.foigen.socrate.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UsersMenu extends VerticalLayout {
    MultiSelectListBox<RateCondition> conditions = new MultiSelectListBox<>();

    UserService userService;
    ConditionService conditionService;
    Supplier<Boolean> refresher;
    Consumer<User> actionDialogOpener;
    Consumer<User> userDialogOpener;

    Button addActionButton = new Button("add action");
    Button saveConditionsButton = new Button(("save conditions"));
    Button showUserInfoButton =new Button("show user info");
    Button closeButton = new Button("close");

    private User user;

    public UsersMenu(UserService userService,
                     ConditionService conditionService,
                     Supplier<Boolean> refresher,
                     Consumer<User> actionDialogOpener,
                     Consumer<User> userDialogOpener) {
        this.userService = userService;
        this.conditionService = conditionService;
        this.refresher = refresher;
        this.actionDialogOpener = actionDialogOpener;
        this.userDialogOpener = userDialogOpener;

        conditions.setItems(conditionService.findAllConditions(""));
        conditions.setItemLabelGenerator(RateCondition::getTitle);
        conditions.setMaxHeight("10em");
        var title = new H2("Conditions");
        setHorizontalComponentAlignment(Alignment.CENTER, title);
        configureButtons();

        add(title, conditions,
                addActionButton,
                saveConditionsButton,
                showUserInfoButton,
                closeButton
        );

    }

    private void configureButtons() {
        List.of(addActionButton, saveConditionsButton,showUserInfoButton, closeButton).forEach(Button::setWidthFull);
        addActionButton.addClickListener(e -> actionDialogOpener.accept(user));
        saveConditionsButton.addClickListener(e -> saveConditions());
        showUserInfoButton.addClickListener(e->userDialogOpener.accept(user));
        closeButton.addClickListener(e -> close());
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            setVisible(true);
            conditions.deselectAll();
            conditions.select(user.getConditions());
        } else {
            conditions.deselectAll();
            setVisible(false);
        }

    }

    public void close() {
        setUser(null);
    }

    public void saveConditions() {
        System.out.println(conditions.getSelectedItems());
        user.setConditions(new ArrayList<>(conditions.getSelectedItems()));
        userService.updateUser(user);
        refresher.get();
        close();
    }
}
