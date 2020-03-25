package ru.protei.portal.ui.document.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
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
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.document.doccategory.DocumentCategoryMultiSelector;
import ru.protei.portal.ui.common.client.widget.document.doctype.DocumentTypeSelector;
import ru.protei.portal.ui.common.client.widget.organization.OrganizationBtnGroupMulti;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.ProjectMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.stringselect.input.StringSelectInput;
import ru.protei.portal.ui.common.client.widget.threestate.ThreeStateButton;
import ru.protei.portal.ui.document.client.activity.filter.AbstractDocumentFilterActivity;
import ru.protei.portal.ui.document.client.activity.filter.AbstractDocumentFilterView;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DocumentFilterView extends Composite implements AbstractDocumentFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
        ensureDebugIds();
        dateRange.setPlaceholder(lang.selectDate());
        sortField.setType(ModuleType.DOCUMENT);
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
        documentCategory.setValue(null);
        documentType.setValue(null);
        approved.setValue(true);
        keywords.setValue(new LinkedList<>());
        sortDir.setValue(false);
        showDeprecated.setValue(false);
        projects.setValue(null);
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
    public HasValue<Set<EntityOption>> projects() {
        return projects;
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
    public HasValue<Set<En_DocumentCategory>> documentCategory() {
        return documentCategory;
    }

    @Override
    public HasValue<DocumentType> documentType() {
        return documentType;
    }

    @Override
    public HasValue<Boolean> approved() {
        return approved;
    }

    @Override
    public HasValue<List<String>> keywords() {
        return keywords;
    }

    @Override
    public HasValue<Boolean> showDeprecated() { return showDeprecated; }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @UiHandler("resetBtn")
    public void onResetClicked(ClickEvent event) {
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "showDeprecated" )
    public void onShowDeprecatedClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "name" )
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        fireChangeTimer();
    }

    @UiHandler("content")
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

    @UiHandler("projects")
    public void onProjectsChanged(ValueChangeEvent<Set<EntityOption>> event) {
        fireChangeTimer();
    }

    @UiHandler("documentCategory")
    public void onDocumentCategorySelected(ValueChangeEvent<Set<En_DocumentCategory>> event) {
        fireChangeTimer();
    }

    @UiHandler("documentType")
    public void onDocumentTypeSelected(ValueChangeEvent<DocumentType> event) {
        fireChangeTimer();
    }

    @UiHandler("approved")
    public void onApprovedClicked(ValueChangeEvent<Boolean> event) {
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

    private void ensureDebugIds() {
        name.ensureDebugId(DebugIds.DOCUMENT.FILTER.SEARCH_INPUT);
        sortByLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.FILTER.SORT_BY_LABEL);
        sortField.ensureDebugId(DebugIds.DOCUMENT.FILTER.SORT_BY_SELECTOR);
        sortDir.ensureDebugId(DebugIds.DOCUMENT.FILTER.SORT_BY_TOGGLE);
        dateRangeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.FILTER.CREATION_DATE_LABEL);
        dateRange.setEnsureDebugId(DebugIds.DOCUMENT.FILTER.CREATION_DATE_INPUT);
        dateRange.getRelative().ensureDebugId(DebugIds.DOCUMENT.FILTER.CREATION_DATE_BUTTON);
        managerLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.FILTER.MANAGER_LABEL);
        manager.ensureDebugId(DebugIds.DOCUMENT.FILTER.MANAGER_SELECTOR);
        projectsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.FILTER.PROJECT_LABEL);
        projects.setAddEnsureDebugId(DebugIds.DOCUMENT.FILTER.PROJECT_SELECTOR_ADD_BUTTON);
        projects.setClearEnsureDebugId(DebugIds.DOCUMENT.FILTER.PROJECT_SELECTOR_CLEAR_BUTTON);
        projects.setItemContainerEnsureDebugId(DebugIds.DOCUMENT.FILTER.PROJECT_SELECTOR_ITEM_CONTAINER);
        organizationCodeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.FILTER.ORGANIZATION_CODE_LABEL);
        organizationCode.setEnsureDebugId(En_OrganizationCode.PAMR, DebugIds.DOCUMENT.FILTER.ORGANIZATION_CODE_PROTEI);
        organizationCode.setEnsureDebugId(En_OrganizationCode.PDRA, DebugIds.DOCUMENT.FILTER.ORGANIZATION_CODE_PROTEI_ST);
        documentCategoryLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.FILTER.DOCUMENT_CATEGORY_LABEL);
        documentCategory.ensureDebugId(DebugIds.DOCUMENT.FILTER.DOCUMENT_CATEGORY_SELECTOR);
        documentTypeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.FILTER.DOCUMENT_TYPE_LABEL);
        documentType.ensureDebugId(DebugIds.DOCUMENT.FILTER.DOCUMENT_TYPE_SELECTOR);
        approvedLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.FILTER.APPROVED_LABEL);
        approved.setYesEnsureDebugId(DebugIds.DOCUMENT.FILTER.APPROVED_YES);
        approved.setNotDefinedEnsureDebugId(DebugIds.DOCUMENT.FILTER.APPROVED_ANY);
        approved.setNoEnsureDebugId(DebugIds.DOCUMENT.FILTER.APPROVED_NO);
        contentLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.FILTER.DOCUMENT_TEXT_LABEL);
        content.ensureDebugId(DebugIds.DOCUMENT.FILTER.DOCUMENT_TEXT_INPUT);
        keywordsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.FILTER.KEY_WORD_LABEL);
        keywords.setEnsureDebugId(DebugIds.DOCUMENT.FILTER.KEY_WORD_INPUT);
        showDeprecated.ensureDebugId(DebugIds.DOCUMENT.FILTER.SHOW_DEPRECATED_CHECKBOX);
        resetBtn.ensureDebugId(DebugIds.DOCUMENT.FILTER.RESET_BUTTON);
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

    @UiField
    CheckBox showDeprecated;

    @Inject
    @UiField
    Lang lang;

    @UiField
    CleanableSearchBox name;

    @UiField
    TextArea content;

    @Inject
    @UiField(provided = true)
    OrganizationBtnGroupMulti organizationCode;

    @Inject
    @UiField(provided = true)
    RangePicker dateRange;

    @UiField
    LabelElement dateRangeLabel;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector manager;

    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;

    @UiField
    LabelElement sortByLabel;

    @UiField
    ToggleButton sortDir;

    @UiField
    LabelElement managerLabel;

    @UiField
    LabelElement projectsLabel;

    @UiField
    LabelElement organizationCodeLabel;

    @UiField
    LabelElement documentCategoryLabel;

    @UiField
    LabelElement documentTypeLabel;

    @UiField
    LabelElement approvedLabel;

    @UiField
    LabelElement contentLabel;

    @UiField
    LabelElement keywordsLabel;

    @Inject
    @UiField(provided = true)
    DocumentCategoryMultiSelector documentCategory;

    @Inject
    @UiField(provided = true)
    DocumentTypeSelector documentType;

    @UiField
    ThreeStateButton approved;

    @Inject
    @UiField(provided = true)
    StringSelectInput keywords;

    @Inject
    @UiField(provided = true)
    ProjectMultiSelector projects;

    AbstractDocumentFilterActivity activity;

    private static DocumentFilterViewUiBinder outUiBinder = GWT.create(DocumentFilterViewUiBinder.class);

    interface DocumentFilterViewUiBinder extends UiBinder<HTMLPanel, DocumentFilterView> {
    }
}
