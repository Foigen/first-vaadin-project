package com.foigen.socrate.views;

import com.foigen.socrate.entities.User;
import com.foigen.socrate.services.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@RolesAllowed("ADMIN")
@Route(value = "/users", layout = MainLayout.class)
public class UsersView extends VerticalLayout {
    Grid<User> grid = new Grid<>(User.class);
    TextField filterText = new TextField();

    UserService userService;
    ActionService actionService;
    ConditionService conditionService;
    TagService tagService;
    BenefitService benefitService;

    UsersMenu menu;
    AddActionDialog addActionDialog;
    UserDialog userDialog;


    public UsersView(UserService userService, ActionService actionService, ConditionService conditionService,
                     TagService tagService, BenefitService benefitService) {
        this.userService = userService;
        this.actionService = actionService;
        this.conditionService = conditionService;
        this.tagService = tagService;
        this.benefitService = benefitService;
        this.addActionDialog = new AddActionDialog(actionService, tagService, () -> {
            updateList();
            return true;
        });
        this.userDialog = new UserDialog(userService, actionService, conditionService, benefitService);
        addClassName("users-view");
        setSizeFull();
        configureGrid();
        configureMenu();
        add(getToolbar(), getContent());
        updateList();
        menu.close();
    }

    private void configureMenu() {
        menu = new UsersMenu(userService, conditionService, () -> {
            updateList();
            return true;
        }, (u) -> addActionDialog.open(u),
                (u) -> userDialog.open(u));
        menu.setWidth("20em");
    }

    private void configureGrid() {
        grid.addClassNames("users-grid");
        grid.setSizeFull();
        grid.setColumns("username", "firstname", "lastname", "patronymic", "email", "rate");
        grid.addColumn(User::getConditions).setHeader("conditions")
                .setComparator((u1, u2) ->
                        u1.getConditions().size() > u2.getConditions().size() ? 1 : 0);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(
                event -> menu.setUser(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by username...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private HorizontalLayout getContent() {
        var gridBox = new VerticalLayout();
        gridBox.add(getToolbar(), grid);
        var content = new HorizontalLayout(gridBox, menu);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }


    private void updateList() {
        grid.setItems(userService.findAllUsers(filterText.getValue()));
    }
}
