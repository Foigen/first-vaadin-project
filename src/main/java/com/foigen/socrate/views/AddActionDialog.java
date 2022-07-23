package com.foigen.socrate.views;

import com.foigen.socrate.entities.RateAction;
import com.foigen.socrate.entities.RateTag;
import com.foigen.socrate.entities.User;
import com.foigen.socrate.services.ActionService;
import com.foigen.socrate.services.TagService;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.function.SerializablePredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class AddActionDialog extends Dialog {
    ComboBox<RateAction> templates = new ComboBox<>("Template");
    TextField title = new TextField("Title");
    IntegerField rate = new IntegerField("Rate");
    MultiSelectListBox<RateTag> tags = new MultiSelectListBox<>();

    Button save = new Button("Save");
    Button close = new Button("Cancel");

    ActionService actionService;
    TagService tagService;

    Binder<RateAction> binder = new BeanValidationBinder<>(RateAction.class);

    User user;
    RateAction action = new RateAction();
    Supplier<Boolean> refresher;

    public AddActionDialog(ActionService actionService, TagService tagService, Supplier<Boolean> refresher) {
        this.actionService = actionService;
        this.tagService = tagService;
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

        templates.setItems(actionService.findAllTemplates());
        templates.setItemLabelGenerator(RateAction::getTitle);
        templates.addValueChangeListener(e -> useTemplate(e.getValue()));

        tags.setItems(tagService.findAllTags());
        tags.setItemLabelGenerator(RateTag::getName);
        tags.setMaxHeight("30rem");

        setMaxHeight("50rem");
        setWidth("40rem");

        VerticalLayout dialogLayout = createDialogLayout();

        var buttons=new HorizontalLayout(save, close);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        dialogLayout.add(buttons);
        add(new H3("Add action"), dialogLayout);
        configureButtons();

    }

    private VerticalLayout createDialogLayout() {
        VerticalLayout dialogLayout = new VerticalLayout(
                templates, title, tags,rate
        );
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        //dialogLayout.getStyle().set("width", "16em").set("max-width", "100%");
        return dialogLayout;
    }

    private void configureButtons() {
        save.addClickListener(e -> sendAction());
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        close.addClickListener(e -> close());
        List.of(save,close).forEach(HasSize::setWidthFull);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void open(User user) {
        super.open();
        setUser(user);
        tags.deselectAll();
    }

    private void sendAction() {
        if (!binder.writeBeanIfValid(action)) return;
        action.setUser(user);
        action.setTags(new ArrayList<>(tags.getSelectedItems()));
        binder.getFields().forEach(System.out::println);
        actionService.executeRateAct(action);
        refresher.get();
        close();
    }

    private void useTemplate(RateAction template) {
        binder.readBean(template);
        tags.deselectAll();
        tags.select(template.getTags());
    }

}
