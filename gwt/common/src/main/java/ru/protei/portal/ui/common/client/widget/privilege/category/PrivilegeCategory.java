package ru.protei.portal.ui.common.client.widget.privilege.category;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_PrivilegeCategory;
import ru.protei.portal.ui.common.client.lang.En_PrivilegeCategoryLang;
import ru.protei.portal.ui.common.client.lang.En_PrivilegeLang;

import java.util.Set;

/**
 * Виджет категории привилегий
 */
public class PrivilegeCategory
        extends Composite {

    public PrivilegeCategory() {
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

    interface PrivilegeListUiBinder extends UiBinder< HTMLPanel, PrivilegeCategory> {}
    private static PrivilegeListUiBinder ourUiBinder = GWT.create( PrivilegeListUiBinder.class );
}
