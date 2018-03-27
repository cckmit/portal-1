package ru.protei.portal.ui.document.client.view.filter;

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
import ru.protei.portal.ui.common.client.widget.organization.OrganizationBtnGroupMulti;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.stringselect.input.StringSelectInput;
import ru.protei.portal.ui.document.client.activity.filter.AbstractDocumentFilterActivity;
import ru.protei.portal.ui.document.client.activity.filter.AbstractDocumentFilterView;
import ru.protei.portal.ui.document.client.widget.doctype.DocumentTypeSelector;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DocumentFilterView extends Composite implements AbstractDocumentFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
        name.getElement().setPropertyString("placeholder", lang.documentSearchNameOrProject());
        dateRange.setPlaceholder(lang.selectDate());
        sortField.setType(ModuleType.DOCUMENT);
        documentType.setDefaultValue(lang.documentTypeNotDefined());
    }

    @Override
    public void setActivity(AbstractDocumentFilterActivity activity) {
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
        keywords.setValue(new LinkedList<>());
        sortDir.setValue(false);
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
    public HasValue<List<String>> keywords() {
        return keywords;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
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

    @UiHandler({"name", "content"})
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
    public void onKeywordschanged(ValueChangeEvent<List<String>> event) {
        fireChangeTimer();
    }

    @UiHandler("sortDir")
    public void onSortDirChanged(ValueChangeEvent<Boolean> event) {
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

    @UiField
    ToggleButton sortDir;

    @Inject
    @UiField(provided = true)
    DocumentTypeSelector documentType;

    @Inject
    @UiField(provided = true)
    StringSelectInput keywords;

    @Inject
    FixedPositioner positioner;

    AbstractDocumentFilterActivity activity;

    private static DocumentFilterViewUiBinder outUiBinder = GWT.create(DocumentFilterViewUiBinder.class);

    interface DocumentFilterViewUiBinder extends UiBinder<HTMLPanel, DocumentFilterView> {
    }
}
