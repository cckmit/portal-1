package ru.protei.portal.ui.common.client.widget.casemeta.tag.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;

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
        if (policyService.hasGrantAccessFor( En_Privilege.ISSUE_VIEW )) {
            companyName.setText(caseTag.getCompanyName());
        } else {
            companyName.setText("");
        }

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
        return COLOR_LIGHT_GRAY;
    }

    private String makeContrastColor(String color) {
        int colorBase = parseHexColor(color);
        int colorThreshold = parseHexColor(COLOR_CONTRAST_THRESHOLD);
        return colorBase > colorThreshold ? COLOR_BLACK : COLOR_WHITE;
    }

    private int parseHexColor(String color) {
        if (color.charAt(0) == '#') {
            color = color.substring(1);
        }
        if (color.length() != 6 && color.length() != 8) {
            return 0;
        }
        long parsed = Long.parseLong(color, 16);
        if (color.length() == 6) {
            // Set the alpha value
            parsed |= 0x00000000ff000000;
        }
        return (int) parsed;
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
    InlineLabel companyName;
    @UiField
    InlineLabel icon;

    @Inject
    PolicyService policyService;

    private CaseTag caseTag = null;

    private static final String COLOR_LIGHT_GRAY = "#e9edef";
    private static final String COLOR_CONTRAST_THRESHOLD = "#757575";
    private static final String COLOR_BLACK = "#000000";
    private static final String COLOR_WHITE = "#FFFFFF";

    interface CaseTagViewUiBinder extends UiBinder<FocusPanel, CaseTagView> {}
    private static CaseTagViewUiBinder ourUiBinder = GWT.create(CaseTagViewUiBinder.class);
}
