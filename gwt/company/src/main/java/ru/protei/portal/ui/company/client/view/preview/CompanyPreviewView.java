package ru.protei.portal.ui.company.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.company.client.activity.preview.AbstractCompanyPreviewActivity;
import ru.protei.portal.ui.company.client.activity.preview.AbstractCompanyPreviewView;

/**
 * Представление превью компании
 */
public class CompanyPreviewView extends Composite implements AbstractCompanyPreviewView {

    public CompanyPreviewView() {
        initWidget( ourUiBinder.createAndBindUi ( this ) );
    }

    @Override
    public void setActivity( AbstractCompanyPreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setPhone( String value ) {
        this.phone.setInnerText( value );
    }

    @Override
    public void setSite( String value ) {
        this.site.setInnerText( value );
    }

    @Override
    public void setEmail( String value ) {
        this.email.setInnerText( value );
    }

    @Override
    public void setAddressDejure( String value ) {
        this.addressDejure.setInnerText( value );
    }

    @Override
    public void setAddressFact( String value ) {
        this.addressFact.setInnerText( value );
    }

    @Override
    public void setGroupCompany( String value ) {
        this.groupCompany.setInnerText( value );
    }

    @Override
    public void setInfo( String value ) {
        this.info.setInnerText( value );
    }

    @Override
    public void setGroupVisible( boolean value ) {
//        groupContainer.setVisible( value );
    }

    @UiField
    SpanElement phone;
    @UiField
    SpanElement site;
    @UiField
    SpanElement email;
    @UiField
    SpanElement groupCompany;
    @UiField
    SpanElement addressDejure;
    @UiField
    SpanElement addressFact;
    @UiField
    SpanElement info;
    @UiField
    HTMLPanel groupContainer;

    AbstractCompanyPreviewActivity activity;

    interface CompanyPreviewViewUiBinder extends UiBinder<HTMLPanel, CompanyPreviewView > {}
    private static CompanyPreviewViewUiBinder ourUiBinder = GWT.create( CompanyPreviewViewUiBinder.class );
}