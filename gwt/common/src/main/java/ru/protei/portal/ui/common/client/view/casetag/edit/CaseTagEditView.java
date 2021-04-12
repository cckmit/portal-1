package ru.protei.portal.ui.common.client.view.casetag.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.casetag.edit.AbstractCaseTagEditActivity;
import ru.protei.portal.ui.common.client.activity.casetag.edit.AbstractCaseTagEditView;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.colorpicker.ColorPicker;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

public class CaseTagEditView extends Composite implements AbstractCaseTagEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        company.setDefaultValue(lang.selectIssueCompany());
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractCaseTagEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public void setCaseTagNameStatus(NameStatus status) {
        verifiableIcon.setClassName(status.getStyle());
    }

    @Override
    public HasText caseTagNameErrorLabel() {
        return tagNameErrorLabel;
    }

    @Override
    public HasVisibility caseTagNameErrorLabelVisibility() {
        return tagNameErrorLabel;
    }

    @Override
    public HasValue<String> colorPicker() {
        return colorPicker;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasEnabled colorEnabled() {
        return colorPicker;
    }

    @Override
    public HasEnabled nameEnabled() {
        return name;
    }

    @Override
    public HasEnabled companyEnabled() {
        return company;
    }

    @Override
    public HasVisibility authorVisibility() {
        return authorContainer;
    }

    @Override
    public void setAuthor(String author) {
        label.setText(lang.tagAuthor() + " " + author);
    }

    private void ensureDebugIds() {
        tagNameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DIALOG_DETAILS.TAG.NAME_LABEL);
        verifiableIcon.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DIALOG_DETAILS.TAG.VERIFIABLE_ICON);
        tagColorLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DIALOG_DETAILS.TAG.COLOR_LABEL);
        tagCompanyLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DIALOG_DETAILS.TAG.COMPANY_LABEL);
        label.ensureDebugId(DebugIds.DIALOG_DETAILS.TAG.AUTHOR_LABEL);
        name.ensureDebugId(DebugIds.DIALOG_DETAILS.TAG.NAME_INPUT);
        company.ensureDebugId(DebugIds.DIALOG_DETAILS.TAG.COMPANY_SELECTOR);
    }

    @UiHandler("name")
    public void onChangeTagName(KeyUpEvent event) {
        verifiableIcon.setClassName(NameStatus.UNDEFINED.getStyle());
        timer.cancel();
        timer.schedule(300);
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    ValidableTextBox name;

    @UiField
    Label tagNameErrorLabel;

    @UiField
    Element verifiableIcon;

    @UiField
    HTMLPanel authorContainer;

    @UiField
    Label label;

    @UiField
    LabelElement tagNameLabel;

    @UiField
    LabelElement tagColorLabel;

    @UiField
    LabelElement tagCompanyLabel;

    @Inject
    @UiField(provided = true)
    ColorPicker colorPicker;

    @Inject
    @UiField(provided = true)
    CompanySelector company;

    Timer timer = new Timer() {
        @Override
        public void run() {
            if (activity != null) {
                activity.onChangeCaseTagName();
            }
        }
    };

    AbstractCaseTagEditActivity activity;

    interface CaseTagEditViewUiBinder extends UiBinder<Widget, CaseTagEditView> {}
    private static CaseTagEditViewUiBinder ourUiBinder = GWT.create(CaseTagEditViewUiBinder.class);
}
