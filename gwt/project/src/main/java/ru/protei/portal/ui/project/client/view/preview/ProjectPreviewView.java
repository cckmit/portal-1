package ru.protei.portal.ui.project.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewActivity;
import ru.protei.portal.ui.project.client.activity.preview.AbstractProjectPreviewView;

/**
 * Вид превью проекта
 */
public class ProjectPreviewView extends Composite implements AbstractProjectPreviewView {

    public ProjectPreviewView() {
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
    public void setHeader( String value ) {
        this.header.setInnerText( value );
    }

    @Override
    public void setCreationDate( String value ) {
        this.creationDate.setInnerText( value );
    }

    @Override
    public void setState( En_RegionState state ) {
//        En_CaseState caseState = En_CaseState.getById( value );
        this.state.setClassName( "small label label-" + state.toString().toLowerCase());
        this.state.setInnerText( stateLang.getStateName( state ) );
    }

//    @Override
//    public void setCriticality( int value ) {
//        En_ImportanceLevel importanceLevel = En_ImportanceLevel.find( value );
//        this.iconCriticality.setClassName( "importance importance-lg none-vertical-align " + importanceLevel.toString().toLowerCase() );
//        CriticalityStyleBuilder.make().addClassName( this.iconCriticality, En_ImportanceLevel.find( value ) );
//        this.criticality.setInnerText( caseImportanceLang.getImportanceName( importanceLevel ) );
//    }

    @Override
    public void setDirection( String value ) {
        this.direction.setInnerText( value );
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
        this.details.setInnerText( value );
    }

    @Override
    public void showFullScreen( boolean value ) {
        this.fullScreen.setVisible( !value );
        if ( value ) {
            this.preview.addStyleName( "col-xs-12 col-lg-6" );
        } else {
            this.preview.setStyleName( "preview" );
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
    Element privateIssue;
    @UiField
    Element header;
    @UiField
    SpanElement creationDate;
    @UiField
    SpanElement direction;
    @UiField
    DivElement state;
    @UiField
    Element iconCriticality;
    @UiField
    SpanElement criticality;
    @UiField
    LabelElement company;
    @UiField
    SpanElement contact;
    @UiField
    LabelElement ourCompany;
    @UiField
    SpanElement headManager;
    @UiField
    SpanElement details;
    @Inject
    @UiField
    Lang lang;
    @UiField
    HTMLPanel commentsContainer;

//    @Inject
//    En_CaseImportanceLang caseImportanceLang;

    @Inject
    En_RegionStateLang stateLang;

    @Inject
    FixedPositioner positioner;

    AbstractProjectPreviewActivity activity;

    interface IssuePreviewViewUiBinder extends UiBinder<HTMLPanel, ProjectPreviewView> {}
    private static IssuePreviewViewUiBinder ourUiBinder = GWT.create( IssuePreviewViewUiBinder.class );
}