package ru.protei.portal.ui.common.client.widget.casecomment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.TimeElapsedTypeLang;
import ru.protei.portal.ui.common.client.view.selector.ElapsedTimeTypeSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;

import java.util.Arrays;

public class EditTimeElapsedTypeWidget extends Composite implements HasValueChangeHandlers<En_TimeElapsedType> {

    @Inject
    public void onInit(TimeElapsedTypeLang elapsedTimeTypeLang) {
        initWidget(ourUiBinder.createAndBindUi(this));

        typeSelector.setDisplayOptionCreator(type ->
                new DisplayOption((type == null || En_TimeElapsedType.NONE.equals(type)) ? lang.issueCommentElapsedTimeTypeLabel() : elapsedTimeTypeLang.getName(type)));
        typeSelector.fillOptions();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<En_TimeElapsedType> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void setVisible(boolean visible) {
        root.setVisible(visible);
        super.setVisible(visible);
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
    }

    public void setTimeElapsedType(En_TimeElapsedType type) {
        this.type = type;
        typeSelector.setValue(type);
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

    interface EditTimeElapsedTypeWidgetUiBinder extends UiBinder<HTMLPanel, EditTimeElapsedTypeWidget> {}
    private static EditTimeElapsedTypeWidgetUiBinder ourUiBinder = GWT.create(EditTimeElapsedTypeWidgetUiBinder.class);
}
