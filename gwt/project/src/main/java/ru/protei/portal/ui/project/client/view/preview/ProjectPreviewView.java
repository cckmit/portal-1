package ru.protei.portal.ui.project.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewActivity;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewView;

import java.util.Set;

/**
 * Вид превью проекта
 */
public class ProjectPreviewView extends Composite implements AbstractProjectPreviewView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
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
    public void setName( String value ) { this.projectName.setInnerText( value ); }

    @Override
    public void setHeader( String value ) { this.header.setInnerText( value ); }

    @Override
    public void setCreationDate( String value ) { this.creationDate.setInnerText( value ); }

    @Override
    public void setState( long value) {
        En_RegionState regionState = En_RegionState.forId( value );
        this.iconState.setClassName(regionStateLang.getStateIcon( regionState )+" fa-lg");
        this.state.setInnerText( regionStateLang.getStateName( regionState ) );
    }

    @Override
    public void setDirection( String value ) { this.projectDirection.setInnerText( value ); }

    @Override
    public void setTeam( Set<PersonProjectMemberView> value ) { this.team.setInnerText( value.toString() ); }

    @Override
    public void setDescription( String value ) { this.description.setInnerText( value ); }

    @Override
    public void setRegion( String value ) { this.projectRegion.setInnerText( value ); }

    @Override
    public void setProducts( Set<ProductShortView> value ) { this.products.setInnerText( value.toString() ); }

    @Override
    public void setCompany( String value ) { this.company.setInnerText( value ); }

    @Override
    public void setCustomerType( String value ) { this.customerType.setInnerText( value );}

    @Override
    public void showFullScreen( boolean value ) {
        fullScreenBtn.setVisible( !value );
        backButton.setVisible( value );
        if ( value ) {
            preview.addStyleName( "col-xs-12 col-lg-6" );
        } else {
            preview.removeStyleName( "col-xs-12 col-lg-6" );
        }
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @Override
    public HasWidgets getDocumentsContainer() {
        return documents;
    }

    @UiHandler( "fullScreenBtn" )
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

    @UiField
    HTMLPanel preview;

    @UiField
    Button fullScreenBtn;
    @UiField
    Button backButton;

    @UiField
    Element header;
    @UiField
    SpanElement creationDate;
    @UiField
    SpanElement projectName;
    @UiField
    SpanElement description;
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
    DivElement products;
    @UiField
    DivElement team;
    @UiField
    HTMLPanel documents;
    @UiField
    HTMLPanel commentsContainer;

    @Inject
    @UiField
    Lang lang;
    @Inject
    En_RegionStateLang regionStateLang;

    @Inject
    FixedPositioner positioner;

    AbstractProjectPreviewActivity activity;

    interface ProjectPreviewViewUiBinder extends UiBinder<HTMLPanel, ProjectPreviewView> {}
    private static ProjectPreviewViewUiBinder ourUiBinder = GWT.create( ProjectPreviewViewUiBinder.class );
}