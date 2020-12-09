package ru.protei.portal.ui.common.client.widget.casetag.item;

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
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.util.ColorUtils;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class CaseTagSelectorItem extends Composite implements HasValue<CaseTag>, HasAddHandlers, HasEditHandlers, HasClickHandlers {

    public CaseTagSelectorItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initDebugIds();
    }

    public void setCaseType(En_CaseType caseType) {
        this.caseType = caseType;
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
        boolean isSystemScope = policyService.hasSystemScopeForPrivilege(privilegeByCaseType(caseType));
        boolean isCompanyEnabled = isCompanyEnabled(caseType);
        if (isSystemScope && isCompanyEnabled) {
            companyName.setText(caseTag.getCompanyName());
            companyName.setVisible(true);
        } else {
            companyName.setText("");
            companyName.setVisible(false);
        }
        icon.setText(ColorUtils.makeSingleCharName(caseTag.getName()));
        icon.getElement().getStyle().setProperty("backgroundColor", backgroundColor);
        icon.getElement().getStyle().setProperty("color", textColor);
        if (caseTag.getPersonName() != null) {
            getElement().setAttribute( "title", caseTag.getPersonName() );
        }

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

    private boolean isCompanyEnabled(En_CaseType caseType) {
        switch (caseType) {
            case CONTRACT: return false;
        }
        return true;
    }

    private En_Privilege privilegeByCaseType(En_CaseType caseType) {
        switch (caseType) {
            case CRM_SUPPORT: return En_Privilege.ISSUE_EDIT;
            case CONTRACT: return En_Privilege.CONTACT_EDIT;
        }
        return En_Privilege.ISSUE_EDIT;
    }

    private void initDebugIds() {
        panel.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TAG_SELECTOR_POPUP.ITEM);
        editIcon.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TAG_SELECTOR_POPUP.EDIT_BUTTON);
        text.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TAG_SELECTOR_POPUP.NAME);
        companyName.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.TAG_SELECTOR_POPUP.COMPANY_NAME);
    }

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
    private En_CaseType caseType;

    interface CaseTagViewUiBinder extends UiBinder<HTMLPanel, CaseTagSelectorItem> {}
    private static CaseTagViewUiBinder ourUiBinder = GWT.create(CaseTagViewUiBinder.class);
}
