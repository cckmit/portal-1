package ru.protei.portal.ui.common.client.widget.togglebtn.group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.widget.selector.base.HasSelectableValues;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.togglebtn.item.ToggleButton;

import java.util.*;

/**
 * Вид группы кнопок-переключателей
 */
public class ToggleBtnGroupBase<T>
        extends Composite implements ValueChangeHandler< Boolean >, HasEnabled, HasSelectableValues<T> {

    public ToggleBtnGroupBase() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public Collection<T> getValues() {
        return modelToItemView.keySet();
    }

    private SelectorModel<T> selectorModel;

    public void setSelectorModel( SelectorModel<T> selectorModel ) {
        this.selectorModel = selectorModel;
    }

    @Override
    protected void onLoad() {
        if ( selectorModel != null ) {
            selectorModel.onSelectorLoad(this);
        }
    }

    public void clearOptions(){
        modelToItemView.clear();
        itemViewToModel.clear();
    }

    @Override
    public void onValueChange( ValueChangeEvent< Boolean > event ) {}

    @Override
    public boolean isEnabled() {
        boolean isEnabled = true;

        for ( ToggleButton button : itemViewToModel.keySet() ) {
            isEnabled &= button.isEnabled();
        }

        return isEnabled;
    }

    @Override
    public void setEnabled( boolean enabled ) {
        itemViewToModel.keySet().forEach( s -> s.setEnabled(enabled) );
    }

    public void addBtn( String caption, T value ) {
        addBtn( caption, value, "btn btn-white" );
    }

    public ToggleButton addBtn( String caption, T value, String buttonStyle ) {
        ToggleButton itemView = itemFactory.get();
        if ( caption != null ) {
            itemView.setText( caption );
        }
        if ( buttonStyle != null ) {
            itemView.setStyleName( buttonStyle );
        }

        itemView.addValueChangeHandler( this );
        root.add( itemView.asWidget() );

        itemViewToModel.put( itemView, value );
        modelToItemView.put( value, itemView );

        return itemView;
    }

    public void addBtnWithIcon( String iconStyle, String buttonStyle, String caption, T value ) {
        ToggleButton itemView = addBtn( caption, value, buttonStyle );
        if ( iconStyle != null ) {
            itemView.setIcon( iconStyle, false );
        }
    }

    public void addBtnWithImage( String imageSrc, String buttonStyle, String caption, T value, String title ) {
        ToggleButton itemView = addBtn( caption, value, buttonStyle );
        itemView.setTitle( title );
        if ( imageSrc != null ) {
            itemView.setImageSrc( imageSrc );
        }
    }

    public void clear() {
        itemViewToModel.clear();
        modelToItemView.clear();
        root.clear();
    }

    public void setEnsureDebugId(T value, String debugId) {
        if (modelToItemView.containsKey(value)) {
            modelToItemView.get(value).setEnsureDebugId(debugId);
        }
    }

    @UiField
    HTMLPanel root;

    @Inject
    Provider< ToggleButton > itemFactory;

    public Map<ToggleButton, T> itemViewToModel = new HashMap<>();
    Map<T, ToggleButton> modelToItemView = new HashMap<>();

    interface ToggleButtonUiBinder extends UiBinder<HTMLPanel, ToggleBtnGroupBase > {}
    private static ToggleButtonUiBinder ourUiBinder = GWT.create( ToggleButtonUiBinder.class );
}