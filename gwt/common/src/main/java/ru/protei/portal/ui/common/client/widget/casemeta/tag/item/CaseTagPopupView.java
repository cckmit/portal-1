package ru.protei.portal.ui.common.client.widget.casemeta.tag.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.util.ColorUtils;

public class CaseTagPopupView extends Composite implements HasValue<CaseTag>, HasAddHandlers, HasEditHandlers, HasClickHandlers {

    public CaseTagPopupView() {
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
        if (policyService.hasGrantAccessFor( En_Privilege.ISSUE_VIEW )) {
            companyName.setText(caseTag.getCompanyName());
            companyName.setVisible(true);
        } else {
            companyName.setText("");
            companyName.setVisible(false);
        }
        icon.setText(ColorUtils.makeSingleCharName(caseTag.getName()));
        icon.getElement().getStyle().setProperty("backgroundColor", backgroundColor);
        icon.getElement().getStyle().setProperty("color", textColor);
        getElement().setAttribute("title", caseTag.getPersonName());

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public CaseTag getValue() {
        return caseTag;
    }

    public void tagEditable(boolean isEditable) {
        if (isEditable) {
            editIcon.addStyleName("fas fa-pen");
        } else {
            editIcon.addStyleName("fas fa-external-link-alt");
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CaseTag> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return addHandler(handler, AddEvent.getType());
    }

    @Override
    public HandlerRegistration addEditHandler(EditHandler handler) {
        return addHandler(handler, EditEvent.getType());
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addHandler(handler, ClickEvent.getType());
    }

    public HasVisibility editIconVisibility() {
        return editIcon;
    }

    @UiHandler({"text", "companyName", "icon"})
    public void rootClick(ClickEvent event) {
        AddEvent.fire(this);
    }

    @UiHandler("editIcon")
    public void editClick(ClickEvent event) {
        ClickEvent.fireNativeEvent(event.getNativeEvent(), this);
    }

    @UiField
    FocusPanel root;
    @UiField
    HTMLPanel panel;
    @UiField
    InlineLabel text;
    @UiField
    InlineLabel companyName;

    @UiField
    InlineLabel icon;

    @UiField
    InlineLabel editIcon;

    @Inject
    PolicyService policyService;

    private CaseTag caseTag = null;

    interface CaseTagViewUiBinder extends UiBinder<FocusPanel, CaseTagPopupView> {}
    private static CaseTagViewUiBinder ourUiBinder = GWT.create(CaseTagViewUiBinder.class);
}
