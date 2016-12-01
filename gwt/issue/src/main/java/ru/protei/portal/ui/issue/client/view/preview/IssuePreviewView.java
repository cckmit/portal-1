package ru.protei.portal.ui.issue.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.common.client.common.CriticalityStyleBuilder;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewActivity;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewView;

/**
 * Вид превью обращения
 */
public class IssuePreviewView extends Composite implements AbstractIssuePreviewView {

    public IssuePreviewView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractIssuePreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setPrivateIssue( boolean privateIssue ) {
        if ( privateIssue ) {
            this.privateIssue.removeClassName( "fa-unlock-alt" );
            this.privateIssue.addClassName( "fa-lock" );
        } else {
            this.privateIssue.removeClassName( "fa-lock" );
            this.privateIssue.addClassName( "fa-unlock-alt" );
        }
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
    public void setState( long value ) {
        En_CaseState caseState = En_CaseState.getById( value );
        this.state.setClassName( "small label label-" + En_CaseState.getById( value ).toString() );
        this.state.setInnerText( caseStateLang.getStateName( caseState ) );
    }

    @Override
    public void setCriticality( int value ) {
        En_ImportanceLevel importanceLevel = En_ImportanceLevel.find( value );
        this.iconCriticality.setClassName( "importance importance-lg none-vertical-align " + importanceLevel.toString() );
        CriticalityStyleBuilder.make().addClassName( this.iconCriticality, En_ImportanceLevel.find( value ) );
        this.criticality.setInnerText( caseImportanceLang.getImportanceName( importanceLevel ) );
    }

    @Override
    public void setProduct( String value ) {
        this.product.setInnerText( value );
    }

    @Override
    public void setCompany( String value ) {
        this.company.setInnerText( value );
    }

    @Override
    public void setContact( String value ) {
        this.contact.setInnerText( value );
    }

    @Override
    public void setOurCompany( String value ) {
        this.ourCompany.setInnerText( value );
    }

    @Override
    public void setManager( String value ) {
        this.manager.setInnerText( value );
    }

    @Override
    public void setInfo( String value ) {
        this.info.setInnerText( value );
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

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

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
    SpanElement product;
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
    SpanElement manager;
    @UiField
    SpanElement info;
    @Inject
    @UiField
    Lang lang;
    @UiField
    HTMLPanel commentsContainer;

    @Inject
    En_CaseImportanceLang caseImportanceLang;

    @Inject
    En_CaseStateLang caseStateLang;

    AbstractIssuePreviewActivity activity;

    interface IssuePreviewViewUiBinder extends UiBinder<HTMLPanel, IssuePreviewView> {}
    private static IssuePreviewViewUiBinder ourUiBinder = GWT.create( IssuePreviewViewUiBinder.class );
}