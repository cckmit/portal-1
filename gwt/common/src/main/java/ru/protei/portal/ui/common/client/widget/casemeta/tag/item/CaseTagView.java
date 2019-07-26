package ru.protei.portal.ui.common.client.widget.casemeta.tag.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.ui.common.client.events.EditEvent;
import ru.protei.portal.ui.common.client.events.EditHandler;
import ru.protei.portal.ui.common.client.events.HasEditHandlers;
import ru.protei.portal.ui.common.client.util.ColorUtils;

public class CaseTagView extends Composite implements HasValue<CaseTag>, HasCloseHandlers<CaseTag>, HasEnabled {

    public CaseTagView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setValue(CaseTag value) {
        setValue(value, false);
    }

    @Override
    public void setValue(CaseTag value, boolean fireEvents) {
        caseTag = value;

        String backgroundColor = ColorUtils.makeSafeColor(caseTag.getColor());
        String textColor = ColorUtils.makeContrastColor(backgroundColor);

        text.setText(caseTag.getName());

        panel.getElement().getStyle().setProperty("backgroundColor", backgroundColor);
        panel.getElement().getStyle().setProperty("color", textColor);

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public CaseTag getValue() {
        return caseTag;
    }

    @Override
    public boolean isEnabled() {
        return remove.isVisible();
    }

    @Override
    public void setEnabled(boolean enabled) {
        remove.setVisible(enabled);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CaseTag> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<CaseTag> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    @UiHandler("remove")
    public void closeClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (!remove.isVisible()) {
            return;
        }
        CloseEvent.fire(this, caseTag);
    }
    @UiField
    FocusPanel root;

    @UiField
    HTMLPanel panel;

    @UiField
    Anchor remove;

    @UiField
    InlineLabel text;

    private CaseTag caseTag = null;

    interface CaseTagViewUiBinder extends UiBinder<FocusPanel, CaseTagView> {}
    private static CaseTagViewUiBinder ourUiBinder = GWT.create(CaseTagViewUiBinder.class);
}
