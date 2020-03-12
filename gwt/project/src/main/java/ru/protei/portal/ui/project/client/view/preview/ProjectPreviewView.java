package ru.protei.portal.ui.project.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewActivity;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewView;

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
    public void setCreatedBy(String value ) { this.createdBy.setInnerHTML( value ); }

    @Override
    public void setState( long value) {
        En_RegionState regionState = En_RegionState.forId( value );
        this.iconState.setClassName(regionStateLang.getStateIcon( regionState )+ " fa-lg");
        this.state.setInnerText( regionStateLang.getStateName( regionState ) );
    }

    @Override
    public void setDirection( String value ) { this.projectDirection.setInnerText( value ); }

    @Override
    public void setDescription( String value ) { this.description.setInnerText( value ); }

    @Override
    public void setRegion( String value ) { this.projectRegion.setInnerText( value ); }

    @Override
    public void setProduct(String value ) {
        this.product.setText( value );
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
    public void setContract(String value, String link) {
        contract.setText(value);
        contract.setHref(link);
    }

    @Override
    public void setPlatform(String value, String link) {
        platform.setText(value);
        platform.setHref(link);
    }

    @Override
    public void isFullScreen(boolean isFullScreen) {
        previewWrapperContainer.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
        if (isFullScreen) {
            metaTable.addClassName("p-r-15 p-l-15");
        } else {
            metaTable.removeClassName("p-r-15 p-l-15");
        }
    }

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

    @UiHandler("product")
    public void onProductLinkClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onProductLinkClicked();
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
        projectDirection.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.DIRECTION_LABEL);
        customerType.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.CUSTOMER_TYPE_LABEL);
        company.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.COMPANY_LABEL);
        team.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.TEAM_LABEL);
        product.ensureDebugId(DebugIds.PROJECT_PREVIEW.PRODUCTS_LABEL);
        documents.ensureDebugId(DebugIds.PROJECT_PREVIEW.DOCUMENTS_CONTAINER);
        commentsContainer.ensureDebugId(DebugIds.PROJECT_PREVIEW.COMMENTS_CONTAINER);
        contract.ensureDebugId(DebugIds.PROJECT_PREVIEW.CONTRACT_LABEL);
        platform.ensureDebugId(DebugIds.PROJECT_PREVIEW.PLATFORM_LABEL);
        technicalSupportValidity.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.TECHNICAL_SUPPORT_VALIDITY_CONTAINER);
    }

    @UiField
    HTMLPanel preview;
    @UiField
    Button backButton;
    @UiField
    Element createdBy;
    @UiField
    Anchor header;
    @UiField
    Anchor contract;
    @UiField
    Anchor platform;
    @UiField
    DivElement description;
    @UiField
    SpanElement projectRegion;
    @UiField
    SpanElement projectDirection;
    @UiField
    DivElement technicalSupportValidity;
    @UiField
    SpanElement company;
    @UiField
    SpanElement customerType;
    @UiField
    Element iconState;
    @UiField
    SpanElement state;
    @UiField
    Anchor product;
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
    @UiField
    Lang lang;
    @UiField
    HeadingElement name;
    @UiField
    HTMLPanel backButtonContainer;
    @Inject
    En_RegionStateLang regionStateLang;

    AbstractProjectPreviewActivity activity;

    interface ProjectPreviewViewUiBinder extends UiBinder<HTMLPanel, ProjectPreviewView> {}
    private static ProjectPreviewViewUiBinder ourUiBinder = GWT.create( ProjectPreviewViewUiBinder.class );
}