package com.foigen.socrate.views;

import com.foigen.socrate.entities.RateCondition;
import com.foigen.socrate.entities.User;
import com.foigen.socrate.enums.Role;
import com.foigen.socrate.services.ConditionService;
import com.foigen.socrate.services.SecurityService;
import com.foigen.socrate.services.TagService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@RolesAllowed("ADMIN")
@Route(value = "/conditions/", layout = MainLayout.class)
public class ConditionsView extends VerticalLayout {
    Grid<RateCondition> grid = new Grid<>(RateCondition.class);
    TextField filterText = new TextField();

    ConditionService conditionService;
    SecurityService securityService;
    TagService tagService;
    ConditionForm form;

    public ConditionsView(ConditionService conditionService,
                          SecurityService securityService,
                          TagService tagService) {
        this.conditionService = conditionService;
        this.securityService = securityService;
        this.tagService = tagService;
        addClassName("conditions-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getContent());
        updateList();
        form.close();
    }

    public void configureGrid() {
        grid.addClassName("conditions-grid");
        grid.setSizeFull();
        grid.setColumns("title");
        grid.addColumn(RateCondition::getMods).setHeader("mods")
                .setComparator((c1, c2) -> c1.getMods().size() > c2.getMods().size() ? 1 : 0);
        grid.getColumns().forEach(x -> x.setAutoWidth(true));
        if (isAdmin())
            grid.asSingleSelect().addValueChangeListener(event ->
                    editCondition(event.getValue()));
    }

    private HorizontalLayout getContent() {
        var description = new VerticalLayout();
        var t = new H2("Условия");
        description.add(
                t,
                new Text("Условия позволяют добавлять множители на начисляемые и списываемые\n" +
                        "баллы рейтинга за операции с соответствующими тэгами \n" +
                        "так например на одном условии может быть сразу несколько модификаторов на один и тот же тэг\n" +
                        "например -1.1 и 1.1 чтобы увеличивать как начисляемые так и списываемые баллы\n" +
                        "либо для того чтобы в большей степени влиять на операцию (расчёт итогового рейтинга\n" +
                        "идет не перемножением всех модификаторов, это бы привело к совершенно неадекватным\n" +
                        "увеличениям операции по модулю в случае наличия нескольких крупных модификаторов)")
        );
        description.setHorizontalComponentAlignment(Alignment.CENTER, t);
        var gridBox = new VerticalLayout(getToolbar(), grid);
        HorizontalLayout content = new HorizontalLayout(description, gridBox, form);
        content.addClassName("content");
        content.setFlexGrow(0.5, description);
        content.setFlexGrow(2, gridBox);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        return content;
    }

    private HorizontalLayout getToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        toolbar.add(filterText);
        if (isAdmin()) {
            Button addConditionButton = new Button("add condition");
            addConditionButton.addClickListener(click -> addCondition());
            toolbar.add(addConditionButton);
        }
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addCondition() {
        grid.asSingleSelect().clear();
        editCondition(new RateCondition());
    }

    private void editCondition(RateCondition condition) {
        if (condition == null) {
            form.close();
        } else {
            form.setCondition(condition);
            form.bindMods();
            form.setVisible(true);
        }
    }

    private void configureForm() {
        form = new ConditionForm(tagService, conditionService, () -> {
            updateList();
            return true;
        });
        form.setWidth("30em");
    }

    private void updateList() {
        grid.setItems(conditionService.findAllConditions(filterText.getValue()));
    }

    private boolean isAdmin() {
        return ((User) securityService.getAuthenticatedUser()).getRole() == Role.ROLE_ADMIN;
    }
}
