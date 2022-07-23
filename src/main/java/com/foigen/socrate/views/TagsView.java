package com.foigen.socrate.views;

import com.foigen.socrate.entities.RateTag;
import com.foigen.socrate.services.TagService;
import com.vaadin.flow.component.Component;
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
@Route(value = "/tags", layout = MainLayout.class)
public class TagsView extends VerticalLayout {
    Grid<RateTag> grid = new Grid<>(RateTag.class);
    TextField filterByName = new TextField();
    TextField newTag = new TextField();
    TagService service;

    public TagsView(TagService service) {
        this.service = service;
        addClassName("tags-view");
        setSizeFull();
        configureGrid();
        add(getContent());
        updateList();
    }

    private HorizontalLayout getToolbar() {
        var toolbar = new HorizontalLayout();
        newTag.setPlaceholder("New tag");
        filterByName.setPlaceholder("Filter by name...");
        filterByName.setClearButtonVisible(true);
        filterByName.setValueChangeMode(ValueChangeMode.LAZY);
        filterByName.addValueChangeListener(e -> updateList());
        var button = new Button("Добавить тэг");
        toolbar.add(filterByName);
        toolbar.add(newTag);
        toolbar.add(button);
        button.addClickListener(x -> {
            service.save(RateTag.builder().name(newTag.getValue()).build());
            updateList();
            newTag.clear();
        });
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassNames("tags-grid");
        grid.setSizeFull();
        grid.setColumns("name");
        grid.setMinHeight("20em");
        grid.setSizeUndefined();
    }

    private Component getContent() {
        var content = new HorizontalLayout();
        var info = new VerticalLayout();
        var crud=new VerticalLayout();
        var tagsHeader=new H2("Тэги");
        info.add(tagsHeader);
        info.setHorizontalComponentAlignment(Alignment.CENTER,tagsHeader);
        var text=new Text(
                "Тэги используются отметки создаваемых дел,шаблонов и условий,\n"
                +" с их помощью можно установить модификаторы на дела\n"
                +"той или иной категории, повысить или понизить получаемые и забираемые баллы\n"
                +"рейтинга. Так к примеру у модификатора 'Водитель-нарушитель' - будет тэг транспорт\n"
                +"и увеличение штрафов за нарушения c тэгом транспорт на определенный процент.\n"
                +"Простое использование процентных модификаторов используется в случае отсутствия\n"
                +"других влияющих факторов, в противном случае формула расчёта меняется в избежание\n"
                +"несопоставимого с разумным изменением стоимости начисления/списания"
        );
        crud.add(getToolbar(),grid);
        info.add(text);
        content.add(info,crud);
        return content;
    }

    private void updateList() {
        grid.setItems(service.findAllTags(filterByName.getValue()));
    }
}
