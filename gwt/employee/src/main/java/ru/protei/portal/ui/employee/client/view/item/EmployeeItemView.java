package ru.protei.portal.ui.employee.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemView;

/**
 * Представление сотрудника
 */
public class EmployeeItemView extends Composite implements AbstractEmployeeItemView {

    public EmployeeItemView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    public void setActivity( AbstractEmployeeItemActivity activity ) {
        this.activity = activity;
    }

    @UiHandler( "preview" )
    public void onPreviewClicked ( ClickEvent event )
    {
        event.preventDefault();
        if ( activity != null ) {
            activity.onPreviewClicked( this );
        }
    }

    @Override
    public void setName( String name ) {
        this.name.setInnerText( name );
    }

    @Override
    public void setBirthday( String value ) {
        birthdayContainer.setVisible( value != null && !value.isEmpty() );
        birthday.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setPhone( String value ) {
        phoneContainer.setVisible( value != null && !value.isEmpty() );
        phone.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setEmail( String value ) {
        emailContainer.setVisible( value != null && !value.isEmpty() );
        email.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setCompany( String value ) {
        companyContainer.setVisible( value != null && !value.isEmpty() );
        company.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setDepartment( String value ) {
        departmentContainer.setVisible( value != null && !value.isEmpty() );
        department.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setPosition( String value ) {
        positionContainer.setVisible( value != null && !value.isEmpty() );
        position.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setPhoto( String url ) {
        photo.setUrl( url );
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @UiField
    HeadingElement name;

    @UiField
    HTMLPanel birthdayContainer;

    @UiField
    HTMLPanel phoneContainer;

    @UiField
    HTMLPanel emailContainer;

    @UiField
    HTMLPanel companyContainer;

    @UiField
    HTMLPanel departmentContainer;

    @UiField
    HTMLPanel positionContainer;

    @UiField
    SpanElement birthday;

    @UiField
    SpanElement phone;

    @UiField
    AnchorElement email;

    @UiField
    Image photo;

    @UiField
    SpanElement company;

    @UiField
    SpanElement department;

    @UiField
    SpanElement position;

    @UiField
    HTMLPanel previewContainer;

    @UiField
    Anchor preview;

    AbstractEmployeeItemActivity activity;

    private static EmployeeItemViewUiBinder ourUiBinder = GWT.create( EmployeeItemViewUiBinder.class );
    interface EmployeeItemViewUiBinder extends UiBinder< HTMLPanel, EmployeeItemView > {}
}