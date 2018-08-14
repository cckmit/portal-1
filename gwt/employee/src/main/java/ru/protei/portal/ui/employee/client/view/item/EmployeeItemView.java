package ru.protei.portal.ui.employee.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
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
    Image photo;

    @UiField
    Anchor edit;

    @UiField
    Anchor favorite;

    @UiField
    HTMLPanel previewContainer;

    @UiField
    Anchor preview;

    AbstractEmployeeItemActivity activity;

    private static EmployeeItemViewUiBinder ourUiBinder = GWT.create( EmployeeItemViewUiBinder.class );
    interface EmployeeItemViewUiBinder extends UiBinder< HTMLPanel, EmployeeItemView > {}
}