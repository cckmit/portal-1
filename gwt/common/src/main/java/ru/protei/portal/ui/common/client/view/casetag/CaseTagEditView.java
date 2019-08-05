package ru.protei.portal.ui.common.client.view.casetag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.casetag.AbstractCaseTagEditActivity;
import ru.protei.portal.ui.common.client.activity.casetag.AbstractCaseTagEditView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.colorpicker.ColorPicker;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;

public class CaseTagEditView extends Composite implements AbstractCaseTagEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        company.setDefaultValue(lang.selectIssueCompany());
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
    public void setVisibleCompanyPanel(boolean isVisible) {
        companyPanel.setVisible(isVisible);
    }

    @Override
    public void setVisibleAuthorPanel(boolean isVisible) {
        authorPanel.setVisible(isVisible);
    }

    @Override
    public void setAuthor(String author) {
        label.setText(lang.tagAuthor() + " " + author);
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    TextBox name;

    @UiField
    HTMLPanel companyPanel;

    @UiField
    HTMLPanel authorPanel;

    @UiField
    Label label;

    @Inject
    @UiField(provided = true)
    ColorPicker color;

    @Inject
    @UiField(provided = true)
    CompanySelector company;

    interface CaseTagEditViewUiBinder extends UiBinder<Widget, CaseTagEditView> {}
    private static CaseTagEditViewUiBinder ourUiBinder = GWT.create(CaseTagEditViewUiBinder.class);
}
