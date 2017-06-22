package ru.protei.portal.ui.account.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.account.client.activity.preview.AbstractAccountPreviewActivity;
import ru.protei.portal.ui.account.client.activity.preview.AbstractAccountPreviewView;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;

public class AccountPreviewView extends Composite implements AbstractAccountPreviewView {
    public AccountPreviewView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch( this, FixedPositioner.NAVBAR_TOP_OFFSET );
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore( this );
    }

    @Override
    public void setActivity( AbstractAccountPreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setLogin( String value ) { this.login.setInnerHTML( value ); }

    @Override
    public void setLastName( String value ) { this.lastName.setInnerHTML( value ); }

    @Override
    public void setFirstName( String value ) { this.firstName.setInnerHTML( value ); }

    @Override
    public void setSecondName( String value ) { this.secondName.setInnerHTML( value ); }

    @Override
    public void setCompany ( String value ) { this.company.setInnerHTML( value ); }

    @Override
    public void setRoles( String value ) { this.roles.setInnerHTML( value ); }

    @UiField
    SpanElement login;

    @UiField
    SpanElement lastName;

    @UiField
    SpanElement firstName;

    @UiField
    SpanElement secondName;

    @UiField
    SpanElement company;

    @UiField
    SpanElement roles;

    @Inject
    @UiField
    Lang lang;

    @Inject
    FixedPositioner positioner;

    AbstractAccountPreviewActivity activity;

    private static AccountPreviewViewUiBinder ourUiBinder = GWT.create( AccountPreviewViewUiBinder.class );
    interface AccountPreviewViewUiBinder extends UiBinder< HTMLPanel, AccountPreviewView > {}
}