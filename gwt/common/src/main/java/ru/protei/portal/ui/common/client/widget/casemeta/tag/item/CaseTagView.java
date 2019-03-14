package ru.protei.portal.ui.common.client.widget.casemeta.tag.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.widget.casemeta.link.item.CaseLinkView;

public class CaseTagView extends Composite implements HasValue<CaseTag>, HasCloseHandlers<CaseTag>, HasAddHandlers, HasEnabled {

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

        String backgroundColor = makeSafeColor(caseTag.getColor());
        String textColor = makeContrastColor(backgroundColor);
        text.setText(caseTag.getName());
        icon.setText(makeSingleCharName(caseTag.getName()));
        icon.getElement().getStyle().setProperty("backgroundColor", backgroundColor);
        icon.getElement().getStyle().setProperty("color", textColor);

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
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return addHandler(handler, AddEvent.getType());
    }

    private String makeSingleCharName(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }
        return String.valueOf(name.charAt(0)).toUpperCase();
    }

    private String makeSafeColor(String color) {
        if (StringUtils.isNotBlank(color)) {
            return color;
        }
        return "#e9edef";
    }

    private String makeContrastColor(String color) {
        int colorBase = parseHexColor(color);
        int colorThreshold = parseHexColor("#757575");
        return colorBase > colorThreshold ? "#000000" : "#FFFFFF";
    }

    private int parseHexColor(String color) {
        if (color.charAt(0) == '#') {
            long parsed = Long.parseLong(color.substring(1), 16);
            if (color.length() == 7) {
                // Set the alpha value
                parsed |= 0x00000000ff000000;
            } else if (color.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int) parsed;
        }
        throw new IllegalArgumentException("Unknown color");
    }

    @UiHandler("remove")
    public void closeClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (!enabled) {
            return;
        }
        CloseEvent.fire(this, caseTag);
    }

    @UiHandler("root")
    public void rootClick(ClickEvent event) {
        AddEvent.fire(this);
    }

    @UiField
    FocusPanel root;
    @UiField
    HTMLPanel panel;
    @UiField
    Anchor remove;
    @UiField
    InlineLabel text;
    @UiField
    InlineLabel icon;

    private CaseTag caseTag = null;
    private boolean enabled = true;

    interface CaseTagViewUiBinder extends UiBinder<FocusPanel, CaseTagView> {}
    private static CaseTagViewUiBinder ourUiBinder = GWT.create(CaseTagViewUiBinder.class);
}
