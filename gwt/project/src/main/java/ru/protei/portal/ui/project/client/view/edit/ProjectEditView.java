package ru.protei.portal.ui.project.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PlanOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.customertype.CustomerFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.plan.selector.PlanMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitWithImageMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.region.RegionFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.state.ProjectStateFormSelector;
import ru.protei.portal.ui.common.client.widget.sla.SlaInput;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.project.client.activity.edit.AbstractProjectEditActivity;
import ru.protei.portal.ui.project.client.activity.edit.AbstractProjectEditView;
import ru.protei.portal.ui.project.client.view.widget.team.TeamSelector;

import java.util.*;

/**
 * Вид создания и редактирования проекта
 */
public class ProjectEditView extends Composite implements AbstractProjectEditView{

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        productModel.setUnitState(En_DevUnitState.ACTIVE);
        productModel.setUnitTypes(En_DevUnitType.COMPLEX, En_DevUnitType.PRODUCT);
        products.setModel(productModel);
        company.setDefaultValue(lang.selectIssueCompany());

        projectState.setDefaultValue(lang.projectStateUnknown());
        projectRegion.setDefaultValue(lang.selectOfficialRegion());
        customerType.setDefaultValue(lang.selectCustomerType());

        companyModel.setCategories(Arrays.asList(En_CompanyCategory.SUBCONTRACTOR));
        subcontractors.setAsyncModel(companyModel);
    }

    @Override
    public void setActivity(AbstractProjectEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setNumber(Integer number) {
        this.number.setText(lang.projectHeader(String.valueOf(number)));
    }

    @Override
    public void setHideNullValue(boolean isHideNullValue) {
        customerType.setHideNullValue(isHideNullValue);
    }

    @Override
    public HasValue<String> name() { return projectName; }

    @Override
    public HasText description() { return description; }

    @Override
    public HasValue<CaseState> state() { return projectState; }

    @Override
    public HasValue<Set<ProductDirectionInfo>> directions() { return productDirection; }

    @Override
    public HasValidable nameValidator() {
        return projectName;
    }

    @Override
    public HasValue<Set<PersonProjectMemberView>> team() { return team; }

    @Override
    public HasValue< EntityOption > region() { return projectRegion; }

    @Override
    public HasValue<Set<ProductShortView>> products() {
        return products;
    }

    @Override
    public HasVisibility numberVisibility() {
        return number;
    }

    @Override
    public HasValue<EntityOption> company() { return company; }

    @Override
    public HasEnabled companyEnabled() {
        return company;
    }

    @Override
    public HasValue<En_CustomerType> customerType() { return customerType; }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @Override
    public HasVisibility saveVisibility() {
        return saveButton;
    }

    @Override
    public HasWidgets getDocumentsContainer() { return documentsContainer; }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public HasEnabled productEnabled() {
        return products;
    }


    @Override
    public HasWidgets getLinksContainer() {
        return linksContainer;
    }

    @Override
    public HasVisibility addLinkButtonVisibility() {
        return addLinkButton;
    }

    @Override
    public HasValue<List<ProjectSla>> slaInput() {
        return slaInput;
    }

    @Override
    public HasVisibility slaVisibility() {
        return slaContainer;
    }

    @Override
    public HasValue<Date> technicalSupportValidity() {
        return technicalSupportValidity;
    }

    @Override
    public HasValue<Date> workCompletionDate() {
        return workCompletionDate;
    }

    @Override
    public HasValue<Date> purchaseDate() {
        return purchaseDate;
    }

    @Override
    public void setTechnicalSupportDateValid(boolean valid) {
        technicalSupportValidity.markInputValid(valid);
    }

    @Override
    public void setWorkCompletionDateValid(boolean valid) {
        workCompletionDate.markInputValid(valid);
    }

    @Override
    public void setPurchaseDateValid(boolean valid) {
        purchaseDate.markInputValid(valid);
    }

    @Override
    public HasValidable slaValidator() {
        return slaInput;
    }

    @Override
    public void updateProductModel(Set<Long> directionIds) {
        productModel.setDirectionIds(directionIds);
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }
    @UiHandler("backButton")
    public void onBackButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    @Override
    public void showComments(boolean isShow) {
        if (isShow)
            comments.removeClassName(UiConstants.Styles.HIDE);
        else
            comments.addClassName(UiConstants.Styles.HIDE);
    }

    @Override
    public void showDocuments(boolean isShow) {
        if(isShow)
            documents.removeClassName(UiConstants.Styles.HIDE);
        else
            documents.addClassName(UiConstants.Styles.HIDE);
    }

    @Override
    public HasVisibility pauseDateContainerVisibility() {
        return pauseDateContainer;
    }
    @Override
    public HasValue<Date> pauseDate() {
        return pauseDate;
    }

    @Override
    public HasValue<Set<PlanOption>> plans() {
        return plans;
    }

    @Override
    public HasValue<Set<EntityOption>> subcontractors() {
        return subcontractors;
    }

    @UiHandler("addLinkButton")
    public void onAddLinkButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onAddLinkClicked(addLinkButton);
        }
    }

    @UiHandler("projectState")
    public void onProjectStateChanged(ValueChangeEvent<CaseState> event) {
        if (activity != null) {
            activity.onStateChanged();
        }
    }

    @UiHandler("productDirection")
    public void onDirectionChanged(ValueChangeEvent<Set<ProductDirectionInfo>> event) {
        if (activity != null) {
            activity.onDirectionChanged();
        }
    }

    @UiHandler("products")
    public void onProductChanged(ValueChangeEvent<Set<ProductShortView>> event) {
        if (activity != null) {
            activity.onProductChanged();
        }
    }

    @UiHandler("company")
    public void onCompanyChanged(ValueChangeEvent<EntityOption> event) {
        activity.onCompanyChanged();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        number.ensureDebugId(DebugIds.PROJECT.NUMBER_INPUT);
        projectName.ensureDebugId(DebugIds.PROJECT.NAME_INPUT);
        description.ensureDebugId(DebugIds.PROJECT.DESCRIPTION_INPUT);
        projectState.setEnsureDebugId(DebugIds.PROJECT.STATE_SELECTOR);
        projectRegion.setEnsureDebugId(DebugIds.PROJECT.REGION_SELECTOR);
        productDirection.ensureDebugId(DebugIds.PROJECT.DIRECTION_SELECTOR);
        products.ensureDebugId(DebugIds.PROJECT.PRODUCT_SELECTOR);
        company.setEnsureDebugId(DebugIds.PROJECT.COMPANY_SELECTOR);
        customerType.setEnsureDebugId(DebugIds.PROJECT.CUSTOMER_TYPE_SELECTOR);
        saveButton.ensureDebugId(DebugIds.PROJECT.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.PROJECT.CANCEL_BUTTON);
        addLinkButton.ensureDebugId(DebugIds.PROJECT.LINKS_BUTTON);
        slaInput.setEnsureDebugId(DebugIds.PROJECT.SLA_INPUT);
        technicalSupportValidity.setEnsureDebugId(DebugIds.PROJECT.TECHNICAL_SUPPORT_VALIDITY_CONTAINER);
        workCompletionDate.setEnsureDebugId(DebugIds.PROJECT.WORK_COMPLETION_DATE);
        purchaseDate.setEnsureDebugId(DebugIds.PROJECT.PURCHASE_DATE);
        subcontractors.setAddEnsureDebugId(DebugIds.PROJECT.SUBCONTRACTOR_SELECTOR_ADD_BUTTON);
        subcontractors.setClearEnsureDebugId(DebugIds.PROJECT.SUBCONTRACTOR_SELECTOR_CLEAR_BUTTON);
        subcontractors.setItemContainerEnsureDebugId(DebugIds.PROJECT.SUBCONTRACTOR_SELECTOR_ITEM_CONTAINER);
        subcontractors.setLabelEnsureDebugId(DebugIds.PROJECT.SUBCONTRACTOR_SELECTOR_LABEL);
    }

    @UiField
    HTMLPanel root;
    @UiField
    Label number;
    @UiField
    ValidableTextBox projectName;
    @UiField
    TextArea description;
    @Inject
    @UiField(provided = true)
    TeamSelector team;
    @Inject
    @UiField( provided = true )
    ProductDirectionMultiSelector productDirection;
    @Inject
    @UiField( provided = true )
    ProjectStateFormSelector projectState;
    @Inject
    @UiField( provided = true )
    RegionFormSelector projectRegion;
    @Inject
    @UiField(provided = true)
    CompanyFormSelector company;
    @Inject
    @UiField(provided = true)
    DevUnitWithImageMultiSelector products;
    @Inject
    @UiField(provided = true)
    CustomerFormSelector customerType;
    @Inject
    @UiField(provided = true)
    SlaInput slaInput;
    @UiField
    HTMLPanel slaContainer;
    @Inject
    @UiField(provided = true)
    SinglePicker technicalSupportValidity;
    @Inject
    @UiField(provided = true)
    SinglePicker workCompletionDate;
    @Inject
    @UiField(provided = true)
    SinglePicker purchaseDate;
    @UiField
    HTMLPanel pauseDateContainer;
    @Inject
    @UiField(provided = true)
    SinglePicker pauseDate;
    @Inject
    @UiField(provided = true)
    PlanMultiSelector plans;

    @UiField
    DivElement comments;
    @UiField
    HTMLPanel commentsContainer;
    @UiField
    DivElement documents;
    @UiField
    HTMLPanel documentsContainer;
    @UiField
    HTMLPanel linksContainer;

    @Inject
    @UiField( provided = true )
    CompanyMultiSelector subcontractors;

    @UiField
    Button addLinkButton;
    @UiField
    Button backButton;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;

    @Inject
    ProductModel productModel;

    @Inject
    CompanyModel companyModel;

    private AbstractProjectEditActivity activity;

    interface ProjectEditViewUiBinder extends UiBinder<HTMLPanel, ProjectEditView> {}
    private static ProjectEditViewUiBinder ourUiBinder = GWT.create(ProjectEditViewUiBinder.class);
}
