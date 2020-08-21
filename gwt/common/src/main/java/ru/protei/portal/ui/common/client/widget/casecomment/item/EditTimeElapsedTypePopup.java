package ru.protei.portal.ui.common.client.widget.casecomment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.TimeElapsedTypeLang;
import ru.protei.portal.ui.common.client.view.selector.ElapsedTimeTypeSelector;
import ru.protei.portal.ui.common.client.widget.composite.PopupLikeComposite;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;

public class EditTimeElapsedTypePopup extends PopupLikeComposite implements HasValueChangeHandlers<En_TimeElapsedType> {
    @Inject
    public void onInit(TimeElapsedTypeLang elapsedTimeTypeLang) {
        initWidget(ourUiBinder.createAndBindUi(this));

        typeSelector.setDisplayOptionCreator(type ->
                new DisplayOption((type == null || En_TimeElapsedType.NONE.equals(type)) ? lang.issueCommentElapsedTimeTypeLabel() : elapsedTimeTypeLang.getName(type)));
        typeSelector.fillOptions();

        addClickableContainer(typeSelector.getPopupContainer());
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<En_TimeElapsedType> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("typeSelector")
    public void onTypeChanged(ValueChangeEvent<En_TimeElapsedType> event) {
        confirmBtn.setEnabled(event.getValue() != En_TimeElapsedType.NONE);
    }

    @UiHandler("confirmBtn")
    public void onConfirmClicked(ClickEvent event) {
        if (type != typeSelector.getValue()) {
            ValueChangeEvent.fire(this, typeSelector.getValue());
        }

        setVisible(false);
    }

    public void setTimeElapsedType(En_TimeElapsedType type) {
        this.type = type;
        typeSelector.setValue(type);
    }

    @Override
    protected void onLoad() {
        typeSelector.setValue(type == null ? En_TimeElapsedType.NONE : type);
        confirmBtn.setEnabled(type != En_TimeElapsedType.NONE);
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    ElapsedTimeTypeSelector typeSelector;
    @UiField
    Button confirmBtn;
    @Inject
    @UiField
    Lang lang;

    private En_TimeElapsedType type;

    interface EditTimeElapsedTypePopupUiBinder extends UiBinder<HTMLPanel, EditTimeElapsedTypePopup> {}
    private static EditTimeElapsedTypePopupUiBinder ourUiBinder = GWT.create(EditTimeElapsedTypePopupUiBinder.class);
}
