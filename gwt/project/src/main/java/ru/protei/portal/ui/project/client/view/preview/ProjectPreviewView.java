package ru.protei.portal.ui.project.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionInputSelector;
import ru.protei.portal.ui.common.client.widget.selector.state.RegionStateIconSelector;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewActivity;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewView;

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

//    @Override
//    public void setPrivateIssue( boolean privateIssue ) {
//        if ( privateIssue ) {
//            this.privateIssue.setClassName( "fa fa-fw fa-lg fa-lock text-danger pull-left" );
//            return;
//        }
//
//        this.privateIssue.setClassName( "fa fa-fw fa-lg fa-unlock-alt text-success pull-left"  );
//    }


    @Override
    public void setName( String name ) {
        this.projectName.setValue( name );
    }

    @Override
    public void setHeader( String value ) {
        this.header.setInnerText( value );
    }

    @Override
    public void setCreationDate( String value ) {
        this.creationDate.setInnerText( value );
    }

    @Override
    public HasValue<En_RegionState> state() {
        return projectState;
    }

    //    @Override
//    public void setCriticality( int value ) {
//        En_ImportanceLevel importanceLevel = En_ImportanceLevel.find( value );
//        this.iconCriticality.setClassName( "importance importance-lg none-vertical-align " + importanceLevel.toString().toLowerCase() );
//        CriticalityStyleBuilder.make().addClassName( this.iconCriticality, En_ImportanceLevel.find( value ) );
//        this.criticality.setInnerText( caseImportanceLang.getImportanceName( importanceLevel ) );
//    }

    @Override
    public HasValue<ProductDirectionInfo> direction() {
        return projectDirection;
    }


//    @Override
//    public void setCompany( String value ) {
//        this.company.setInnerText( value );
//    }

//    @Override
//    public void setContact( String value ) {
//        this.contact.setInnerHTML( value );
//    }
//
//    @Override
//    public void setOurCompany( String value ) {
//        this.ourCompany.setInnerText( value );
//    }

    @Override
    public void setHeadManager( String value ) {
        this.headManager.setInnerText( value );
    }

    @Override
    public void setDetails( String value ) {
        this.details.setValue( value );
    }

    @Override
    public void showFullScreen( boolean value ) {
        this.fullScreen.setVisible( !value );
        if ( value ) {
            this.preview.addStyleName( "col-xs-12 col-lg-6" );
        } else {
            this.preview.removeStyleName( "col-xs-12 col-lg-6" );
        }
    }

//    @Override
//    public HasWidgets getCommentsContainer() {
//        return commentsContainer;
//    }

    @UiHandler( "fullScreen" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();

        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    @UiField
    HTMLPanel preview;
    @UiField
    Anchor fullScreen;
    @UiField
    Element header;
    @UiField
    SpanElement creationDate;
    @UiField
    SpanElement headManager;
    @UiField
    SpanElement deployManager;
    @UiField
    TextArea details;
    @Inject
    @UiField
    Lang lang;
//    @UiField
//    HTMLPanel commentsContainer;
    @UiField
    TextBox projectName;
    @Inject
    @UiField( provided = true )
    ProductDirectionInputSelector projectDirection;
    @Inject
    @UiField( provided = true )
    RegionStateIconSelector projectState;

    @Inject
    FixedPositioner positioner;

    AbstractProjectPreviewActivity activity;

    interface IssuePreviewViewUiBinder extends UiBinder<HTMLPanel, ProjectPreviewView> {}
    private static IssuePreviewViewUiBinder ourUiBinder = GWT.create( IssuePreviewViewUiBinder.class );
}