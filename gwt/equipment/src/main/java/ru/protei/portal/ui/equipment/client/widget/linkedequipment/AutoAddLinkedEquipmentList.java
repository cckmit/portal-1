package ru.protei.portal.ui.equipment.client.widget.linkedequipment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Equipment;
import java.util.Set;

/**
 * Виджет связанных устройств
 */
public class AutoAddLinkedEquipmentList extends Composite
        implements HasValue<Set<Equipment>> {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        addItem();
    }

    @Override
    public Set<Equipment> getValue() {
        return null;
    }

    @Override
    public void setValue( Set<Equipment> equipments ) {
        setValue( equipments, false );
    }

    @Override
    public void setValue( Set<Equipment> equipments, boolean fireEvents ) {
        this.value = equipments;
        if ( value == null ) {

        }
        for ( Equipment eq : equipments ) {
            addItem();
        }
        if ( fireEvents ) {
            ValueChangeEvent.fire( this, equipments );
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<Set<Equipment>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    private void addItem(){
    }

    @UiField
    HTMLPanel root;

    private Set<Equipment> value;

    interface AutocompleteLinkedEquipmentListUiBinder extends UiBinder<HTMLPanel, AutoAddLinkedEquipmentList> {}
    private static AutocompleteLinkedEquipmentListUiBinder ourUiBinder = GWT.create( AutocompleteLinkedEquipmentListUiBinder.class );
}