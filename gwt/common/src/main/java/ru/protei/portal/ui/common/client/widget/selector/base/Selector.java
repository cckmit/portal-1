package ru.protei.portal.ui.common.client.widget.selector.base;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.event.*;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.popup.arrowselectable.ArrowSelectableSelectorPopup;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

/**
 * Базовая логика селектора
 */
public abstract class Selector<T>
        extends Composite
        implements HasValue<T>, ValueChangeHandler<String>,
        HasSelectorChangeValHandlers,
        HasAddHandlers,
        SelectorWithModel<T>, SelectorItemSelectHandler {

    public interface SelectorFilter<T> {

        boolean isDisplayed( T value );
    }

    @PostConstruct
    private void onInit() {
        noSearchResult = lang.errNoMatchesFound();
    }

    public Collection<T> getValues() {
        return itemToDisplayOptionModel.keySet();
    }

    public void setSelectorModel( SelectorModel<T> selectorModel ) {
        this.selectorModel = selectorModel;
    }

    public void setValue(T value) {
        setValue(value, false);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        if (value == null && !hasNullValue && !itemToDisplayOptionModel.isEmpty()) {
            value = itemToDisplayOptionModel.entrySet().iterator().next().getKey();
            fireEvents = true;
        }

        selectedOption = value;
        if ( value == null && nullItemOption != null ) {
            fillSelectorView( nullItemOption );
        } else {
            fillSelectorView( displayOptionCreator.makeDisplaySelectedOption( value ) );
        }

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    public T getValue() {
        return selectedOption;
    }

    public void setDisplayOptionCreator( DisplayOptionCreator<T> creator ) {
        this.displayOptionCreator = creator;
    }

    public void refreshValue() {
        setValue(selectedOption);
    }

    public void setAutoSelectFirst(boolean autoSelectFirst) {
        this.autoSelectFirst = autoSelectFirst;
    }

    public void setSearchEnabled(boolean isEnabled) {
        this.searchEnabled = isEnabled;
    }

    public void setSearchAutoFocus(boolean isEnabled) {
        this.searchAutoFocusEnabled = isEnabled;
    }

    public void setHasNullValue(boolean hasNullValue) {
        this.hasNullValue = hasNullValue;
    }

    public void setAddButtonVisible(boolean isVisible) {
        this.addButtonVisible = isVisible;
    }

    public void setAddButtonText(String addButtonText) {
        this.addButtonText = addButtonText;
    }

    @Override
    public void fillOptions( List<T> options ) {
        clearOptions();

        for ( T option : options ) {
            addOption( option );
        }
    }

    public void addOption( T value ) {
        if ( displayOptionCreator == null ) {
            return;
        }

        DisplayOption option = displayOptionCreator.makeDisplayOption( value );
        SelectorItem itemView = buildItemView(option.getName(), option.getStyle());
        if ( option.getImageSrc() != null ) {
            itemView.setImage(option.getImageSrc());
        }
        itemView.setIcon(option.getIcon());

        if (isNotEmpty(option.getTitle())) {
            itemView.setTitle(option.getTitle());
        }

        itemViewToModel.put(itemView, value);
        itemToViewModel.put(value, itemView);
        if (value == null) {
            nullItemOption = option;
            nullItemView = itemView;
        } else {
            itemToDisplayOptionModel.put(value, option);
        }

        popup.addItem(itemView);
    }

    public void clearOptions() {
        popup.clear();
        popup.setNoElements(false, null);

        itemToViewModel.clear();
        itemToDisplayOptionModel.clear();
        itemViewToModel.clear();

        nullItemOption = null;
        nullItemView = null;
    }

    @Override
    public void onSelectorItemSelect(SelectorItemSelectEvent event) {
        T value = itemViewToModel.get(event.getSource());
        if (value == null && !itemViewToModel.containsKey(event.getSource())) {
            return;
        }

        DisplayOption option = value != null ? displayOptionCreator.makeDisplaySelectedOption(value) : nullItemOption;
        selectedOption = value;
        fillSelectorView(option);

        ValueChangeEvent.fire(this, value);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return popup.addAddHandler(handler);
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        onSearchChanged(event.getValue());
    }

    public void setHideNullValue(boolean isHideNullValue) {
        this.isHideNullValue = isHideNullValue;
    }

    protected void onSearchChanged( String searchString ) {
        String searchText = searchString.toLowerCase();

        boolean isEmptyResult = true;
        boolean exactMatch = false;

        popup.clear();

        if (searchText.isEmpty() && nullItemView != null && !isHideNullValue) {
            popup.addItem(nullItemView);
        }

        for (Map.Entry<T, DisplayOption> entry : itemToDisplayOptionModel.entrySet()) {
            if ( filter != null && !filter.isDisplayed( entry.getKey() ) ) {
                continue;
            }

            String entryText = entry.getValue().getName().toLowerCase();
            if (searchText.isEmpty() || entryText.contains(searchText)) {
                SelectorItem itemView = itemToViewModel.get(entry.getKey());
                if (itemView != null) {
                    popup.addItem(itemView);
                }
                if (entryText.equals(searchText))
                    exactMatch = true;

                isEmptyResult = false;
            }
        }

        if (exactMatch) {
            SelectorChangeValEvent.fire(this, null);
        } else {
            SelectorChangeValEvent.fire(this, searchString);
        }

        if (isEmptyResult) {
            addEmptyListGhostOption(noSearchResult);
        }
    }

    @Override
    public HandlerRegistration addSelectorChangeValHandler(SelectorChangeValHandler handler) {
        return addHandler(handler, SelectorChangeValEvent.getType());
    }

    public void setFilter( SelectorFilter<T> selectorFilter ) {
        filter = selectorFilter;
    }

    public abstract void fillSelectorView(DisplayOption selectedValue);

    public void setFixedStrategy(boolean isFixedStrategy) {
        popup.setFixedStrategy(isFixedStrategy);
    }

    @Override
    protected void onLoad() {
        if ( selectorModel != null ) {
            selectorModel.onSelectorLoad(this);
        }
    }

    @Override
    protected void onUnload() {
        if ( selectorModel != null ) {
            selectorModel.onSelectorUnload(this);
        }
    }

    protected void showPopup(IsWidget relative) {
        popup.showNear(relative.asWidget().getElement());
        showPopup();
    }

    protected void reselectValueIfNeeded() {
        T value = getValue();
        if (value != null) {
            setValue(value, false);
        }
    }

    private void showPopup() {
        popup.setSearchHandler(searchEnabled ? this::onSearchChanged : null);

        popup.setSearchAutoFocus(searchAutoFocusEnabled);
        popup.setAddButton(addButtonVisible, addButtonText);

        popup.clearSearchField();
        onSearchChanged("");

        if (!searchEnabled && autoSelectFirst) {
            selectFirstElement();
        }
    }

    protected void closePopup() {
        popup.hide();
    }

    protected void selectFirstElement() {
        popup.focusPopup();
    }

    private SelectorItem buildItemView(String name, String styleName) {
        SelectorItem itemView = itemFactory.get();
        itemView.setName(name);
        itemView.setStyle(styleName);
        itemView.addSelectorItemSelectHandler(this);
        return itemView;
    }

    private void addEmptyListGhostOption(String name) {
        popup.setNoElements(true, name);
    }

    @Inject
    Lang lang;

    @Inject
    Provider<SelectorItem> itemFactory;
    protected DisplayOption nullItemOption;
    protected ArrowSelectableSelectorPopup popup
            = new ArrowSelectableSelectorPopup(KeyCodes.KEY_ENTER, true);

    protected boolean hasNullValue = true;
    private boolean isHideNullValue = false;
    private boolean autoSelectFirst = true;
    private boolean searchEnabled = false;
    private boolean searchAutoFocusEnabled = false;
    private boolean addButtonVisible = false;
    private String addButtonText;
    private T selectedOption = null;
    private SelectorItem nullItemView;
    protected DisplayOptionCreator<T> displayOptionCreator;

    private SelectorModel<T> selectorModel;

    protected Map<SelectorItem, T> itemViewToModel = new HashMap<>();
    protected Map<T, SelectorItem> itemToViewModel = new HashMap<>();

    protected Map<T, DisplayOption> itemToDisplayOptionModel = new HashMap<>();
    protected SelectorFilter<T> filter = null;
    protected String noSearchResult;
}
