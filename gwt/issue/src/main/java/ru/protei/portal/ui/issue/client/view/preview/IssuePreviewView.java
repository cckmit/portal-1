package ru.protei.portal.ui.issue.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewActivity;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewView;

/**
 * Created by turik on 11.11.16.
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
    public void setCreationDate( String value ) {
        this.creationDate.setInnerText( value );
    }

    @Override
    public void setNumber( String value ) {
        this.number.setInnerText( value );
    }

    @Override
    public void setState( String value ) {
        this.state.setInnerText( value );
    }

    @Override
    public void setCriticality( String value ) {
        this.criticality.setInnerText( value );
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
    public void setManager( String value ) {
        this.manager.setInnerText( value );
    }

    @Override
    public void setInfo( String value ) {
        this.info.setInnerText( value );
    }

    @Override
    public HTMLPanel preview() {
        return preview;
    }

    @Override
    public HasVisibility fullScreen() {
        return fullScreen;
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
    SpanElement number;
    @UiField
    SpanElement creationDate;
    @UiField
    SpanElement product;
    @UiField
    SpanElement state;
    @UiField
    SpanElement criticality;
    @UiField
    SpanElement company;
    @UiField
    SpanElement contact;
    @UiField
    SpanElement manager;

    @Inject
    @UiField
    Lang lang;
    @UiField
    SpanElement info;

    AbstractIssuePreviewActivity activity;

    interface IssuePreviewViewUiBinder extends UiBinder<HTMLPanel, IssuePreviewView> {}
    private static IssuePreviewViewUiBinder ourUiBinder = GWT.create( IssuePreviewViewUiBinder.class );
}