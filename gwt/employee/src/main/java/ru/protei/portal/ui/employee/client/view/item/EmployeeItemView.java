package ru.protei.portal.ui.employee.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemView;

import java.util.List;

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
    public void setEmail( List<String> list ) {
        boolean isEmpty = CollectionUtils.isEmpty( list );
        emailContainer.setVisible( !isEmpty );
        emails.removeAllChildren();
        if ( isEmpty ) return;
        list.forEach( value -> emails.appendChild( buildAnchorElement( value ) ) );
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

    private Element buildAnchorElement( String value ){
        AnchorElement anchor = DOM.createAnchor().cast();
        anchor.setInnerText( value );
        anchor.setHref( "mailto:" + value );
        return anchor;
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
    HTMLPanel departmentContainer;

    @UiField
    HTMLPanel positionContainer;

    @UiField
    SpanElement birthday;

    @UiField
    SpanElement phone;

    @UiField
    Image photo;

    @UiField
    SpanElement department;

    @UiField
    SpanElement position;

    @UiField
    HTMLPanel previewContainer;

    @UiField
    Anchor preview;

    @UiField
    SpanElement emails;


    AbstractEmployeeItemActivity activity;

    private static EmployeeItemViewUiBinder ourUiBinder = GWT.create( EmployeeItemViewUiBinder.class );
    interface EmployeeItemViewUiBinder extends UiBinder< HTMLPanel, EmployeeItemView > {}
}