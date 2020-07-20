package ru.protei.portal.ui.issuereport.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.switcher.Switcher;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateActivity;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateView;
import ru.protei.portal.ui.issuereport.client.widget.reporttype.ReportScheduledTypeButtonSelector;
import ru.protei.portal.ui.issuereport.client.widget.reporttype.ReportTypeButtonSelector;

import java.util.List;

public class IssueReportCreateView extends Composite implements AbstractIssueReportCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
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
    public void fillReportTypes(List<En_ReportType> options) {
        reportType.fillOptions(options);
        reportType.setEnabled(options.size() > 1);
    }

    @Override
    public HasWidgets getIssueFilterContainer() {
        return issueFilterWidgetContainer;
    }

    @Override
    public void fillReportScheduledTypes(List<En_ReportScheduledType> options) {
        scheduledType.fillOptions(options);
        scheduledType.setEnabled(options.size() > 1);
    }

    @Override
    public HasVisibility scheduledTypeContainerVisibility() {
        return scheduledTypeContainer;
    }

    @Override
    public HasVisibility checkImportanceHistoryContainerVisibility() {
        return checkImportanceHistoryContainer;
    }

    @Override
    public HasValue<Boolean> checkImportanceHistory() {
        return checkImportanceHistory;
    }

    @Override
    public HasValue<Boolean> withDescription() {
        return withDescription;
    }

    @Override
    public HasVisibility withDescriptionContainerVisibility() {
        return withDescriptionContainer;
    }

    @UiHandler("reportType")
    public void onReportTypeChanged(ValueChangeEvent<En_ReportType> event) {
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

    private void ensureDebugIds() {
        name.ensureDebugId(DebugIds.ISSUE_REPORT.NAME_INPUT);
        reportType.ensureDebugId(DebugIds.ISSUE_REPORT.REPORT_TYPE);
        scheduledType.ensureDebugId(DebugIds.ISSUE_REPORT.REPORT_SCHEDULED_TYPE);
        checkImportanceHistory.ensureDebugId(DebugIds.ISSUE_REPORT.IMPORTANCE_CHECK_HISTORY);
        withDescription.ensureDebugId(DebugIds.ISSUE_REPORT.WITH_DESCRIPTION);
        createButton.ensureDebugId(DebugIds.ISSUE_REPORT.CREATE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.ISSUE_REPORT.CANCEL_BUTTON);
    }

    @Inject
    @UiField(provided = true)
    ReportTypeButtonSelector reportType;
    @UiField
    TextBox name;
    @Inject
    @UiField(provided = true)
    ReportScheduledTypeButtonSelector scheduledType;
    @UiField
    HTMLPanel scheduledTypeContainer;
    @UiField
    HTMLPanel checkImportanceHistoryContainer;
    @UiField
    HTMLPanel withDescriptionContainer;

    @UiField
    Switcher checkImportanceHistory;
    @UiField
    Switcher withDescription;
    @UiField
    HTMLPanel issueFilterWidgetContainer;
    @UiField
    Button createButton;
    @UiField
    Button cancelButton;

    private AbstractIssueReportCreateActivity activity;

    interface IssueReportCreateViewUiBinder extends UiBinder<Widget, IssueReportCreateView> {}
    private static IssueReportCreateViewUiBinder ourUiBinder = GWT.create(IssueReportCreateViewUiBinder.class);
}
