package ru.protei.portal.ui.common.client.view.casetag.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.casetag.edit.AbstractCaseTagEditActivity;
import ru.protei.portal.ui.common.client.activity.casetag.edit.AbstractCaseTagEditView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.colorpicker.ColorPicker;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;

public class CaseTagEditView extends Composite implements AbstractCaseTagEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        company.setDefaultValue(lang.selectIssueCompany());
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractCaseTagEditActivity activity) {}

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<String> color() {
        return color;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasEnabled colorEnabled() {
        return color;
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
        tagColorLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DIALOG_DETAILS.TAG.COLOR_LABEL);
        tagCompanyLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DIALOG_DETAILS.TAG.COMPANY_LABEL);
        label.ensureDebugId(DebugIds.DIALOG_DETAILS.TAG.AUTHOR_LABEL);
        name.ensureDebugId(DebugIds.DIALOG_DETAILS.TAG.NAME_INPUT);
        company.ensureDebugId(DebugIds.DIALOG_DETAILS.TAG.COMPANY_SELECTOR);
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    TextBox name;

    @UiField
    HTMLPanel companyPanel;

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
    ColorPicker color;

    @Inject
    @UiField(provided = true)
    CompanySelector company;

    interface CaseTagEditViewUiBinder extends UiBinder<Widget, CaseTagEditView> {}
    private static CaseTagEditViewUiBinder ourUiBinder = GWT.create(CaseTagEditViewUiBinder.class);
}
