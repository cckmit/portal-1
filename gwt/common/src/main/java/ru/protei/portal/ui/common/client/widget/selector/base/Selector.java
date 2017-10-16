package ru.protei.portal.ui.common.client.widget.selector.base;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.event.HasSelectorChangeValHandlers;
import ru.protei.portal.ui.common.client.widget.selector.event.SelectorChangeValEvent;
import ru.protei.portal.ui.common.client.widget.selector.event.SelectorChangeValHandler;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовая логика селектора
 */
public abstract class Selector<T>
        extends Composite
        implements HasValue<T>,
        ClickHandler, ValueChangeHandler<String>,
        Window.ScrollHandler,
        HasSelectorChangeValHandlers {

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
        } else if ( !itemToDisplayOptionModel.containsKey(value) && displayOptionCreator != null ) {
            fillSelectorView( displayOptionCreator.makeDisplayOption( value ) );
        } else {
            fillSelectorView( itemToDisplayOptionModel.get(value) );
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

    public void setSearchEnabled(boolean isEnabled) {
        this.searchEnabled = isEnabled;
    }

    public void setSearchAutoFocus(boolean isEnabled) {
        this.searchAutoFocusEnabled = isEnabled;
    }

    public void setHasNullValue(boolean hasNullValue) {
        this.hasNullValue = hasNullValue;
    }

     public void addOption( T value ) {
        if ( displayOptionCreator == null ) {
            return;
        }

        DisplayOption option = displayOptionCreator.makeDisplayOption( value );
        SelectorItem itemView = buildItemView(option.getName(),
                option.getStyle(), itemHandler);
        itemView.setImage(option.getImageSrc());
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

        DisplayOption option = value != null ? itemToDisplayOptionModel.get(value) : nullItemOption;
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
    public void onValueChange(ValueChangeEvent<String> event) {
        String searchText = event.getValue().toLowerCase();

        boolean isEmptyResult = true;
        boolean exactMatch = false;

        popup.getChildContainer().clear();

        if (searchText.isEmpty() && nullItemView != null) {
            popup.getChildContainer().add(nullItemView);
        }

        for (Map.Entry<T, DisplayOption> entry : itemToDisplayOptionModel.entrySet()) {
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
            SelectorChangeValEvent.fire(this, event.getValue());
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

    public abstract void fillSelectorView(DisplayOption selectedValue);

    @Override
    protected void onLoad() {
        scrollRegistration = Window.addWindowScrollHandler(this);
    }

    @Override
    protected void onUnload() {
        scrollRegistration.removeHandler();
    }

    protected void showPopup(IsWidget relative) {
        this.relative = relative;
        popup.setSearchVisible(searchEnabled);
        popup.setSearchAutoFocus(searchAutoFocusEnabled);

        popup.showNear(relative);
        popup.addValueChangeHandler(this);
        popup.clearSearchField();

        if (!searchEnabled) {
            selectFirstElement();
        }
    }

    protected void closePopup() {
        popup.hide();
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
        itemView.addStyleName("search-no-result");
        popup.getChildContainer().add(itemView.asWidget());
    }

    private void selectFirstElement() {
        HTMLPanel panel = (HTMLPanel) popup.getChildContainer();
        SelectorItem firstItem = (SelectorItem) panel.getWidget(0);
        firstItem.setFocus(true);
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
    SelectorPopup popup;
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
    private boolean searchEnabled = false;
    private boolean searchAutoFocusEnabled = false;
    private IsWidget relative;
    private T selectedOption = null;
    private SelectorItem nullItemView;
    private DisplayOptionCreator<T> displayOptionCreator;

    private HandlerRegistration scrollRegistration;
    protected Map<SelectorItem, T> itemViewToModel = new HashMap<>();
    protected Map<T, SelectorItem> itemToViewModel = new HashMap<>();

    protected Map<T, DisplayOption> itemToDisplayOptionModel = new HashMap<>();
}
