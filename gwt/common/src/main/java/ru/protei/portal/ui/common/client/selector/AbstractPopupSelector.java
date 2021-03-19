package ru.protei.portal.ui.common.client.selector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.selector.pageable.*;
import ru.protei.portal.ui.common.client.selector.popup.item.SelectorItemHandler;
import ru.protei.portal.ui.common.client.selector.util.ValueChangeButton;
import ru.protei.portal.ui.common.client.widget.selector.popup.PopupHandler;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopupWithSearch;
import ru.protei.portal.ui.common.client.widget.selector.popup.arrowselectable.ArrowSelectableSelectorPopup;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.ui.common.client.selector.util.ValueChangeButton.getValueChangeButton;
import static ru.protei.portal.ui.common.client.selector.util.ValueChangeButton.isValueChangeButton;

/**
 * Селектор c выпадающим списком
 */
public abstract class AbstractPopupSelector<T> extends Composite
        implements Selector<T>,
        ItemsContainer<T>,
        PopupHandler,
        HasEnabled, HasVisibility,
        SelectorItemHandler<T>,
        LoadingHandler, SearchHandler {

    @Override
    public void onLoadingStart() {
        getPopup().showLoading(true);
        getPopup().setNoElements(false, null);
    }

    @Override
    public void onLoadingComplete() {
        getPopup().showLoading(false);
        if (!isAttached()) return;
        getSelector().fillContinue(this);
        checkNoElements();
    }

    @Override
    public void onSelectorItemClicked(SelectorItem<T> selectorItem) {
        onSelectorItemSelected(selectorItem);
    }

    private void onSelectorItemSelected(SelectorItem<T> selectorItem) {
        T value = selectorItem.getValue();
        getSelector().getSelection().select(value);

        onSelectionChanged();
    }

    @Override
    public void onKeyboardButtonDown( SelectorItem<T> selectorItem, KeyDownEvent event ) {
        if (!isValueChangeButton(event.getNativeKeyCode())) {
            return;
        }

        ValueChangeButton notUsed = getValueChangeButton( event.getNativeKeyCode() ); //

        onSelectorItemSelected(selectorItem);

        if (isAutoCloseable) {
            popup.hide();
        } else {
            popup.refreshPopup();
        }
    }

    @Override
    public void onMouseClickEvent( SelectorItem<T> selectorItem, ClickEvent event ) {
        onSelectorItemSelected(selectorItem);

        if (isAutoCloseable) {
            popup.hide();
        } else {
            popup.refreshPopup();
            popup.focusPopup();
        }
    }

    @Override
    public void onPopupHide(SelectorPopup selectorPopup) {
        clearPopupItems();
        getPopup().setNoElements(false, null);
        if (popupUnloadHandler != null) popupUnloadHandler.run();
    }

    @Override
    public void onUnload() {
        clearSelector();
    }

    @Override
    public void onEndOfScroll() {
        getSelector().fillContinue(this);
    }

    @Override
    public void fill( T element, String elementHtml) {
        SelectorItem<T> itemView = makeItemView(element, elementHtml);
        getPopup().getContainer().add(itemView.asWidget());
    }

    @Override
    public void onSearch(String searchString) {
        searchHandler.onSearch(searchString);
    }

    @Override
    public void setItemRenderer( SelectorItemRenderer<T> selectorItemRenderer) {
        getSelector().setItemRenderer(selectorItemRenderer);
    }

    @Override
    public void setModel( SelectorModel<T> selectorModel) {
        this.getSelector().setModel(selectorModel);
    }

    public void setAsyncModel( final AsyncSelectorModel<T> selectorModel) {
        getSelector().setModel((SelectorModel<T>) elementIndex -> selectorModel.get(elementIndex, AbstractPopupSelector.this));
    }

    public void clearSelector(){
        clearPopupItems();
        if (getPopup() instanceof SelectorPopupWithSearch) {
            ((SelectorPopupWithSearch) getPopup()).clearSearchField();
            getSelector().setSearchString(null);
        }
    }

    public void setAsyncSearchModel( final AsyncSearchSelectorModel<T> selectorModel) {
        getSelector().setModel((SelectorModel<T>) elementIndex -> selectorModel.get(elementIndex, AbstractPopupSelector.this));
        searchHandler = searchString -> {
            clearPopupItems();
            selectorModel.setSearchString(searchString);
            getSelector().fillFromBegin(AbstractPopupSelector.this);
            checkNoElements();
        };
    }

    public void setPageSize(int pageSize) {
        getSelector().setPageSize(pageSize);
    }

    public boolean isSelected(T element){
        return getSelector().getSelection().isSelected( element );
    }

    public void setSearchAutoFocus(boolean isSearchAutoFocus){
        if(getPopup() instanceof SelectorPopupWithSearch) ((SelectorPopupWithSearch) getPopup()).setSearchAutoFocus(isSearchAutoFocus);
    }

    public void setHasNullValue(boolean hasNullValue) {
        getSelector().setHasNullValue(hasNullValue);
    }

    public boolean hasNullValue() {
        return getSelector().hasNullValue();
    }

    public void setHideSelectedFromChose(boolean hideSelectedFromChose) {
        getSelector().setHideSelectedFromChose(hideSelectedFromChose);
    }

    public void setEmptyListText(String emptyListText) {
        this.emptyListText = emptyListText;
    }

    public void setEmptySearchText(String emptySearchText) {
        this.emptySearchText = emptySearchText;
    }

    public void setFilter( ru.protei.portal.ui.common.client.widget.selector.base.Selector.SelectorFilter<T> selectorFilter) {
        getSelector().setFilter(selectorFilter);
    }

    public HandlerRegistration addAddHandler( AddHandler addhandler ) {
        getPopup().addAddHandler( addhandler );
        return addHandler( addhandler, AddEvent.getType() );
    }

    public void setAddButtonVisibility(boolean isVisible) {
        getPopup().setAddButtonVisibility(isVisible);
    }

    public void setAddButton (boolean addVisible, String text){
        getPopup().setAddButton(addVisible, text);
    }

    public void setSearchEnabled(boolean isSearchEnabled) {
        if (isSearchEnabled) {
            getPopup().setSearchHandler(this);
        } else {
            getPopup().setSearchHandler(null);
        }
    }

    public SelectorPopup getPopup() {
        if (popup == null) {
            setPopup( new ArrowSelectableSelectorPopup( true) );
            setSearchEnabled( true );
        }
        return popup;
    }

    public void setPopup(SelectorPopup popup) {
        this.popup = popup;
        popup.setPopupHandler(this);
    }

    public void setFixedStrategy(boolean isFixedStrategy) {
        popup.setFixedStrategy(isFixedStrategy);
    }

    /**
     * Выполнить после сокрытия попапа
     */
    public void setPopupUnloadHandler( Runnable popupUnloadHandler ){
        this.popupUnloadHandler = popupUnloadHandler;
    }

    public void setAutoCloseable(boolean isAutoCloseable) {
        this.isAutoCloseable = isAutoCloseable;
    }

    /**
     * Основной метод рендеринга элемента
     */
    protected abstract SelectorItem<T> makeSelectorItem( T element, String elementHtml );

    /**
     * Логинка селектора
     */
    protected abstract AbstractPageableSelector<T> getSelector();

    /**
     * При изменении выбранных значений
     */
    protected abstract void onSelectionChanged();

    protected void checkNoElements() {
        getPopup().setNoElements(getPopup().isEmpty(), isEmpty(getSelector().getSearchString()) ? emptyListText : emptySearchText  );
    }

    private void clearPopupItems() {
        getPopup().clear();
    }

    private SelectorItem<T> makeItemView(T t, String elementHtml) {
        SelectorItem<T> itemView = makeSelectorItem(t, elementHtml );
        itemView.setValue(t);
        itemView.addSelectorHandler(this);
        return itemView;
    }

    // Переопределяется в AsyncSearchSelectorModel
    private SearchHandler searchHandler = new SearchHandler() {
        @Override
        public void onSearch(String searchString) {
            clearPopupItems();
            getSelector().setSearchString(searchString);
            getSelector().fillFromBegin(AbstractPopupSelector.this);
            checkNoElements();
        }
    };

    private SelectorPopup popup;

    private String emptyListText = null;
    private String emptySearchText = emptyListText;
    private boolean isAutoCloseable = true;

    public static final String DISABLED = "disabled";
    private Runnable popupUnloadHandler;
}
