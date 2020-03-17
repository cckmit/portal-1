package ru.protei.portal.ui.issuereport.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterModel;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterView;
import ru.protei.portal.ui.common.client.view.filter.IssueFilterView;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterParamView;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateActivity;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateView;
import ru.protei.portal.ui.issuereport.client.widget.issuefilter.model.AbstractIssueFilter;
import ru.protei.portal.ui.issuereport.client.widget.issuefilter.IssueFilter;
import ru.protei.portal.ui.issuereport.client.widget.reporttype.ReportTypeButtonSelector;

import java.util.List;

public class IssueReportCreateView extends Composite implements AbstractIssueReportCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractIssueReportCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<En_ReportType> reportType() {
        return reportType;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public AbstractIssueFilterView getIssueFilter() {
        return issueFilter;
    }

    @Override
    public IssueFilterParamView getIssueFilterParams() {
        return issueFilter.getIssueFilterParams();
    }

    @Override
    public void reset() {
        reportType.setValue(En_ReportType.CASE_OBJECTS, true);
        name.setValue(null);
    }

    @Override
    public void fillReportTypes(List<En_ReportType> options) {
        reportType.fillOptions(options);
        reportType.setEnabled(options.size() > 1);
    }

    @UiHandler("reportType")
    public void onReportTypeChanged(ValueChangeEvent<En_ReportType> event) {
        issueFilter.updateFilterType(En_CaseFilterType.valueOf(reportType.getValue().name()));
        if (activity != null) {
            activity.onReportTypeChanged();
        }
    }

    @UiHandler("createButton")
    public void createButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void cancelButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    @Inject
    @UiField(provided = true)
    ReportTypeButtonSelector reportType;
    @UiField
    TextBox name;
    @Inject
    @UiField(provided = true)
    IssueFilterView issueFilter;
    @UiField
    Button createButton;
    @UiField
    Button cancelButton;

    private AbstractIssueReportCreateActivity activity;

    interface IssueReportCreateViewUiBinder extends UiBinder<Widget, IssueReportCreateView> {}
    private static IssueReportCreateViewUiBinder ourUiBinder = GWT.create(IssueReportCreateViewUiBinder.class);
}
