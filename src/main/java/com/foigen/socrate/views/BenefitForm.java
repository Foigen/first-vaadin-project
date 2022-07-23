package com.foigen.socrate.views;

import com.foigen.socrate.entities.Benefit;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.shared.Registration;

import java.util.Objects;

public class BenefitForm extends FormLayout {
    TextField name = new TextField("Name");
    ComboBox<Boolean> higher = new ComboBox<>("higher/lesser");
    IntegerField thenRate = new IntegerField("Then Rate");
    TextArea description = new TextArea("Description");

    Binder<Benefit> binder = new BeanValidationBinder<>(Benefit.class);
    private Benefit benefit;

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    public BenefitForm() {
        addClassName("benefit-form");
        binder.forField(name)
                .withValidator(str -> str.length() >= 1, "non empty")
                .bind(Benefit::getName, Benefit::setName);
        binder.forField(higher)
                .withValidator(Objects::nonNull, "non empty")
                .bind(Benefit::getHigher, Benefit::setHigher);
        binder.forField(thenRate)
                .withValidator(Validator.from((SerializablePredicate<Integer>) integer -> Math.abs(integer) <= 1000,
                        "should be in range -1000 - 1000"))
                .bind(Benefit::getThenRate, Benefit::setThenRate);
        binder.forField(description).bind(Benefit::getDescription,Benefit::setDescription);

        name.setRequiredIndicatorVisible(true);
        higher.setRequiredIndicatorVisible(true);
        thenRate.setRequiredIndicatorVisible(true);

        name.setClearButtonVisible(true);
        description.setClearButtonVisible(true);
        higher.setRequired(true);
        higher.setItems(true, false);
        higher.setItemLabelGenerator(x -> x ? "higher" : "lesser");


        add(name, higher, thenRate, description, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);
        //
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, benefit)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        //
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(benefit);
            fireEvent(new SaveEvent(this, benefit));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public void setBenefit(Benefit benefit) {
        this.benefit = benefit;
        binder.readBean(benefit);
    }

    //
    public static abstract class BenefitFormEvent extends ComponentEvent<BenefitForm> {
        private final Benefit benefit;

        protected BenefitFormEvent(BenefitForm source, Benefit benefit) {
            super(source, false);
            this.benefit = benefit;
        }

        public Benefit getBenefit() {
            return benefit;
        }
    }

    public static class SaveEvent extends BenefitFormEvent {
        SaveEvent(BenefitForm source, Benefit benefit) {
            super(source, benefit);
        }
    }

    public static class DeleteEvent extends BenefitFormEvent {
        DeleteEvent(BenefitForm source, Benefit benefit) {
            super(source, benefit);
        }

    }

    public static class CloseEvent extends BenefitFormEvent {
        CloseEvent(BenefitForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}