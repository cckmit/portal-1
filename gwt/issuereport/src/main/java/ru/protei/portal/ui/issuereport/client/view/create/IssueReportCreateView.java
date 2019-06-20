package ru.protei.portal.ui.issuereport.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.issuefilterselector.IssueFilterSelector;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateActivity;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateView;
import ru.protei.portal.ui.issuereport.client.widget.ReportTypeButtonSelector;

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
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<CaseFilterShortView> userFilter() {
        return userFilter;
    }

    @Override
    public void resetFilter() {
        reportType.setValue(En_ReportType.CASE_OBJECTS, true);
        userFilter.updateFilterType(En_CaseFilterType.CASE_OBJECTS);
        name.setValue(null);
        userFilter.setValue(null);
    }

    @Override
    public HasWidgets getReportContainer() {
        return reportContainer;
    }

    @Override
    public void fillReportTypes(List<En_ReportType> options) {
        reportType.fillOptions(options);
    }

    @UiHandler("reportType")
    public void onReportTypeSelected(ValueChangeEvent<En_ReportType> event) {
        if (activity != null) {
            activity.onReportTypeSelected();
        }
    }

    @UiHandler("userFilter")
    public void onKeyUpSearch(ValueChangeEvent<CaseFilterShortView> event) {
        if (activity != null) {
            activity.onUserFilterChanged();
        }
    }

    private void ensureDebugIds() {
        userFilter.setEnsureDebugId( DebugIds.FILTER.USER_FILTER.FILTERS_BUTTON );
    }

    @Inject
    @UiField(provided = true)
    ReportTypeButtonSelector reportType;

    @UiField
    TextBox name;

    @Inject
    @UiField(provided = true)
    IssueFilterSelector userFilter;

    @UiField
    HTMLPanel reportContainer;

    private AbstractIssueReportCreateActivity activity;

    interface IssueReportCreateViewUiBinder extends UiBinder<Widget, IssueReportCreateView> {}
    private static IssueReportCreateViewUiBinder ourUiBinder = GWT.create(IssueReportCreateViewUiBinder.class);
}
