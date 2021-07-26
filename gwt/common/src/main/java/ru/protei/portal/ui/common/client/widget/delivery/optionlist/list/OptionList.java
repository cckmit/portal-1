package ru.protei.portal.ui.common.client.widget.delivery.optionlist.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.marker.HasLongId;
import ru.protei.portal.ui.common.client.events.EditEvent;
import ru.protei.portal.ui.common.client.events.EditHandler;
import ru.protei.portal.ui.common.client.events.HasEditHandlers;
import ru.protei.portal.ui.common.client.widget.delivery.optionlist.item.OptionItem;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.length;

/**
 * Список комплектов поставки
 */
public class OptionList<T extends HasLongId>
        extends Composite
        implements HasValue<Set<T>>,
        ValueChangeHandler<Boolean>,
        HasEnabled, SelectorWithModel<T>,
        HasEditHandlers
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
        for ( Map.Entry< T, OptionItem> entry : itemToViewModel.entrySet() ) {
            entry.getValue().setValue( selected.contains( entry.getKey() ) );
        }
        if ( fireEvents ) {
            ValueChangeEvent.fire( this, selected );
        }
    }

    public Collection<T> getValues() {
        return itemViewToModel.values();
    }

    public void setSelectorModel( SelectorModel<T> selectorModel ) {
        this.selectorModel = selectorModel;
    }

    public void addOption( String number, String status, String statusColor, Integer amount, String name, T value ) {
        if ( filter != null && !filter.isDisplayed( value ) ) {
            return;
        }

        OptionItem itemView = itemFactory.get();
        itemView.setNumber( number );
        itemView.setStatusTitle( status );
        itemView.setStatusColor( statusColor );
        itemView.setName( getName(name), name );
        itemView.setAmount(amount);

        itemView.addValueChangeHandler( this );
        itemView.setValue( selected.contains( value ) );
        itemView.setEnabled( isEnabled );

        itemView.addEditHandler( event -> onSelectItem(itemView, value, name) );

        if (isMandatoryOption(value)) {
            makeOptionMandatory(itemView);
            selected.add(value);
        }
        itemViewToModel.put( itemView, value );
        itemToViewModel.put( value, itemView );
        itemToNameModel.put( value, name );
        container.add( itemView.asWidget() );
    }

    private String getName(String name) {
        if (isShortNameEnabled && length(name)>10) {
            return name.substring(0, 10) + "...";
        }
        return name;
    }

    public void addOption( String name, T value ) {
        addOption( null, null, null, null, name, value );
    }

    @Override
    public void fillOptions(List<T> options) {
        clearOptions();
        for (T option : options) {
            addOption(option.toString(), option);
        }
    }

    @Override
    public void refreshValue() {
        setValue(selected);
    }

    @Override
    protected void onLoad() {
        if (selectorModel != null) {
            selectorModel.onSelectorLoad(this);
        }
    }

    @Override
    protected void onUnload() {
        if (selectorModel != null) {
            selectorModel.onSelectorUnload(this);
        }
    }

    public void clearOptions() {
        container.clear();
        selected.clear();
        itemViewToModel.clear();
        itemToViewModel.clear();
        itemToNameModel.clear();
    }

    @Override
    public void onValueChange( ValueChangeEvent< Boolean > event ) {
        T value = itemViewToModel.get( event.getSource() );
        if ( value == null && !itemViewToModel.containsKey( event.getSource() )) {
            return;
        }

        if (event.getValue() == Boolean.FALSE && isMandatoryOption(value)) {
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
    public HandlerRegistration addEditHandler(EditHandler handler) {
        return addHandler(handler, EditEvent.getType());
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

    public void setFilter(Selector.SelectorFilter filter) {
        this.filter = filter;
    }

    public void setEnsureDebugId(T value, String debugId) {
        if (itemToViewModel.containsKey(value)) {
            itemToViewModel.get(value).setEnsureDebugId(debugId);
        }
    }

    public void setMandatoryOptions(T...options) {
        mandatoryOptions = options == null ? new ArrayList<>() : Arrays.asList(options);
        itemViewToModel.entrySet().stream()
                .filter(p -> isMandatoryOption(p.getValue()))
                .map(Map.Entry::getKey)
                .forEach(this::makeOptionMandatory);
        selected.addAll(mandatoryOptions);
    }


    public void setShortNameEnabled(boolean isShortNameEnabled){
        this.isShortNameEnabled = isShortNameEnabled;
    }

    private void onSelectItem(OptionItem itemView, T value, String name) {
        unselectItems();
        itemView.setActive(true);
        EditEvent.fire(OptionList.this, value.getId(), name);
    }

    private void unselectItems() {
        if (isEmpty(itemViewToModel)){
            return;
        }
        itemViewToModel.keySet().forEach(o -> o.setActive(false));
    }

    private void makeOptionMandatory(OptionItem item) {
        item.setEnabled(false);
        item.setValue(true);
    }

    private boolean isMandatoryOption(T option) {
        return mandatoryOptions != null && mandatoryOptions.contains(option);
    }

    protected void makeItemSelected(Long kitId) {
        if (isEmpty(itemToViewModel)){
            return;
        }
        itemToViewModel.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getKey().getId(), kitId))
                .findAny().ifPresent(entry -> entry.getValue().setActive(true));
    }

    @UiField
    FlowPanel container;

    @Inject
    Provider<OptionItem> itemFactory;
    Set<T> selected = new HashSet< T >();

    Map<OptionItem, T> itemViewToModel = new HashMap< OptionItem, T >();
    Map<T, OptionItem> itemToViewModel = new HashMap< T, OptionItem >();
    Map<T, String> itemToNameModel = new HashMap< T, String >();

    private boolean isEnabled = true;
    protected Selector.SelectorFilter<T> filter = null;
    private List<T> mandatoryOptions;
    private SelectorModel<T> selectorModel;
    private boolean isShortNameEnabled;

    interface OptionListUiBinder extends UiBinder< FlowPanel, OptionList > {}
    private static OptionListUiBinder ourUiBinder = GWT.create( OptionListUiBinder.class );

}
