package com.foigen.socrate.views;

import com.foigen.socrate.entities.Benefit;
import com.foigen.socrate.entities.RateAction;
import com.foigen.socrate.entities.RateCondition;
import com.foigen.socrate.entities.User;
import com.foigen.socrate.services.ActionService;
import com.foigen.socrate.services.BenefitService;
import com.foigen.socrate.services.ConditionService;
import com.foigen.socrate.services.UserService;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class UserDialog extends Dialog {
    H3 fullname = new H3("");
    H3 email = new H3("");
    H3 passport = new H3("");
    H3 rate = new H3("");

    Grid<RateCondition> conditionGrid = new Grid<>(RateCondition.class);
    Grid<Benefit> benefitGrid = new Grid<>(Benefit.class);
    Grid<RateAction> actionGrid = new Grid<>(RateAction.class);

    UserService userService;
    ActionService actionService;
    ConditionService conditionService;
    BenefitService benefitService;

    User user;

    public UserDialog(UserService userService,
                      ActionService actionService,
                      ConditionService conditionService,
                      BenefitService benefitService) {
        this.userService = userService;
        this.actionService = actionService;
        this.conditionService = conditionService;
        this.benefitService = benefitService;
        configureGrids();

        setMinWidth("50rem");
        setMinHeight("50rem");
        setMaxHeight("80rem");
//        setSizeFull();
        var layout=new VerticalLayout();
        layout.setSizeFull();
        add(fullname,email,passport,rate,
                conditionGrid,benefitGrid,actionGrid);
//        add(layout);
    }

    private void configureGrids() {
//        conditionGrid.setSizeFull();
        conditionGrid.setSizeUndefined();
        conditionGrid.setColumns("title");
        conditionGrid.addColumn(RateCondition::getMods).setHeader("mods")
                .setComparator((c1, c2) -> c1.getMods().size() > c2.getMods().size() ? 1 : 0);
        conditionGrid.getColumns().forEach(column -> column.setAutoWidth(true));

//        benefitGrid.setSizeFull();
        benefitGrid.setSizeUndefined();
        benefitGrid.setColumns("name");
        benefitGrid.addColumn(benefit -> benefit.getHigher() ? "higher" : "lesser")
                .setHeader("higher/lesser")
                .setComparator(x -> x.getHigher() ? 1 : 0);
        benefitGrid.addColumns("thenRate", "description");
        benefitGrid.getColumns().forEach(column -> column.setAutoWidth(true));

//        actionGrid.setSizeFull();
        actionGrid.setSizeUndefined();
        actionGrid.setColumns("title","date", "rate");
        actionGrid.addColumn(RateAction::getTags).setHeader("Tags")
                .setComparator(
                        (x, y) -> x.getTags().size() > y.getTags().size() ? 1 : 0
                );
        actionGrid.getColumns().forEach(x -> x.setAutoWidth(true));
    }

    public void setUser(User user) {
        this.user = user;
        fullname.setText("Full name: "+user.getLastname() + " " + user.getFirstname() + " " + user.getPatronymic());
        email.setText("Email: "+user.getEmail());
        passport.setText("Passport: "+user.getPassportSeries() + " " + user.getPassportID());
        rate.setText("Rate: "+user.getRate());
        updateLists();
    }

    public void open(User user){
        setUser(user);
        open();
    }

    private void updateConditionList() {
        conditionGrid.setItems(user.getConditions());
    }

    private void updateBenefitList() {
        benefitGrid.setItems(benefitService.findBenefitsByUserValidation(user));
    }

    private void updateActionList() {
        actionGrid.setItems(actionService.findAllActionsByUser(user));
        System.err.println("abap");
        user.getActions().forEach(System.err::println);
    }

    private void updateLists() {
        updateConditionList();
        updateBenefitList();
        updateActionList();
    }
}
