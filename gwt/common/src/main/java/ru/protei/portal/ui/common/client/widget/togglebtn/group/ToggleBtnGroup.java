package ru.protei.portal.ui.common.client.widget.togglebtn.group;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import ru.protei.portal.ui.common.client.widget.togglebtn.item.ToggleButton;

/**
 * Вид группы кнопок-переключателей
 */
public class ToggleBtnGroup<T>
        extends ToggleBtnGroupBase<T> implements HasValue<T>{

    @Override
    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler<T> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public void setValue( T value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( T value, boolean fireEvents ) {
        ToggleButton button = modelToItemView.get( value );
        if ( button == null ) {
            return;
        }

        selected = value;
        for ( ToggleButton btn : itemViewToModel.keySet() ) {
            btn.setValue( btn == button );
        }

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, selected );
        }
    }

    @Override
    public T getValue() {
        return selected;
    }

    @Override
    public void onValueChange( ValueChangeEvent< Boolean > event ) {
        ToggleButton source = (ToggleButton) event.getSource();
        T value = itemViewToModel.get( source );
        selected = value;
        source.setValue( true );
        if ( !source.getStyleName().contains( "active" ) ) {
            source.addStyleName( "active" ) ;
        }

        for ( ToggleButton button : itemViewToModel.keySet() ) {
            if ( button != source ) {
                button.setValue( false );
                button.removeStyleName( "active" );
            }
        }

        ValueChangeEvent.fire( this, selected );
    }

    private T selected = null;
}