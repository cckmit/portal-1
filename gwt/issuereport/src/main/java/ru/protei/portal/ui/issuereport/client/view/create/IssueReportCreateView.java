package ru.protei.portal.ui.issuereport.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterParamView;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateActivity;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateView;
import ru.protei.portal.ui.issuereport.client.widget.ReportTypeButtonSelector;

public class IssueReportCreateView extends Composite implements AbstractIssueReportCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        issueFilterParamView.addBodyStyles("grid grid-indent");
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
    public AbstractIssueFilterWidgetView getIssueFilterWidget() {
        return issueFilterParamView;
    }

    @Override
    public void resetFilter() {
        issueFilterParamView.resetFilter();
        reportType.setValue(En_ReportType.CASE_OBJECTS, true);
        name.setValue(null);
    }

    @UiHandler("reportType")
    public void onReportTypeSelected(ValueChangeEvent<En_ReportType> event) {
        if (activity != null) {
            activity.onReportTypeSelected();
        }
    }

    @Inject
    @UiField(provided = true)
    ReportTypeButtonSelector reportType;
    @UiField
    TextBox name;

    @Inject
    @UiField(provided = true)
    IssueFilterParamView issueFilterParamView;

    private AbstractIssueReportCreateActivity activity;

    interface IssueReportCreateViewUiBinder extends UiBinder<Widget, IssueReportCreateView> {}
    private static IssueReportCreateViewUiBinder ourUiBinder = GWT.create(IssueReportCreateViewUiBinder.class);
}