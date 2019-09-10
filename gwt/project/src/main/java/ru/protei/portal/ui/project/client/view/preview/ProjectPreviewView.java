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
import ru.protei.portal.ui.common.client.common.FixedPositioner;
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
    protected void onDetach() {
        super.onDetach();
        watchForScroll(false);
    }

    @Override
    public void watchForScroll(boolean isWatch) {
        if(isWatch)
            positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
        else
            positioner.ignore(this);
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
    public void setAuthor(String value) {
        this.author.setInnerHTML( value );
    }

    @Override
    public void setCreationDate( String value ) { this.creationDate.setInnerText( value ); }

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
    public void setProducts( String value ) { this.products.setInnerText( value ); }

    @Override
    public void setCompany( String value ) { this.company.setInnerText( value ); }

    @Override
    public void setCustomerType( String value ) { this.customerType.setInnerText( value );}

    @Override
    public void setTeam( String value ) { this.team.setInnerHTML( value ); }

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

    @UiHandler("header")
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

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        header.ensureDebugId(DebugIds.PROJECT_PREVIEW.FULL_SCREEN_BUTTON);
        header.ensureDebugId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.TITLE_LABEL);
        creationDate.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.DATE_CREATED_LABEL);
        header.ensureDebugId(DebugIds.PROJECT_PREVIEW.NAME_LABEL);
        description.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.INFO_LABEL);
        state.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.STATE_LABEL);
        projectRegion.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.REGION_LABEL);
        projectDirection.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.DIRECTION_LABEL);
        customerType.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.CUSTOMER_TYPE_LABEL);
        company.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.COMPANY_LABEL);
        team.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.TEAM_LABEL);
        products.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROJECT_PREVIEW.PRODUCTS_LABEL);
        documents.ensureDebugId(DebugIds.PROJECT_PREVIEW.DOCUMENTS_CONTAINER);
        commentsContainer.ensureDebugId(DebugIds.PROJECT_PREVIEW.COMMENTS_CONTAINER);
    }

    @UiField
    HTMLPanel preview;
    @UiField
    Element author;
    @UiField
    Button backButton;
    @UiField
    Element creationDate;
    @UiField
    Anchor header;
    @UiField
    DivElement description;
    @UiField
    SpanElement projectRegion;
    @UiField
    SpanElement projectDirection;
    @UiField
    SpanElement company;
    @UiField
    SpanElement customerType;
    @UiField
    Element iconState;
    @UiField
    SpanElement state;
    @UiField
    SpanElement products;
    @UiField
    DivElement team;
    @UiField
    HTMLPanel documents;
    @UiField
    HTMLPanel commentsContainer;

    @Inject
    @UiField
    Lang lang;
    @UiField
    HeadingElement name;
    @UiField
    HTMLPanel backButtonContainer;
    @Inject
    En_RegionStateLang regionStateLang;

    @Inject
    FixedPositioner positioner;

    AbstractProjectPreviewActivity activity;

    interface ProjectPreviewViewUiBinder extends UiBinder<HTMLPanel, ProjectPreviewView> {}
    private static ProjectPreviewViewUiBinder ourUiBinder = GWT.create( ProjectPreviewViewUiBinder.class );
}