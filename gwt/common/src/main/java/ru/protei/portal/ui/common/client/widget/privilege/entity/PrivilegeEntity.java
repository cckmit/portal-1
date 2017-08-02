package ru.protei.portal.ui.common.client.widget.privilege.entity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * Виджет категории привилегий
 */
public class PrivilegeEntity
        extends Composite {

    public PrivilegeEntity() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    public void setHeader( String value ) {
        this.header.setInnerText( value );
    }

    public HasWidgets getContainer() {
        return container;
    }

    @UiField
    LabelElement header;
    @UiField
    HTMLPanel container;

    interface PrivilegeListUiBinder extends UiBinder< HTMLPanel, PrivilegeEntity > {}
    private static PrivilegeListUiBinder ourUiBinder = GWT.create( PrivilegeListUiBinder.class );
}
