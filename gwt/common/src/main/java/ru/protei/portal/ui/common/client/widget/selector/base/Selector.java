package ru.protei.portal.ui.common.client.widget.selector.base;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.event.HasSelectorChangeValHandlers;
import ru.protei.portal.ui.common.client.widget.selector.event.SelectorChangeValEvent;
import ru.protei.portal.ui.common.client.widget.selector.event.SelectorChangeValHandler;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Базовая логика селектора
 */
public abstract class Selector<T>
        extends Composite
        implements HasValue<T>,
        ClickHandler, ValueChangeHandler<String>,
        Window.ScrollHandler,
        HasSelectorChangeValHandlers,
        HasAddHandlers,
         SelectorWithModel<T>
{

    public Collection<T> getValues() {
        return itemToDisplayOptionModel.keySet();
    }

    private SelectorModel<T> selectorModel;

    public void setSelectorModel( SelectorModel<T> selectorModel ) {
        this.selectorModel = selectorModel;
    }
    public interface SelectorFilter<T> {
        boolean isDisplayed( T value );
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
        SelectorItem itemView = buildItemView(option.getName(), option.getStyle(), itemHandler);
        if ( option.getImageSrc() != null ) {
            itemView.setImage(option.getImageSrc());
        }
        itemView.setIcon(option.getIcon());

        itemViewToModel.put(itemView, value);
        itemToViewModel.put(value, itemView);
        if (value == null) {
            nullItemOption = option;
            nullItemView = itemView;
        } else {
            itemToDisplayOptionModel.put(value, option);
        }



        popup.getChildContainer().add(itemView.asWidget());
    }

    public void clearOptions() {
        popup.getChildContainer().clear();

        itemToViewModel.clear();
        itemToDisplayOptionModel.clear();
        itemViewToModel.clear();

        nullItemOption = null;
        nullItemView = null;
    }

    @Override
    public void onClick(ClickEvent event) {
        T value = itemViewToModel.get(event.getSource());
        if (value == null && !itemViewToModel.containsKey(event.getSource())) {
            return;
        }

        DisplayOption option = value != null ? displayOptionCreator.makeDisplaySelectedOption(value) : nullItemOption;
        selectedOption = value;
        fillSelectorView(option);

        popup.hide();
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

    protected void onSearchChanged( String searchString ) {
        String searchText = searchString.toLowerCase();

        boolean isEmptyResult = true;
        boolean exactMatch = false;

        popup.getChildContainer().clear();

        if (searchText.isEmpty() && nullItemView != null) {
            popup.getChildContainer().add(nullItemView);
        }

        for (Map.Entry<T, DisplayOption> entry : itemToDisplayOptionModel.entrySet()) {
            if ( filter != null && !filter.isDisplayed( entry.getKey() ) ) {
                continue;
            }

            String entryText = entry.getValue().getName().toLowerCase();
            if (searchText.isEmpty() || entryText.contains(searchText)) {
                SelectorItem itemView = itemToViewModel.get(entry.getKey());
                if (itemView != null) {
                    popup.getChildContainer().add(itemView);
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
            addEmptyListGhostOption(lang.errNoMatchesFound());
        }
    }


    @Override
    public void onWindowScroll(Window.ScrollEvent event) {
        if (popup.isAttached()) {
            showPopup(relative);
        }
    }

    @Override
    public HandlerRegistration addSelectorChangeValHandler(SelectorChangeValHandler handler) {
        return addHandler(handler, SelectorChangeValEvent.getType());
    }

    public void addCloseHandler(CloseHandler<PopupPanel> handler) {
        popup.addCloseHandler(handler);
    }

    public void setFilter( SelectorFilter<T> selectorFilter ) {
        filter = selectorFilter;
    }

    public abstract void fillSelectorView(DisplayOption selectedValue);

    @Override
    protected void onLoad() {
        scrollRegistration = Window.addWindowScrollHandler(this);
        if ( selectorModel != null ) {
            selectorModel.onSelectorLoad(this);
        }
    }

    @Override
    protected void onUnload() {
        scrollRegistration.removeHandler();
        if ( selectorModel != null ) {
            selectorModel.onSelectorUnload(this);
        }
    }


    protected void showPopup(IsWidget relative) {
        this.relative = relative;
        popup.showNear(relative);
        showPopup();
    }

    protected void showPopupRight(IsWidget relative) {
        this.relative = relative;
        popup.showNearRight(relative);
        showPopup();
    }

    protected void showPopupInlineRight(IsWidget relative) {
        this.relative = relative;
        popup.showNearInlineRight(relative);
        showPopup();
    }

    protected void reselectValueIfNeeded() {
        T value = getValue();
        if (value != null) {
            setValue(value, false);
        }
    }

    private void showPopup() {
        popup.setSearchVisible(searchEnabled);
        popup.setSearchAutoFocus(searchAutoFocusEnabled);
        popup.setAddButton(addButtonVisible, addButtonText);

        if (popupValueChangeHandlerRegistration != null) {
            popupValueChangeHandlerRegistration.removeHandler();
        }
        popupValueChangeHandlerRegistration = popup.addValueChangeHandler(this);
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
        HTMLPanel panel = (HTMLPanel) popup.getChildContainer();
        SelectorItem firstItem = (SelectorItem) panel.getWidget(0);
        firstItem.setFocus(true);
    }

    private SelectorItem buildItemView(String name, String styleName, KeyUpHandler itemHandler) {
        SelectorItem itemView = itemFactory.get();
        itemView.setName(name);
        itemView.setStyle(styleName);
        itemView.addClickHandler(this);
        itemView.addKeyUpHandler(itemHandler);
        return itemView;
    }

    private void addEmptyListGhostOption(String name) {
        SelectorItem itemView = itemFactory.get();
        itemView.setName(name);
        itemView.addStyleName( UiConstants.Styles.SEARCH_NO_RESULT );
        popup.getChildContainer().add(itemView.asWidget());
    }

    private void onArrowUp(SelectorItem item) {
        HTMLPanel panel = (HTMLPanel) popup.getChildContainer();
        int selectedItemIndex = panel.getWidgetIndex(item);
        if (selectedItemIndex == 0) {
            return;
        }
        SelectorItem previousSelectorItem = (SelectorItem) panel.getWidget(--selectedItemIndex);
        previousSelectorItem.setFocus(true);
    }

    private void onArrowDown(SelectorItem item) {
        HTMLPanel panel = (HTMLPanel) popup.getChildContainer();
        int selectedItemIndex = panel.getWidgetIndex(item);
        if (selectedItemIndex == panel.getWidgetCount() - 1) {
            return;
        }
        SelectorItem nextSelectorItem = (SelectorItem) panel.getWidget(++selectedItemIndex);
        nextSelectorItem.setFocus(true);
    }

    @Inject
    protected SelectorPopup popup;
    @Inject
    Lang lang;

    @Inject
    Provider<SelectorItem> itemFactory;
    protected DisplayOption nullItemOption;

    KeyUpHandler itemHandler = keyUpEvent -> {
        if (keyUpEvent.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
            onArrowDown((SelectorItem) keyUpEvent.getSource());
        }

        if (keyUpEvent.getNativeKeyCode() == KeyCodes.KEY_UP) {
            onArrowUp((SelectorItem) keyUpEvent.getSource());
        }
    };

    protected boolean hasNullValue = true;
    private boolean autoSelectFirst = true;
    private boolean searchEnabled = false;
    private boolean searchAutoFocusEnabled = false;
    private boolean addButtonVisible = false;
    private String addButtonText;
    private IsWidget relative;
    private T selectedOption = null;
    private SelectorItem nullItemView;
    protected DisplayOptionCreator<T> displayOptionCreator;
    private HandlerRegistration popupValueChangeHandlerRegistration;

    private HandlerRegistration scrollRegistration;
    protected Map<SelectorItem, T> itemViewToModel = new HashMap<>();
    protected Map<T, SelectorItem> itemToViewModel = new HashMap<>();

    protected Map<T, DisplayOption> itemToDisplayOptionModel = new HashMap<>();
    protected SelectorFilter<T> filter = null;
}
