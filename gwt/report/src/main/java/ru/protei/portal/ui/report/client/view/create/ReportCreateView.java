package ru.protei.portal.ui.report.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ReportAdditionalParamType;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.selector.report.additionalparams.ReportAdditionalParamsMultiSelector;
import ru.protei.portal.ui.common.client.widget.switcher.Switcher;
import ru.protei.portal.ui.report.client.activity.create.AbstractReportCreateActivity;
import ru.protei.portal.ui.report.client.activity.create.AbstractReportCreateView;
import ru.protei.portal.ui.report.client.widget.reporttype.ReportScheduledTypeButtonSelector;
import ru.protei.portal.ui.report.client.widget.reporttype.ReportTypeButtonSelector;

import java.util.List;
import java.util.Set;

public class ReportCreateView extends Composite implements AbstractReportCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractReportCreateActivity activity) {
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
    public HasWidgets getFilterContainer() {
        return filterContainer;
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
    public HasVisibility additionalParamsVisibility() {
        return additionalParamsContainer;
    }

    @Override
    public HasValue<Set<En_ReportAdditionalParamType>> additionalParams() {
        return additionalParams;
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
        additionalParams.ensureDebugId(DebugIds.ISSUE_REPORT.ADDITIONAL_PARAMS);
        additionalParams.setAddEnsureDebugId(DebugIds.ISSUE_REPORT.ADDITIONAL_PARAMS_ADD_BUTTON);
        additionalParams.setClearEnsureDebugId(DebugIds.ISSUE_REPORT.ADDITIONAL_PARAMS_CLEAR_BUTTON);
        additionalParams.setItemContainerEnsureDebugId(DebugIds.ISSUE_REPORT.ADDITIONAL_PARAMS_ITEM_CONTAINER);
        additionalParams.setLabelEnsureDebugId(DebugIds.ISSUE_REPORT.ADDITIONAL_PARAMS_LABEL);
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
    HTMLPanel additionalParamsContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    Button createButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField(provided = true)
    ReportAdditionalParamsMultiSelector additionalParams;

    private AbstractReportCreateActivity activity;

    interface IssueReportCreateViewUiBinder extends UiBinder<Widget, ReportCreateView> {}
    private static IssueReportCreateViewUiBinder ourUiBinder = GWT.create(IssueReportCreateViewUiBinder.class);
}
