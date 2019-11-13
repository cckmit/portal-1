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
        product.updateQuery(En_DevUnitState.ACTIVE, En_DevUnitType.COMPLEX, En_DevUnitType.PRODUCT);
        company.setDefaultValue(lang.selectIssueCompany());
        company.showDeprecated(false);
        product.setDefaultValue(lang.selectIssueProduct());
        projectState.setDefaultValue(regionStateLang.getStateName(En_RegionState.UNKNOWN));
        projectRegion.setDefaultValue(lang.selectOfficialRegion());
        productDirection.setDefaultValue(lang.contractSelectDirection());
        customerType.setDefaultValue(lang.selectCustomerType());
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
        productDirection.setHideNullValue(isHideNullValue);
        projectRegion.setHideNullValue(isHideNullValue);
        customerType.setHideNullValue(isHideNullValue);
        company.setHideNullValue(isHideNullValue);
    }

    @Override
    public HasValue<String> name() { return projectName; }

    @Override
    public HasText description() { return description; }

    @Override
    public HasValue<En_RegionState> state() { return projectState; }

    @Override
    public HasValue<ProductDirectionInfo> direction() { return productDirection; }

    @Override
    public HasValidable nameValidator() {
        return projectName;
    }

    @Override
    public HasValue<Set<PersonProjectMemberView>> team() { return team; }

    @Override
    public HasValue< EntityOption > region() { return projectRegion; }

    @Override
    public HasValue<ProductShortView> product() {
        return product;
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

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        number.ensureDebugId(DebugIds.PROJECT.NUMBER_INPUT);
        projectName.ensureDebugId(DebugIds.PROJECT.NAME_INPUT);
        description.ensureDebugId(DebugIds.PROJECT.DESCRIPTION_INPUT);
        projectState.setEnsureDebugId(DebugIds.PROJECT.STATE_SELECTOR);
        projectRegion.setEnsureDebugId(DebugIds.PROJECT.REGION_SELECTOR);
        productDirection.setEnsureDebugId(DebugIds.PROJECT.DIRECTION_SELECTOR);
        company.setEnsureDebugId(DebugIds.PROJECT.COMPANY_SELECTOR);
        customerType.setEnsureDebugId(DebugIds.PROJECT.CUSTOMER_TYPE_SELECTOR);
        saveButton.ensureDebugId(DebugIds.PROJECT.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.PROJECT.CANCEL_BUTTON);
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
    ProductDirectionButtonSelector productDirection;

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
    DevUnitButtonSelector product;

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