package ru.protei.portal.ui.issuereport.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilter;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterActivity;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateActivity;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateView;
import ru.protei.portal.ui.issuereport.client.widget.ReportTypeButtonSelector;

import java.util.Set;

public class IssueReportCreateView extends Composite implements AbstractIssueReportCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractIssueReportCreateActivity activity, IssueFilterActivity issueFilterActivity) {
        this.activity = activity;
        this.issueFilter.setActivity(issueFilterActivity);
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
        return issueFilter.userFilter();
    }

    @Override
    public HasValue<Set<EntityOption>> companies() {
        return issueFilter.companies();
    }

    @Override
    public HasValue<Set<ProductShortView>> products() {
        return issueFilter.products();
    }

    @Override
    public HasValue<Set<PersonShortView>> managers() {
        return issueFilter.managers();
    }

    @Override
    public HasValue<Set<PersonShortView>> initiators() {
        return issueFilter.initiators();
    }

    @Override
    public HasValue<Set<En_CaseState>> states() {
        return issueFilter.states();
    }

    @Override
    public HasValue<Set<En_ImportanceLevel>> importances() {
        return issueFilter.importances();
    }

    @Override
    public HasValue<DateInterval> dateRange() {
        return issueFilter.dateRange();
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return issueFilter.sortField();
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return issueFilter.sortDir();
    }

    @Override
    public HasValue<String> searchPattern() {
        return issueFilter.searchPattern();
    }

    @Override
    public HasValue<Boolean> searchPrivate() {
        return issueFilter.searchPrivate();
    }

    @Override
    public HasVisibility companiesVisibility() {
        return issueFilter.companiesVisibility();
    }

    @Override
    public HasVisibility productsVisibility() {
        return issueFilter.productsVisibility();
    }

    @Override
    public HasVisibility managersVisibility() {
        return issueFilter.managersVisibility();
    }

    @Override
    public HasVisibility searchPrivateVisibility() {
        return issueFilter.searchPrivateVisibility();
    }

    @Override
    public HasVisibility searchByCommentsVisibility() {
        return issueFilter.searchByCommentsVisibility();
    }

    @Override
    public HasVisibility commentAuthorsVisibility() {
        return issueFilter.commentAuthorsVisibility();
    }

    @Override
    public HasValue<Set<PersonShortView>> commentAuthors() {
        return issueFilter.commentAuthors();
    }

    @Override
    public void resetFilter() {
        issueFilter.resetFilter();
        reportType.setValue(En_ReportType.CASE_OBJECTS, true);
        name.setValue(null);
    }

    @Override
    public void toggleMsgSearchThreshold() {
        issueFilter.toggleMsgSearchThreshold();
    }

    @Override
    public void fillFilterFields(CaseQuery caseQuery) {
        issueFilter.fillFilterFields(caseQuery);
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
    IssueFilter issueFilter;

    private AbstractIssueReportCreateActivity activity;

    interface IssueReportCreateViewUiBinder extends UiBinder<Widget, IssueReportCreateView> {}
    private static IssueReportCreateViewUiBinder ourUiBinder = GWT.create(IssueReportCreateViewUiBinder.class);
}
