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
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateActivity;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateView;
import ru.protei.portal.ui.issuereport.client.widget.issuefilter.model.AbstractIssueFilter;
import ru.protei.portal.ui.issuereport.client.widget.issuefilter.IssueFilter;
import ru.protei.portal.ui.issuereport.client.widget.reporttype.ReportScheduledTypeButtonSelector;
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
    public HasValue<En_ReportScheduledType> reportScheduledType() {
        return scheduledType;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public AbstractIssueFilter getIssueFilter() {
        return issueFilter;
    }

    @Override
    public void reset() {
        reportType.setValue(En_ReportType.CASE_OBJECTS, true);
        scheduledType.setValue(En_ReportScheduledType.NONE);
        scheduledType.setVisible(false);
        scheduledTypeLabel.setVisible(false);
        name.setValue(null);
    }

    @Override
    public void fillReportTypes(List<En_ReportType> options) {
        reportType.fillOptions(options);
        reportType.setEnabled(options.size() > 1);
    }

    @Override
    public void fillReportScheduledTypes(List<En_ReportScheduledType> options) {
        scheduledType.fillOptions(options);
        scheduledType.setEnabled(options.size() > 1);
    }

    @UiHandler("reportType")
    public void onReportTypeChanged(ValueChangeEvent<En_ReportType> event) {
        issueFilter.updateFilterType(En_CaseFilterType.valueOf(reportType.getValue().name()));
        scheduledType.setValue(En_ReportScheduledType.NONE);
        scheduledType.setVisible(En_ReportType.CASE_TIME_ELAPSED.equals(event.getValue()));
        scheduledTypeLabel.setVisible(En_ReportType.CASE_TIME_ELAPSED.equals(event.getValue()));
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
    @UiField
    Label scheduledTypeLabel;
    @Inject
    @UiField(provided = true)
    ReportScheduledTypeButtonSelector scheduledType;
    @Inject
    @UiField(provided = true)
    IssueFilter issueFilter;
    @UiField
    Button createButton;
    @UiField
    Button cancelButton;

    private AbstractIssueReportCreateActivity activity;

    interface IssueReportCreateViewUiBinder extends UiBinder<Widget, IssueReportCreateView> {}
    private static IssueReportCreateViewUiBinder ourUiBinder = GWT.create(IssueReportCreateViewUiBinder.class);
}
