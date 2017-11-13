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
import ru.protei.portal.core.model.dict.En_PrivilegeAction;
import ru.protei.portal.core.model.dict.En_PrivilegeEntity;
import ru.protei.portal.ui.common.client.lang.En_PrivilegeActionLang;
import ru.protei.portal.ui.common.client.lang.En_PrivilegeEntityLang;
import ru.protei.portal.ui.common.client.widget.optionlist.base.ModelList;
import ru.protei.portal.ui.common.client.widget.privilege.entity.PrivilegeEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Виджет списка привилегий
 */
public class PrivilegeList 
        extends Composite 
        implements HasValue<Set<En_Privilege>>, ModelList<En_Privilege> {

    @Inject
    public void onInit( PrivilegeModel model ) {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        model.subscribe( this );
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

    @Override
    public void fillOptions( List< En_Privilege > items ) {
        buildWidget( items );
    }

    private void buildWidget( List< En_Privilege > privileges ) {
        container.clear();
        fillActionHeaders();

        for ( En_PrivilegeEntity entity : En_PrivilegeEntity.values() ) {
            PrivilegeEntity entityItem = new PrivilegeEntity();
            entityItem.setHeader( entityLang.getName( entity ) );
            container.add( entityItem.asWidget() );

            for ( En_PrivilegeAction action : En_PrivilegeAction.values() ) {
                En_Privilege privilege = En_Privilege.findPrivilege( entity, action );

                ToggleButton privilegeItem = new ToggleButton();
                privilegeItem.setStyleName( "btn privilege-btn" );

                entityItem.getContainer().add( privilegeItem.asWidget() );
                if ( privilege == null || !privileges.contains( privilege ) ) {
                    privilegeItem.setEnabled( false );
                } else {
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
    }

    private void fillActionHeaders() {
        PrivilegeEntity entityItem = new PrivilegeEntity();
        container.add( entityItem );

        for ( En_PrivilegeAction action : En_PrivilegeAction.values() ) {
            entityItem.getContainer().add( new Label( actionLang.getName( action ) ) );
        }
    }

    private void fillValues() {
        for ( Map.Entry<En_Privilege, ToggleButton> entry : modelToView.entrySet() ) {
            entry.getValue().setValue( values.contains( entry.getKey() ) );
        }
    }

    @UiField
    HTMLPanel container;

    @Inject
    En_PrivilegeEntityLang entityLang;
    @Inject
    En_PrivilegeActionLang actionLang;

    private Set<En_Privilege> values;

    private Map<En_Privilege, ToggleButton> modelToView = new HashMap<>();

    interface PrivilegeListUiBinder extends UiBinder< HTMLPanel, PrivilegeList > {}
    private static PrivilegeListUiBinder ourUiBinder = GWT.create( PrivilegeListUiBinder.class );
}
