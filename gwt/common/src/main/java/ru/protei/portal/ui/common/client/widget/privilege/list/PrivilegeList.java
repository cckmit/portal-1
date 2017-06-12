package ru.protei.portal.ui.common.client.widget.privilege.list;

import com.google.gwt.core.client.GWT;
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
import ru.protei.portal.ui.common.client.widget.privilege.category.PrivilegeCategory;
import ru.protei.portal.ui.common.client.widget.privilege.privilege.Privilege;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Виджет списка привилегий
 */
public class PrivilegeList 
        extends Composite 
        implements HasValue<Set<En_Privilege>> {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        fillPrivileges();
    }

    @Override
    public Set<En_Privilege> getValue() {
        return values;
    }

    @Override
    public void setValue( Set<En_Privilege> values ) {
        setValue( values, false );
    }

    @Override
    public void setValue( Set<En_Privilege> values, boolean fireEvent ) {
        this.values = values;
        fillValues();
        if ( fireEvent ) {
            ValueChangeEvent.fire( this, values );
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<Set<En_Privilege>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    private void fillPrivileges() {
        for ( En_PrivilegeCategory category : En_PrivilegeCategory.values() ) {
            PrivilegeCategory categoryItem = new PrivilegeCategory();
            categoryItem.setHeader( categoryLang.getName( category ) );
            container.add( categoryItem.asWidget() );
            for ( En_Privilege privilege : En_PrivilegeCategory.getPrivileges( category ) ) {
                Privilege privilegeItem = new Privilege();
                privilegeItem.setHeader( privilegeLang.getName( privilege ) );
                categoryItem.getContainer().add( privilegeItem.asWidget() );
                modelToView.put( privilege, privilegeItem );

                privilegeItem.addValueChangeHandler( valueChangeEvent -> {
                    if ( valueChangeEvent.getValue() ) {
                        values.add( privilege );
                    } else {
                        values.remove( privilege );
                    }
                } );
            }
        }
    }

    private void fillValues() {
        for ( Map.Entry<En_Privilege, Privilege> entry : modelToView.entrySet() ) {
            entry.getValue().setValue( values.contains( entry.getKey() ) );
        }
    }

    @UiField
    HTMLPanel container;

    @Inject
    En_PrivilegeLang privilegeLang;
    @Inject
    En_PrivilegeCategoryLang categoryLang;

    private Set<En_Privilege> values;

    private Map<En_Privilege, Privilege> modelToView = new HashMap<>();

    interface PrivilegeListUiBinder extends UiBinder< HTMLPanel, PrivilegeList > {}
    private static PrivilegeListUiBinder ourUiBinder = GWT.create( PrivilegeListUiBinder.class );
}
