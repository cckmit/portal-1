package ru.protei.portal.ui.documentation.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.documentation.client.activity.filter.AbstractDocumentationFilterActivity;
import ru.protei.portal.ui.documentation.client.activity.filter.AbstractDocumentationFilterView;
import ru.protei.portal.ui.documentation.client.widget.keyword.KeywordMultiSelector;
import ru.protei.portal.ui.documentation.client.widget.selector.DocumentTypeSelector;
import ru.protei.portal.ui.equipment.client.widget.organization.OrganizationBtnGroupMulti;

import java.util.Collections;
import java.util.Set;

public class DocumentationFilterView extends Composite implements AbstractDocumentationFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
        name.getElement().setPropertyString("placeholder", lang.documentationSearchNameOrProject());
        dateRange.setPlaceholder(lang.selectDate());
        sortField.setType(ModuleType.DOCUMENTATION);
    }

    @Override
    public void setActivity(AbstractDocumentationFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public void resetFilter() {
        name.setValue(null);
        manager.setValue(null);
        sortField.setValue(En_SortField.name);
        organizationCode.setValue(null);
        dateRange.setValue(null);
        documentType.setValue(null);
        keywords.setValue(Collections.emptySet());
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<String> content() {
        return content;
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return manager;
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<Set<En_OrganizationCode>> organizationCodes() {
        return organizationCode;
    }

    @Override
    public HasValue<DateInterval> dateRange() {
        return dateRange;
    }

    @Override
    public HasValue<DocumentType> documentType() {
        return documentType;
    }

    @Override
    public HasValue<Set<String>> keywords() {
        return keywords;
    }

    @UiHandler("resetBtn")
    public void onResetClicked(ClickEvent event) {
        if (activity != null) {
            resetFilter();
            if (activity != null) {
                activity.onFilterChanged();
            }
        }
    }

    @UiHandler("name")
    public void onKeyUpSearch(KeyUpEvent event) {
        fireChangeTimer();
    }

    @UiHandler("organizationCode")
    public void onSelectOrganizationCode(ValueChangeEvent<Set<En_OrganizationCode>> event) {
        fireChangeTimer();
    }

    @UiHandler("manager")
    public void onManagerSelected(ValueChangeEvent<PersonShortView> event) {
        fireChangeTimer();
    }

    @UiHandler("documentType")
    public void onDocumentTypeSelected(ValueChangeEvent<DocumentType> event) {
        fireChangeTimer();
    }

    @UiHandler("keywords")
    public void onKeywordschanged(ValueChangeEvent<Set<String>> event) {
        fireChangeTimer();
    }


    @UiHandler("sortField")
    public void onSortFieldChanged(ValueChangeEvent<En_SortField> event) {
        fireChangeTimer();
    }

    @UiHandler("dateRange")
    public void onDateRangeChanged(ValueChangeEvent<DateInterval> event) {
        fireChangeTimer();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

    private void fireChangeTimer() {
        timer.cancel();
        timer.schedule(300);
    }

    private final Timer timer = new Timer() {
        @Override
        public void run() {
            if (activity != null) {
                activity.onFilterChanged();
            }
        }
    };

    @UiField
    Button resetBtn;

    @Inject
    @UiField
    Lang lang;

    @UiField
    TextBox name;

    @UiField
    TextArea content;

    @Inject
    @UiField(provided = true)
    OrganizationBtnGroupMulti organizationCode;

    @Inject
    @UiField(provided = true)
    RangePicker dateRange;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector manager;

    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;

    @Inject
    @UiField(provided = true)
    DocumentTypeSelector documentType;

    @Inject
    @UiField(provided = true)
    KeywordMultiSelector keywords;

    @Inject
    FixedPositioner positioner;

    AbstractDocumentationFilterActivity activity;

    private static DocumentationFilterViewUiBinder outUiBinder = GWT.create(DocumentationFilterViewUiBinder.class);

    interface DocumentationFilterViewUiBinder extends UiBinder<HTMLPanel, DocumentationFilterView> {
    }
}
