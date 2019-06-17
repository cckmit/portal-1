package ru.protei.portal.ui.common.client.view.casetag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.casetag.AbstractCaseTagCreateActivity;
import ru.protei.portal.ui.common.client.activity.casetag.AbstractCaseTagCreateView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.colorpicker.ColorPicker;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;

public class CaseTagCreateView extends Composite implements AbstractCaseTagCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        company.setDefaultValue(lang.selectIssueCompany());
    }

    @Override
    public void setActivity(AbstractCaseTagCreateActivity activity) {}

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
    public void setVisibleCompanyPanel() {
        companyPanel.setVisible(true);
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    TextBox name;

    @UiField
    HTMLPanel companyPanel;

    @Inject
    @UiField(provided = true)
    ColorPicker color;

    @Inject
    @UiField(provided = true)
    CompanySelector company;

    interface CaseTagCreateViewUiBinder extends UiBinder<Widget, CaseTagCreateView> {}
    private static CaseTagCreateViewUiBinder ourUiBinder = GWT.create(CaseTagCreateViewUiBinder.class);
}
