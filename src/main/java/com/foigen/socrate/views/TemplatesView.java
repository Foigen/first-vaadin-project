package com.foigen.socrate.views;

import com.foigen.socrate.entities.RateAction;
import com.foigen.socrate.entities.User;
import com.foigen.socrate.enums.Role;
import com.foigen.socrate.services.ActionService;
import com.foigen.socrate.services.SecurityService;
import com.foigen.socrate.services.TagService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

@PermitAll
@Route(value = "/templates/", layout = MainLayout.class)
public class TemplatesView extends VerticalLayout {
    Grid<RateAction> grid = new Grid<>(RateAction.class);
    TextField filterText = new TextField();
    TemplateForm form;

    TagService tagService;
    ActionService actionService;
    SecurityService securityService;

    public TemplatesView(TagService tagService,
                         ActionService actionService,
                         SecurityService securityService) {
        this.tagService = tagService;
        this.actionService = actionService;
        this.securityService = securityService;
        addClassName("templates-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getContent());
        updateList();
        form.close();
    }

    private void configureGrid() {
        grid.addClassName("templates-grid");
        grid.setSizeFull();
        grid.setColumns("title", "rate");
        grid.addColumn(RateAction::getTags).setHeader("Tags")
                .setComparator(
                        (x, y) -> x.getTags().size() > y.getTags().size() ? 1 : 0
                );
        grid.getColumns().forEach(x -> x.setAutoWidth(true));
        if (isAdmin())
            grid.asSingleSelect().addValueChangeListener(event -> editTemplate(event.getValue()));
    }

    private void configureForm() {
        form = new TemplateForm(tagService, actionService, () -> {
            updateList();
            return true;
        });
        form.setWidth("20em");
    }

    private HorizontalLayout getToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        filterText.setPlaceholder("Filter by title...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        toolbar.add(filterText);
        if (isAdmin()) {
            var addTemplateButton = new Button("add template");
            addTemplateButton.addClickListener(click -> addTemplate());// TODO: 02.06.2022 добавить создание шаблона
            toolbar.add(addTemplateButton);
        }
        toolbar.addClassName("toolbar");
        return toolbar;
    }


    private HorizontalLayout getContent() {
        var gridBox = new VerticalLayout();
        gridBox.add(
                getToolbar(),
                grid
        );
        var content = new HorizontalLayout(gridBox, form);
//        content.setJustifyContentMode(JustifyContentMode.END);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void editTemplate(RateAction template) {
        if (template == null) {
            form.close();
        } else {
            form.setTemplate(template);
            form.setVisible(true);
        }
    }

    private void addTemplate() {
        grid.asSingleSelect().clear();
        editTemplate(new RateAction());
    }

    private void updateList() {
        grid.setItems(actionService.findAllTemplates(filterText.getValue()));
    }

    private boolean isAdmin() {
        return ((User) securityService.getAuthenticatedUser()).getRole() == Role.ROLE_ADMIN;
    }
}
