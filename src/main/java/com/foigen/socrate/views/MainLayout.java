package com.foigen.socrate.views;

import com.foigen.socrate.enums.Role;
import com.foigen.socrate.entities.User;
import com.foigen.socrate.services.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;


public class MainLayout extends AppLayout {
    SecurityService securityService;
    UserService userService;
    ActionService actionService;
    ConditionService conditionService;
    BenefitService benefitService;

    UserDialog userDialog;

    public MainLayout(SecurityService securityService,
                      UserService userService,
                      ActionService actionService,
                      ConditionService conditionService,
                      BenefitService benefitService) {
        this.securityService = securityService;
        this.userService = userService;
        this.actionService = actionService;
        this.conditionService = conditionService;
        this.benefitService = benefitService;
        userDialog=new UserDialog(userService,actionService,
                conditionService,benefitService);
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H3 logo = new H3("Social rate system");
        Button logout = new Button("Log out", x -> securityService.logout());
        var userLink = new Button(securityService.getAuthenticatedUser().getUsername(),e->userDialog.open((User) (securityService.getAuthenticatedUser())));
        if(isAdmin()) userLink.setEnabled(false);
        var rightSidePanel = new HorizontalLayout(userLink, logout);
        rightSidePanel.setAlignItems(FlexComponent.Alignment.BASELINE);
        // TODO: 24.05.2022 ссылку перебить на личную страницу пользователя
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, rightSidePanel);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        header.expand(logo);
        header.setWidth("100%");

        addToNavbar(header);

    }

    private void createDrawer() {
        var benefitsLink = new RouterLink("Benefits", BenefitsView.class);
        var usersLink = new RouterLink("Users", UsersView.class);
        var tagsLink=new RouterLink("Tags",TagsView.class);
        var conditionsLink=new RouterLink("Conditions",ConditionsView.class);
        var templatesLink=new RouterLink("Templates",TemplatesView.class);
        benefitsLink.setHighlightCondition(HighlightConditions.sameLocation());

        var layout=new VerticalLayout();
        layout.add(benefitsLink,templatesLink);
        if (isAdmin())
            layout.add(usersLink,tagsLink,conditionsLink);
        addToDrawer(layout);
    }

    private boolean isAdmin(){
        return ((User) securityService.getAuthenticatedUser()).getRole()== Role.ROLE_ADMIN;
    }
}