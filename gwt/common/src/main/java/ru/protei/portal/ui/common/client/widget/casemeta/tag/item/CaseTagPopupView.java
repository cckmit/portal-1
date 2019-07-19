package ru.protei.portal.ui.common.client.widget.casemeta.tag.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.util.ColorUtils;

public class CaseTagPopupView extends Composite implements HasValue<CaseTag>, HasAddHandlers{

    @Inject
    public CaseTagPopupView(NotifyActivity activity, CompanyControllerAsync companyService) {
        this.activity = activity;
        this.companyService = companyService;
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

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public CaseTag getValue() {
        return caseTag;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CaseTag> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return addHandler(handler, AddEvent.getType());
    }

    @UiHandler({"text", "companyName", "icon"})
    public void rootClick(ClickEvent event) {
        AddEvent.fire(this);
    }

    @UiHandler("editIcon")
    public void editClick(ClickEvent event) {
        companyService.getCompany(caseTag.getCompanyId(), new AsyncCallback<Company>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Company result) {
                activity.fireEvent(new CaseTagEvents.Create(caseTag, result));
            }
        });
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
    private NotifyActivity activity;
    private CompanyControllerAsync companyService;

    interface CaseTagViewUiBinder extends UiBinder<FocusPanel, CaseTagPopupView> {}
    private static CaseTagViewUiBinder ourUiBinder = GWT.create(CaseTagViewUiBinder.class);
}
