package ru.protei.portal.ui.employee.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
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
        this.name.setTitle( name );
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
        emailContainer.setVisible(!value.isEmpty());
        emails.setInnerHTML(value);
    }

    @Override
    public void setDepartmentParent(String value) {
        departmentParentContainer.setVisible( value != null && !value.isEmpty() );
        departmentParent.setInnerText( value == null ? "" : value );
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
    public void setIP(String value) {
        ipContainer.setVisible( value != null && !value.isEmpty() );
        ip.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setFireDate(String value) {
        employeeContainer.addClassName("fired");
        name.setInnerHTML("<i class='fa fa-ban text-danger'></i> " + this.name.getInnerText());
        fireDateContainer.setVisible(true);
        fireDate.setInnerText( value == null ? "" : value );
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
    HTMLPanel fireDateContainer;

    @UiField
    HTMLPanel phoneContainer;

    @UiField
    HTMLPanel emailContainer;

    @UiField
    HTMLPanel departmentParentContainer;

    @UiField
    HTMLPanel departmentContainer;

    @UiField
    HTMLPanel positionContainer;

    @UiField
    HTMLPanel ipContainer;

    @UiField
    SpanElement birthday;

    @UiField
    SpanElement fireDate;

    @UiField
    SpanElement phone;

    @UiField
    Image photo;

    @UiField
    SpanElement department;

    @UiField
    SpanElement departmentParent;

    @UiField
    SpanElement position;

    @UiField
    SpanElement ip;

    @UiField
    HTMLPanel previewContainer;

    @UiField
    DivElement employeeContainer;

    @UiField
    Anchor preview;

    @UiField
    SpanElement emails;


    AbstractEmployeeItemActivity activity;

    private static EmployeeItemViewUiBinder ourUiBinder = GWT.create( EmployeeItemViewUiBinder.class );
    interface EmployeeItemViewUiBinder extends UiBinder< HTMLPanel, EmployeeItemView > {}
}