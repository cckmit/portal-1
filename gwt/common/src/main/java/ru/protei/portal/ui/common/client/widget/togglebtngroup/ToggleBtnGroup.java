package ru.protei.portal.ui.common.client.widget.togglebtngroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Вид группы кнопок-переключателей
 */
public class ToggleBtnGroup< T >
        extends Composite implements HasValue< Set < T > >, ValueChangeHandler< Boolean > {

    public ToggleBtnGroup() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
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
        selected = values;
        if ( selected == null ) {
            selected = new HashSet<T>();
        }

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

    public void addBtn( String text, T value ) {
        ToggleButton itemView = itemFactory.get();
        if ( text != null ) {
            itemView.setText( text );
        }
        itemView.setStyleName( "button whiteC switcher" );
        itemView.addValueChangeHandler( this );
        root.add( itemView.asWidget() );

        itemViewToModel.put( itemView, value );
    }

    public void clear() {
        itemViewToModel.clear();
        root.clear();
    }

    @UiField
    HTMLPanel root;

    @Inject
    Provider< ToggleButton > itemFactory;

    Map< ToggleButton, T > itemViewToModel = new HashMap< ToggleButton, T >();

    private Set< T > selected;

    interface ToggleButtonUiBinder extends UiBinder<HTMLPanel, ToggleBtnGroup> {}
    private static ToggleButtonUiBinder ourUiBinder = GWT.create( ToggleButtonUiBinder.class );
}