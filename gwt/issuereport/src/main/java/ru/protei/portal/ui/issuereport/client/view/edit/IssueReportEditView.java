package ru.protei.portal.ui.issuereport.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.subscription.locale.LocaleButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.issue.client.widget.importance.btngroup.ImportanceBtnGroupMulti;
import ru.protei.portal.ui.issue.client.widget.state.option.IssueStatesOptionList;
import ru.protei.portal.ui.issuereport.client.activity.edit.AbstractIssueReportEditActivity;
import ru.protei.portal.ui.issuereport.client.activity.edit.AbstractIssueReportEditView;

import java.util.Set;

public class IssueReportEditView extends Composite implements AbstractIssueReportEditView {

    @Override
    public void setActivity(AbstractIssueReportEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> title() {
        return title;
    }

    @Override
    public HasValue<String> locale() {
        return locale;
    }

    @Override
    public HasText search() {
        return search;
    }

    @Override
    public HasValue<DateInterval> dateRange() {
        return dateRange;
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @Override
    public HasValue<Set<EntityOption>> companies() {
        return companies;
    }

    @Override
    public HasValue<Set<ProductShortView>> products() {
        return products;
    }

    @Override
    public HasValue<Set<PersonShortView>> managers() {
        return managers;
    }

    @Override
    public HasValue<Set<En_ImportanceLevel>> importance() {
        return importance;
    }

    @Override
    public HasValue<Set<En_CaseState>> state() {
        return state;
    }

    @Override
    public HasEnabled titleEnabled() {
        return title;
    }

    @Override
    public HasEnabled localeEnabled() {
        return locale;
    }

    @Override
    public HasEnabled searchEnabled() {
        return search;
    }

    @Override
    public HasEnabled dateRangeEnabled() {
        return dateRange;
    }

    @Override
    public HasEnabled sortFieldEnabled() {
        return sortField;
    }

    @Override
    public HasEnabled sortDirEnabled() {
        return sortDir;
    }

    @Override
    public HasEnabled companiesEnabled() {
        return companies;
    }

    @Override
    public HasEnabled productsEnabled() {
        return products;
    }

    @Override
    public HasEnabled managersEnabled() {
        return managers;
    }

    @Override
    public HasEnabled importanceEnabled() {
        return importance;
    }

    @Override
    public HasEnabled stateEnabled() {
        return state;
    }

    @Override
    public HasVisibility companiesVisibility() {
        return companies;
    }

    @Override
    public HasVisibility productsVisibility() {
        return products;
    }

    @Override
    public HasVisibility managersVisibility() {
        return managers;
    }

    @Override
    public HasVisibility requestButtonVisibility() {
        return requestButton;
    }

    @UiHandler("requestButton")
    public void requestButtonClick(ClickEvent event) {
        activity.onRequestClicked();
    }

    @UiHandler("cancelButton")
    public void cancelButtonClick(ClickEvent event) {
        activity.onCancelClicked();
    }

    @Inject
    @UiField(provided = true)
    LocaleButtonSelector locale;

    @UiField
    ValidableTextBox title;

    @UiField
    TextBox search;

    @Inject
    @UiField(provided = true)
    RangePicker dateRange;

    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @Inject
    @UiField(provided = true)
    CompanyMultiSelector companies;

    @Inject
    @UiField(provided = true)
    ProductMultiSelector products;

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector managers;

    @Inject
    @UiField(provided = true)
    ImportanceBtnGroupMulti importance;

    @Inject
    @UiField(provided = true)
    IssueStatesOptionList state;

    @UiField
    Button requestButton;

    @UiField
    Button cancelButton;

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        locale.setValue("ru");
        sortField.setValue(En_SortField.creation_date);
    }

    private AbstractIssueReportEditActivity activity;

    private static IssueReportEditViewUiBinder ourUiBinder = GWT.create(IssueReportEditViewUiBinder.class);
    interface IssueReportEditViewUiBinder extends UiBinder<HTMLPanel, IssueReportEditView> {}
}
