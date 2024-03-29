package ru.protei.portal.ui.account.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.account.client.activity.preview.AbstractAccountPreviewActivity;
import ru.protei.portal.ui.account.client.activity.preview.AbstractAccountPreviewView;
import ru.protei.portal.ui.common.client.lang.Lang;

public class AccountPreviewView
        extends Composite
        implements AbstractAccountPreviewView {

    public AccountPreviewView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractAccountPreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setLogin( String value ) { this.login.setInnerText( value ); }

    @Override
    public void setRoles( String value ) { this.roles.setInnerText( value ); }

    @Override
    public void setPersonInfo( String value ) { this.personInfo .setInnerText( value ); }

    @Override
    public void setTypeImage(String value) { this.typeImage.setSrc( value ); }

    @UiField
    SpanElement login;
    @UiField
    SpanElement roles;
    @UiField
    Element personInfo;
    @UiField
    ImageElement typeImage;

    @Inject
    @UiField
    Lang lang;

    AbstractAccountPreviewActivity activity;

    private static AccountPreviewViewUiBinder ourUiBinder = GWT.create( AccountPreviewViewUiBinder.class );
    interface AccountPreviewViewUiBinder extends UiBinder< HTMLPanel, AccountPreviewView > {}
}