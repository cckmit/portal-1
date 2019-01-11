package ru.protei.portal.ui.common.client.widget.togglebtn.group;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import ru.protei.portal.ui.common.client.widget.togglebtn.item.ToggleButton;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Вид группы кнопок-переключателей
 */
public class ToggleBtnGroupMulti<T>
        extends ToggleBtnGroupBase<T> implements HasValue<Set<T>>{

    public ToggleBtnGroupMulti() {
        selected = new HashSet<>();
    }

    @Override
    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler< Set< T > > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public void setValue( Set< T > values ) {
        setValue( values, false );
    }

    @Override
    public void setValue( Set< T > values, boolean fireEvents ) {
        selected = values == null ? new HashSet<>() : values;

        for (Map.Entry<ToggleButton, T> entry : itemViewToModel.entrySet()) {
            entry.getKey().setValue(selected.contains(entry.getValue()));
        }

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, selected );
        }
    }

    @Override
    public Set< T > getValue() {
        return selected;
    }

    public void refreshValue() { setValue( selected ); }

    @Override
    public void onValueChange( ValueChangeEvent< Boolean > event ) {
        ToggleButton source = ( ToggleButton ) event.getSource();
        T value = itemViewToModel.get( source );
        if ( source.getValue() ) {
            source.addStyleName( "active" );
            selected.add( value );
        } else {
            source.removeStyleName( "active" );
            selected.remove( value );
        }

        ValueChangeEvent.fire( this, selected );
    }

    protected Set<T> selected;
}