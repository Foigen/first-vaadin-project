package com.foigen.socrate.views;

import com.foigen.socrate.entities.RateAction;
import com.foigen.socrate.entities.RateTag;
import com.foigen.socrate.services.ActionService;
import com.foigen.socrate.services.TagService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.function.SerializablePredicate;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

public class TemplateForm extends FormLayout {
    TextField title = new TextField("Title");
    MultiSelectListBox<RateTag> tags = new MultiSelectListBox<>();
    IntegerField rate = new IntegerField("Rate");

    TagService tagService;
    ActionService actionService;
    Supplier<Boolean> refresher;

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private RateAction template = new RateAction();

    Binder<RateAction> binder = new Binder<>(RateAction.class);

    public TemplateForm(TagService tagService, ActionService actionService, Supplier<Boolean> refresher) {
        this.tagService = tagService;
        this.actionService = actionService;
        this.refresher = refresher;

        binder.forField(title)
                .withValidator(str -> str.length() >= 1, "non empty")
                .bind(RateAction::getTitle, RateAction::setTitle);
        binder.forField(rate).withValidator(
                        Validator.from((SerializablePredicate<Integer>) integer -> Math.abs(integer) <= 1000,
                                "transaction amount should not exceed 1000"))
                .bind(RateAction::getRate, RateAction::setRate);

        title.setRequiredIndicatorVisible(true);
        rate.setRequiredIndicatorVisible(true);

        System.out.println(tagService.findAllTags());
        tags.setItems(tagService.findAllTags());
        tags.setItemLabelGenerator(RateTag::getName);
        tags.setHeight("8em");
        add(title, tags, rate);
        add(createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(x -> addTemplate());
        close.addClickListener(x -> close());
        delete.addClickListener(x -> deleteTemplate());

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        var buttons = new HorizontalLayout(save, delete, close);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.EVENLY);
        return buttons;
    }

    public void setTemplate(RateAction template) {
        this.template = template;
        binder.readBean(template);
        Optional.ofNullable(template).orElse(new RateAction()).getTags().forEach(x -> System.out.println(x.getName()));
        if (template != null)
            tags.select(template.getTags());
        else tags.deselectAll();
    }

    public void addTemplate() {
        if (!binder.writeBeanIfValid(template)) return;
        template.setTags(new ArrayList<>(tags.getSelectedItems()));
        System.out.println(template);
        actionService.saveTemplate(template);
        close();
        refresher.get();
    }

    public void deleteTemplate() {
        actionService.delete(template);
        close();
        refresher.get();
    }

    public void close() {
        setVisible(false);
        setTemplate(null);
    }
}
