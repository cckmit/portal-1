package ru.protei.portal.ui.project.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.customertype.CustomerTypeSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.region.RegionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.state.RegionStateButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.project.client.activity.edit.AbstractProjectEditActivity;
import ru.protei.portal.ui.project.client.activity.edit.AbstractProjectEditView;
import ru.protei.portal.ui.project.client.view.widget.team.TeamSelector;

import java.util.Set;

/**
 * Вид создания и редактирования проекта
 */
public class ProjectEditView extends Composite implements AbstractProjectEditView{

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        products.updateQuery(En_DevUnitState.ACTIVE, En_DevUnitType.COMPLEX, En_DevUnitType.PRODUCT);
        company.setDefaultValue(lang.selectIssueCompany());
        projectState.setDefaultValue(regionStateLang.getStateName(En_RegionState.UNKNOWN));
        projectRegion.setDefaultValue(lang.selectOfficialRegion());
        projectDirection.setDefaultValue(lang.contractSelectDirection());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
    }

    @Override
    public void setActivity(AbstractProjectEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<Integer> number() { return number; }

    @Override
    public HasValue<String> name() { return projectName; }

    @Override
    public HasText description() { return description; }

    @Override
    public HasValue<En_RegionState> state() { return projectState; }

    @Override
    public HasValue<ProductDirectionInfo> direction() { return projectDirection; }

    @Override
    public HasValue<Set<PersonProjectMemberView>> team() { return team; }

    @Override
    public HasValue< EntityOption > region() { return projectRegion; }

    @Override
    public HasValue<Set<ProductShortView>> products() {
//        return products;
        return null;
    }

    @Override
    public ProductShortView getProduct() {
        return products.getValue();
    }

    @Override
    public void setProduct(ProductShortView product) {
        products.setValue(product);
    }

    @Override
    public HasValue<EntityOption> company() { return company; }

    @Override
    public HasValue<En_CustomerType> customerType() { return customerType; }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @Override
    public HasWidgets getDocumentsContainer() { return documentsContainer; }

    @Override
    public HasValidable nameValidator() { return projectName; }

    @Override
    public HasVisibility numberVisibility() { return number; }

    @Override
    public HasVisibility saveVisibility() { return saveButton; }

    @Override
    public HasEnabled saveEnabled() { return saveButton; }

    @UiHandler( "saveButton" )
    public void onSaveClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onSaveClicked();
        }
    }
    @UiHandler( "cancelButton" )
    public void onCancelClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onCancelClicked();
        }
    }

    @Override
    public void showComments(boolean isShow) {
        if(isShow)
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

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        number.ensureDebugId(DebugIds.PROJECT.NUMBER_INPUT);
        projectName.ensureDebugId(DebugIds.PROJECT.NAME_INPUT);
        description.ensureDebugId(DebugIds.PROJECT.DESCRIPTION_INPUT);
        projectState.setEnsureDebugId(DebugIds.PROJECT.STATE_SELECTOR);
        projectRegion.setEnsureDebugId(DebugIds.PROJECT.REGION_SELECTOR);
        projectDirection.setEnsureDebugId(DebugIds.PROJECT.DIRECTION_SELECTOR);
        company.setEnsureDebugId(DebugIds.PROJECT.COMPANY_SELECTOR);
        customerType.setEnsureDebugId(DebugIds.PROJECT.CUSTOMER_TYPE_SELECTOR);
/*
        products.setEnsureDebugId(DebugIds.PROJECT.PRODUCTS_SELECTOR);
        team.setEnsureDebugId(DebugIds.PROJECT.TEAM_SELECTOR);
        documentsContainer.setEnsureDebugIdContainer(DebugIds.PROJECT.DOCUMENTS_CONTAINER);
        commentsContainer.setEnsureDebugIdContainer(DebugIds.PROJECT.COMMENTS_CONTAINER);
        */

        saveButton.ensureDebugId(DebugIds.PROJECT.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.PROJECT.CANCEL_BUTTON);
    }

    @UiField
    HTMLPanel root;

    @UiField
    IntegerBox number;
    @UiField
    ValidableTextBox projectName;
    @UiField
    HTMLPanel nameContainer;

    @UiField
    TextArea description;

    @Inject
    @UiField(provided = true)
    TeamSelector team;

    @Inject
    @UiField( provided = true )
    ProductDirectionButtonSelector projectDirection;

    @Inject
    @UiField( provided = true )
    RegionStateButtonSelector projectState;

    @Inject
    @UiField( provided = true )
    RegionButtonSelector projectRegion;

    @Inject
    @UiField(provided = true)
    CompanySelector company;

    @Inject
    @UiField(provided = true)
    DevUnitButtonSelector products;

    @Inject
    @UiField(provided = true)
    CustomerTypeSelector customerType;

    @UiField
    DivElement comments;
    @UiField
    HTMLPanel commentsContainer;
    @UiField
    DivElement documents;
    @UiField
    HTMLPanel documentsContainer;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;
    @Inject
    En_RegionStateLang regionStateLang;

    private AbstractProjectEditActivity activity;

    interface ProjectEditViewUiBinder extends UiBinder<HTMLPanel, ProjectEditView> {}
    private static ProjectEditViewUiBinder ourUiBinder = GWT.create(ProjectEditViewUiBinder.class);
}