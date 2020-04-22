package ru.protei.portal.ui.common.client.view.workerposition.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.workerposition.edit.AbstractWorkerPositionEditActivity;
import ru.protei.portal.ui.common.client.activity.workerposition.edit.AbstractWorkerPositionEditView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.homecompany.HomeCompanyButtonSelector;

public class WorkerPositionEditView extends Composite implements AbstractWorkerPositionEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        company.setDefaultValue(lang.selectIssueCompany());
    }

    @Override
    public void setActivity(AbstractWorkerPositionEditActivity activity) {}

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasEnabled nameEnabled() {
        return name;
    }

    @Override
    public HasEnabled companyEnabled() {
        return company;
    }

    @UiField
    Lang lang;

    @UiField
    TextBox name;

    @UiField
    LabelElement positionNameLabel;

    @UiField
    LabelElement positionCompanyLabel;

    @Inject
    @UiField(provided = true)
    HomeCompanyButtonSelector company;

    interface CaseTagEditViewUiBinder extends UiBinder<Widget, WorkerPositionEditView> {}
    private static CaseTagEditViewUiBinder ourUiBinder = GWT.create(CaseTagEditViewUiBinder.class);
}
