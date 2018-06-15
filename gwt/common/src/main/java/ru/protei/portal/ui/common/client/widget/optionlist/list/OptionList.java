package ru.protei.portal.ui.common.client.widget.optionlist.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.widget.optionlist.item.OptionItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Список чекбоксов с заголовом
 */
public class OptionList<T>
        extends Composite
        implements HasValue<Set<T>>, ValueChangeHandler<Boolean>, HasEnabled
{
    public OptionList() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public Set< T > getValue() {
        return selected;
    }

    @Override
    public void setValue( Set< T > value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( Set< T > value, boolean fireEvents ) {
        selected = value == null ? new HashSet< T >() : value;
        for ( Map.Entry< T, OptionItem > entry : itemToViewModel.entrySet() ) {
            entry.getValue().setValue( selected.contains( entry.getKey() ) );
        }
        if ( fireEvents ) {
            ValueChangeEvent.fire( this, selected );
        }
    }

    public void setHeader( String header ) {
        this.header.setInnerText( header == null ? "" : header );
    }


    public void addOption( String name, T value, String styleName ) {
        OptionItem itemView = itemFactory.get();
        itemView.setName( name );
        itemView.addValueChangeHandler( this );
        itemView.setEnabled(isEnabled);
        itemViewToModel.put( itemView, value );
        itemToViewModel.put( value, itemView );
        itemToNameModel.put( value, name );
        if ( styleName != null ) {
            itemView.setStyleName( styleName );
        }
        container.add( itemView.asWidget() );
    }

    public void addOption( String name, T value ) {
        addOption( name, value, null );
    }

    public void clearOptions() {
        container.clear();
    }

    public void reset() {
        clearOptions();
        selected.clear();
        itemViewToModel.clear();
        itemToViewModel.clear();
        itemToNameModel.clear();
    }

    @Override
    public void onValueChange( ValueChangeEvent< Boolean > event ) {
        T value = itemViewToModel.get( event.getSource() );
        if ( value == null && !itemViewToModel.containsKey( event.getSource() ) ) {
            return;
        }

        if ( event.getValue() ) {
            selected.add( value );
        } else {
            selected.remove( value );
        }

        ValueChangeEvent.fire( this, selected );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< Set<T> > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        itemViewToModel.forEach((k, v) -> k.setEnabled(isEnabled));
    }

    @UiField
    FlowPanel container;
    @UiField
    LabelElement header;

    @Inject
    Provider<OptionItem> itemFactory;
    Set<T> selected = new HashSet< T >();

    Map<OptionItem, T> itemViewToModel = new HashMap< OptionItem, T >();
    Map<T, OptionItem> itemToViewModel = new HashMap< T, OptionItem >();
    Map<T, String> itemToNameModel = new HashMap< T, String >();

    private boolean isEnabled = true;

    interface OptionListUiBinder extends UiBinder< HTMLPanel, OptionList > {}
    private static OptionListUiBinder ourUiBinder = GWT.create( OptionListUiBinder.class );

}