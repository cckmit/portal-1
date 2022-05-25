package ru.protei.portal.ui.project.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.ProjectStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.sla.SlaInputReadOnly;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewActivity;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewView;

import java.util.List;
import java.util.Map;

/**
 * Вид превью проекта
 */
public class ProjectPreviewView extends Composite implements AbstractProjectPreviewView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    @Override
    public void setActivity( AbstractProjectPreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setHeader(String value ) { this.header.setText( value ); }

    @Override
    public void setName(String value) { this.name.setInnerText( value ); }

    @Override
    public void setHeaderHref(String link) {
        this.header.setHref(link);
    }
    @Override
    public void setCreatedBy(String value ) { this.createdBy.setInnerHTML( value ); }

    @Override
    public void setState( String value ) {
        CaseState state = new CaseState( value );
        this.iconState.setClassName(projectStateLang.getStateIcon( state )+ " fa-lg");
        this.state.setInnerText( projectStateLang.getStateName( state ) );
    }

    @Override
    public void setStateIconColor(String color) {
        this.iconState.getStyle().setColor(color);
    }

    @Override
    public void setDirections(String value ) { this.projectDirections.setInnerText( value ); }

    @Override
    public void setDescription( String value ) { this.description.setInnerText( value ); }

    @Override
    public void setRegion( String value ) { this.projectRegion.setInnerText( value ); }

    @Override
    public void setProducts(Map<Long, String> value ) {
        this.products.clear();
        value.forEach((id, name) -> {
            final Anchor anchor = new Anchor(name, "#");
            anchor.setStyleName("project-preview-product-links");
            anchor.addClickHandler(event -> {
                event.preventDefault();
                if (activity != null) {
                    activity.onProductLinkClicked(id);
                }
            });
            this.products.add(anchor);
        });
    }

    @Override
    public void setCompany( String value ) { this.company.setInnerText( value ); }

    @Override
    public void setCustomerType( String value ) { this.customerType.setInnerText( value );}

    @Override
    public void setTeam( String value ) { this.team.setInnerHTML( value ); }

    @Override
    public void setTechnicalSupportValidity(String value) {
        technicalSupportValidity.setInnerText(value);
    }

    @Override
    public void setWorkCompletionDate(String value) {
        workCompletionDate.setInnerText(value);
    }

    @Override
    public void setPurchaseDate(String value) {
        purchaseDate.setInnerText(value);
    }

    @Override
    public void setPauseDateValidity(String value) {
        pauseDate.setInnerText(value);
    }

    @Override
    public HasVisibility backButtonVisibility() {
        return backButtonContainer;
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @Override
    public HasWidgets getDocumentsContainer() {
        return documents;
    }

    @Override
    public HasWidgets getLinksContainer() {
        return linksContainer;
    }

    @Override
    public HasValue<List<ProjectSla>> slaInputReadOnly() {
        return slaInputReadOnly;
    }

    @Override
    public HasVisibility slaContainerVisibility() {
        return slaContainer;
    }

    @Override
    public void setContracts(Map<EntityOption, String> contractToLink) {
        addLinksToContainer(contractToLink, contracts);
    }

    @Override
    public void setPlatforms(Map<EntityOption, String> platformToLink) {
        addLinksToContainer(platformToLink, platforms);
    }

    @Override
    public void isFullScreen(boolean isFullScreen) {
        previewWrapperContainer.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
        slaInputReadOnly.setStyleName("p-r-15 p-l-15", isFullScreen);
        if (isFullScreen) {
            metaTable.addClassName("p-r-15 p-l-15");
            slaContainer.getElement().replaceClassName("col-md-10", "col-md-6");
        } else {
            metaTable.removeClassName("p-r-15 p-l-15");
            slaContainer.getElement().replaceClassName("col-md-6", "col-md-10");
        }
    }

    @Override
    public void setTechnicalSupportValidityVisible(boolean isVisible){
        if (isVisible) {
            technicalSupportValidityNotSetLabel.removeClassName(CrmConstants.Style.HIDE);
        } else {
            technicalSupportValidityNotSetLabel.addClassName(CrmConstants.Style.HIDE);
        }
    }

    @Override
    public void setWorkCompletionDateLabelVisible(boolean isVisible){
        if (isVisible) {
            workCompletionDateNotSetLabel.removeClassName(CrmConstants.Style.HIDE);
        } else {
            workCompletionDateNotSetLabel.addClassName(CrmConstants.Style.HIDE);
        }
    }

    @Override
    public void setPurchaseDateLabelVisible(boolean isVisible){
        if (isVisible) {
            purchaseDateNotSetLabel.removeClassName(CrmConstants.Style.HIDE);
        } else {
            purchaseDateNotSetLabel.addClassName(CrmConstants.Style.HIDE);
        }
    }

    @Override
    public void setSubcontractors(String value) { this.subcontractors.setInnerText(value); }

    @UiHandler( "header" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    @UiHandler( "backButton" )
    public void onGoToProjectClicked ( ClickEvent event) {
        if ( activity != null ) {
            activity.onGoToProjectClicked();
        }
    }

    private void addLinksToContainer(Map<EntityOption, String> valueToLink, HTMLPanel linksContainer) {
        linksContainer.getElement().removeAllChildren();

        for (Map.Entry<EntityOption, String> currEntry : valueToLink.entrySet()) {
            AnchorElement element = AnchorElement.as(DOM.createAnchor());
            element.setInnerText(currEntry.getKey().getDisplayText());
            element.setHref(currEntry.getValue());
            element.setAttribute("target", "_blank");
            linksContainer.getElement().appendChild(element);
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        header.ensureDebugId(DebugIds.PROJECT_PREVIEW.FULL_SCREEN_BUTTON);
        header.ensureDebugId(DebugIds.PROJECT_PREVIEW.TITLE_LABEL);
        createdBy.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.DATE_CREATED_LABEL);
        name.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.NAME_LABEL);
        description.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.INFO_LABEL);
        state.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.STATE_LABEL);
        projectRegion.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.REGION_LABEL);
        projectDirections.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.DIRECTION_LABEL);
        customerType.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.CUSTOMER_TYPE_LABEL);
        company.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.COMPANY_LABEL);
        team.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.TEAM_LABEL);
        products.ensureDebugId(DebugIds.PROJECT_PREVIEW.PRODUCTS_LABEL);
        documents.ensureDebugId(DebugIds.PROJECT_PREVIEW.DOCUMENTS_CONTAINER);
        commentsContainer.ensureDebugId(DebugIds.PROJECT_PREVIEW.COMMENTS_CONTAINER);
        contracts.ensureDebugId(DebugIds.PROJECT_PREVIEW.CONTRACTS_CONTAINER);
        platforms.ensureDebugId(DebugIds.PROJECT_PREVIEW.PLATFORMS_LABEL);
        technicalSupportValidity.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.TECHNICAL_SUPPORT_VALIDITY_CONTAINER);
        workCompletionDate.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.WORK_COMPLETION_DATE);
        purchaseDate.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.PURCHASE_DATE);
        slaInputReadOnly.ensureDebugId(DebugIds.PROJECT_PREVIEW.SLA_INPUT);
    }

    @UiField
    Button backButton;
    @UiField
    Element createdBy;
    @UiField
    Anchor header;
    @UiField
    HTMLPanel contracts;
    @UiField
    HTMLPanel platforms;
    @UiField
    DivElement description;
    @UiField
    SpanElement projectRegion;
    @UiField
    SpanElement projectDirections;
    @UiField
    DivElement technicalSupportValidity;
    @UiField
    DivElement workCompletionDate;
    @UiField
    DivElement purchaseDate;
    @UiField
    SpanElement company;
    @UiField
    SpanElement customerType;
    @UiField
    Element iconState;
    @UiField
    SpanElement state;
    @UiField
    SpanElement pauseDate;
    @UiField
    HTMLPanel products;
    @UiField
    DivElement team;
    @UiField
    HTMLPanel documents;
    @UiField
    HTMLPanel commentsContainer;
    @UiField
    HTMLPanel previewWrapperContainer;
    @UiField
    HTMLPanel linksContainer;
    @UiField
    DivElement metaTable;
    @Inject
    @UiField(provided = true)
    SlaInputReadOnly slaInputReadOnly;
    @UiField
    HTMLPanel slaContainer;

    @UiField
    SpanElement technicalSupportValidityNotSetLabel;
    @UiField
    SpanElement workCompletionDateNotSetLabel;
    @UiField
    SpanElement purchaseDateNotSetLabel;

    @Inject
    @UiField
    Lang lang;
    @UiField
    HeadingElement name;
    @UiField
    HTMLPanel backButtonContainer;
    @UiField
    DivElement subcontractors;
    @Inject
    ProjectStateLang projectStateLang;

    AbstractProjectPreviewActivity activity;

    interface ProjectPreviewViewUiBinder extends UiBinder<HTMLPanel, ProjectPreviewView> {}
    private static ProjectPreviewViewUiBinder ourUiBinder = GWT.create( ProjectPreviewViewUiBinder.class );
}
