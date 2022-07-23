package com.foigen.socrate.views;

import com.foigen.socrate.entities.Benefit;
import com.foigen.socrate.enums.Role;
import com.foigen.socrate.entities.User;
import com.foigen.socrate.services.BenefitService;
import com.foigen.socrate.services.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

@PermitAll
@Route(value = "/benefits/", layout = MainLayout.class)
public class BenefitsView extends VerticalLayout {
    Grid<Benefit> grid = new Grid<>(Benefit.class);
    TextField filterText = new TextField();
    BenefitForm form;
    BenefitService benefitService;
    SecurityService securityService;

    public BenefitsView(BenefitService benefitService,
                        SecurityService securityService) {
        this.benefitService = benefitService;
        this.securityService = securityService;
        addClassName("benefits-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new BenefitForm();
        form.setWidth("25em");

        form.addListener(BenefitForm.SaveEvent.class, this::saveBenefit);
        form.addListener(BenefitForm.DeleteEvent.class, this::deleteContact);
        form.addListener(BenefitForm.CloseEvent.class, e -> closeEditor());
    }

    private void configureGrid() {
        grid.addClassNames("benefits-grid");
        grid.setSizeFull();
        grid.setColumns("name");
        grid.addColumn(benefit -> benefit.getHigher() ? "higher" : "lesser").setHeader("higher/lesser").setComparator(x -> x.getHigher() ? 1 : 0);
        grid.addColumns("thenRate", "description");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        if (isAdmin())
            grid.asSingleSelect().addValueChangeListener(event ->
                    editBenefit(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        HorizontalLayout toolbar=new HorizontalLayout();
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        toolbar.add(filterText);
        if (isAdmin()) {
            var addBenefitButton = new Button("add benefit");
            addBenefitButton.addClickListener(click -> addBenefit());
            toolbar.add(addBenefitButton);
        }
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void saveBenefit(BenefitForm.SaveEvent event) {
        benefitService.saveBenefit(event.getBenefit());
        updateList();
        closeEditor();
    }

    private void deleteContact(BenefitForm.DeleteEvent event) {
        benefitService.deleteBenefit(event.getBenefit());
        updateList();
        closeEditor();
    }

    public void editBenefit(Benefit benefit) {
        if (benefit == null) {
            closeEditor();
        } else {
            form.setBenefit(benefit);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setBenefit(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addBenefit() {
        grid.asSingleSelect().clear();
        editBenefit(new Benefit());
    }

    private void updateList() {
        grid.setItems(benefitService.findAllBenefits(filterText.getValue()));
    }

    private boolean isAdmin() {
        return ((User) securityService.getAuthenticatedUser()).getRole()== Role.ROLE_ADMIN;
    }
}