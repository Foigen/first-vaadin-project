package com.foigen.socrate.views;

import com.foigen.socrate.entities.RateCondition;
import com.foigen.socrate.entities.RateMod;
import com.foigen.socrate.entities.RateTag;
import com.foigen.socrate.services.ConditionService;
import com.foigen.socrate.services.TagService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import javax.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@PermitAll
public class ConditionForm extends FormLayout {
    TextField title = new TextField("Title");
    VerticalLayout columns = new VerticalLayout();

    List<ComboBox<RateTag>> tags = new ArrayList<>();
    List<NumberField> mods = new ArrayList<>();
    List<Binder<RateMod>> modBinders = new ArrayList<>();

    Binder<RateCondition> conditionBinder = new BeanValidationBinder<>(RateCondition.class);
    RateCondition condition = new RateCondition();

    int colCounter = 0;
    int visibleLines = 0;

    TagService tagService;
    ConditionService conditionService;

    Button addLineButton = new Button("New mod");
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Supplier<Boolean> refresher;

    public ConditionForm(TagService tagService, ConditionService conditionService, Supplier<Boolean> refresher) {
        this.tagService = tagService;
        this.conditionService = conditionService;
        this.refresher = refresher;
        conditionBinder.forField(title)
                .withValidator(str -> str.length() >= 1, "non empty")
                .bind(RateCondition::getTitle, RateCondition::setTitle);

        title.setRequiredIndicatorVisible(true);

        addClassName("condition-form");
        configureAddButton();
        var firstline = new HorizontalLayout(title, addLineButton);
        firstline.setAlignItems(FlexComponent.Alignment.BASELINE);
        columns.add(firstline);
        add(columns);
        add(createButtonsLayout());
    }

    private void configureAddButton() {
        addLineButton.addClickListener(event -> drawLine());
    }


    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(x -> addCondition());
        close.addClickListener(x -> close());
        delete.addClickListener(x -> deleteCondition());

        var buttons = new HorizontalLayout(save, delete, close);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.EVENLY);
        return buttons;
    }

    public void addLine() {
        if (colCounter > 8) return;
        colCounter++;
        visibleLines++;
        var tag = new ComboBox<RateTag>();
        tag.setItems(tagService.findAllTags());
        tag.setItemLabelGenerator(RateTag::getName);
        var mod = new NumberField();
        var binder = new BeanValidationBinder<>(RateMod.class);
        binder.forField(tag).withValidator(Objects::nonNull, "Tag must be not null").bind(RateMod::getTag, RateMod::setTag);
        binder.forField(mod).withValidator(x -> Objects.nonNull(x) && x >= -2.0 && x <= 2.0, "mod absolute value must be lesser 2").bind(RateMod::getMod, RateMod::setMod);
        modBinders.add(binder);
        columns.add(new HorizontalLayout(tag, mod));
        tags.add(tag);
        mods.add(mod);
    }
    private void drawLines(int linesCount){
        var locklCount=Math.min(linesCount,8);
        for(var i=0;i<locklCount;i++) drawLine();
    }

    private void drawLine() {
        if (visibleLines != colCounter) {
            tags.get(visibleLines).getParent().orElseThrow().setVisible(true);
            visibleLines++;
            return;
        }
        addLine();
    }

    private void eraseLines() {
        tags.forEach(x->x.getParent().orElseThrow().setVisible(false));
        visibleLines = 0;
    }
    private void clearBinders(){
        modBinders.forEach(x->x.readBean(null));
    }

    public void setCondition(RateCondition condition) {
        this.condition = condition;
        conditionBinder.readBean(condition);
    }

    public void close() {
        setVisible(false);
        setCondition(null);
        refresher.get();
        eraseLines();
        clearBinders();
    }

    public void bindMods(){
        var conMods=condition.getMods();
        var colCount=condition.getMods().size();
        drawLines(colCount);
        for (int i = 0; i < colCount; i++) {
            modBinders.get(i).readBean(conMods.get(i));
        }
    }

    public void deleteCondition() {
        conditionService.delete(condition);
        close();
    }

    public void addCondition() {
        List<RateMod> retRateMods = new ArrayList<>();
        modBinders.forEach(x -> {
            try {
                var temp=new RateMod();
                x.writeBean(temp);
                System.out.println("ABAP "+temp);
                retRateMods.add(temp);
            } catch (ValidationException e) {
                System.err.println("VALIDATION UNSUCCESSFUL");
            }
        });
        if(conditionBinder.writeBeanIfValid(condition)) {
            condition.getMods().forEach(System.out::println);
            condition.setMods(List.copyOf(retRateMods));
            System.out.println(condition);
            conditionService.save(condition);
            eraseLines();
            close();
        }
    }

}
