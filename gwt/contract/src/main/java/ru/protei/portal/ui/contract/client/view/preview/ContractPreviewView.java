package ru.protei.portal.ui.contract.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.tab.TabWidget;
import ru.protei.portal.ui.contract.client.activity.preview.AbstractContractPreviewActivity;
import ru.protei.portal.ui.contract.client.activity.preview.AbstractContractPreviewView;
import ru.protei.portal.ui.contract.client.widget.contractspecification.previewitem.ContractSpecificationPreviewItem;

import java.util.List;

public class ContractPreviewView extends Composite implements AbstractContractPreviewView {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    @Override
    public void setActivity( AbstractContractPreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentContainer;
    }

    @Override
    public HasWidgets getTagsContainer() {
        return tagsContainer;
    }

    @Override
    public void setHeader(String value) {
        this.header.setText(value);
    }

    @Override
    public void setHeaderHref(String link) {
        this.header.setHref(link);
    }

    @Override
    public void setState(String value) {
        this.state.setSrc(value);
    }

    @Override
    public void setDateSigning(String value) {
        this.dateSigning.setInnerHTML(value);
    }

    @Override
    public void setDateValid(String value) {
        this.dateValid.setInnerHTML(value);
    }

    @Override
    public void setDateExecution(String value) {
        this.dateExecution.setInnerHTML(value);
    }

    @Override
    public void setDateEndWarranty(String value) {
        this.dateEndWarranty.setInnerHTML(value);
    }

    @Override
    public void setDescription(String value) {
        this.description.setInnerHTML(value);
    }

    @Override
    public void setDirections(String value) {
        this.directions.setInnerHTML(value);
    }

    @Override
    public void setContractor(String value) {
        this.contractor.setInnerHTML(value);
    }

    @Override
    public void setCurator(String value) {
        this.curator.setInnerHTML(value);
    }

    @Override
    public void setDeliveryNumber(String value) {
        deliveryNumber.setInnerText(value);
    }

    @Override
    public void setProjectManager(String value) {
        this.projectManager.setInnerHTML(value);
    }

    @Override
    public void setContractSignManager(String value) {
        this.contractSignManager.setInnerHTML(value);
    }

    @Override
    public void setDates(List<Widget> value) {
        this.dates.clear();
        value.forEach(widget -> this.dates.add(widget));
    }

    @Override
    public void setSpecifications(List<ContractSpecificationPreviewItem> value) {
        this.specifications.clear();
        value.forEach(item -> this.specifications.add(item));
    }

    @Override
    public void setOrganization(String value) {
        this.organization.setInnerHTML(value);
    }

    @Override
    public void setParentContract(String value) {
        this.contractParent.setInnerHTML(value);
    }

    @Override
    public void setChildContracts(String value) {
        this.contractChild.setInnerHTML(value);
    }

    @Override
    public void setFileLocation(String value) {
        this.fileLocation.setInnerText(value);
    }
    
    @Override
    public void setNotifies(String value) {
        this.notifies.setInnerText(value);
    }

    @Override
    public void setProject(String value, String link) {
        project.setText(value);
        project.setHref(link);
    }

    @Override
    public HasVisibility footerVisibility() {
        return footerContainer;
    }

    @Override
    public void isFullScreen(boolean isFullScreen) {
        previewWrapperContainer.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
    }

    @UiHandler("header")
    public void onFullScreenClicked(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onFullScreenClicked();
        }
    }

    @UiHandler("backButton")
    public void onGoToContractsClicked(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onGoToContractsClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        header.ensureDebugId(DebugIds.CONTRACT_PREVIEW.CONTRACT_TITLE_LABEL);
        description.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.CONTRACT_NAME_LABEL);
        contractor.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.CONTRACTOR_LABEL);
        dateSigning.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.DATE_SIGNING_LABEL);
        dateValid.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.DATE_VALID_LABEL);
        directions.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.DIRECTIONS_LABEL);
        deliveryNumber.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.DELIVERY_NUMBER_LABEL);
        contractParent.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.RECEIPT_AGREEMENT_LABEL);
        contractChild.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.EXPENDITURE_AGREEMENT_LABEL);
        tagsContainer.ensureDebugId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.TAGS_LABEL);
        organization.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.ORGANIZATION_LABEL);
        fileLocation.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.FILE_LOCATION);
        project.ensureDebugId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.PROJECT_LABEL);
        curator.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.CURATOR_LABEL);
        projectManager.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.PROJECT_MANAGER_LABEL);
        contractSignManager.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.SIGN_MANAGER_LABEL);
        dateEndWarranty.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.DATE_END_WARRANTY_LABEL);
        dateExecution.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.DATE_EXECUTION_LABEL);
        notifies.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT_PREVIEW.NOTIFIERS);
        tabs.setTabNameDebugId(lang.contractDeliveryAndPaymentsPeriodHeader(), DebugIds.CONTRACT.DELIVERY_AND_PAYMENTS_PERIOD_TAB);
        tabs.setTabNameDebugId(lang.contractSpecificationHeader(), DebugIds.CONTRACT.SPECIFICATION_TAB);
    }

    @UiField
    Lang lang;
    @UiField
    HTMLPanel commentContainer;
    @UiField
    SpanElement dateSigning;
    @UiField
    SpanElement dateValid;
    @UiField
    SpanElement description;
    @UiField
    SpanElement directions;
    @UiField
    ImageElement state;
    @UiField
    SpanElement organization;
    @UiField
    SpanElement projectManager;
    @UiField
    SpanElement curator;
    @UiField
    SpanElement contractor;
    @UiField
    Anchor header;
    @UiField
    HTMLPanel dates;
    @UiField
    HTMLPanel specifications;
    @UiField
    SpanElement contractParent;
    @UiField
    SpanElement contractChild;
    @UiField
    SpanElement fileLocation;
    @UiField
    Anchor project;
    @UiField
    HTMLPanel footerContainer;
    @UiField
    HTMLPanel previewWrapperContainer;
    @UiField
    HTMLPanel tagsContainer;
    @UiField
    SpanElement contractSignManager;
    @UiField
    SpanElement notifies;
    @UiField
    SpanElement deliveryNumber;
    @UiField
    TabWidget tabs;
    @UiField
    SpanElement dateExecution;
    @UiField
    SpanElement dateEndWarranty;

    private AbstractContractPreviewActivity activity;

    private static PreviewViewUiBinder ourUiBinder = GWT.create( PreviewViewUiBinder.class );
    interface PreviewViewUiBinder extends UiBinder< HTMLPanel, ContractPreviewView> {}
}