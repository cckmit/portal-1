package ru.protei.portal.ui.common.client.selector;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.selector.pageable.*;
import ru.protei.portal.ui.common.client.selector.popup.PopupHandler;
import ru.protei.portal.ui.common.client.selector.popup.SelectorPopupWithSearch;
import ru.protei.portal.ui.common.client.selector.popup.item.SelectorItemHandler;

import java.util.Iterator;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

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
        T value = selectorItem.getValue();
        getSelector().getSelection().select(value);

        onSelectionChanged();
    }

    @Override
    public void onPopupUnload(SelectorPopup selectorPopup) {
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
        getPopup().getChildContainer().add(itemView.asWidget());
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
        getSelector().setModel( new SelectorModel<T>() {
            @Override
            public T get(int elementIndex) {
                return selectorModel.get(elementIndex, AbstractPopupSelector.this);
            }
        });
    }

    public void clearSelector(){
        clearPopupItems();
        if (getPopup() instanceof SelectorPopupWithSearch) {
            ((SelectorPopupWithSearch) getPopup()).clearSearchField();
            getSelector().setSearchString(null);
        }
    }

    public void setAsyncSearchModel( final AsyncSearchSelectorModel<T> selectorModel) {
        getSelector().setModel( new SelectorModel<T>() {
            @Override
            public T get(int elementIndex) {
                return selectorModel.get(elementIndex, AbstractPopupSelector.this);
            }
        });
        searchHandler = new SearchHandler() {
            @Override
            public void onSearch(String searchString) {
                clearPopupItems();
                selectorModel.setSearchString(searchString);
                getSelector().fillFromBegin(AbstractPopupSelector.this);
                checkNoElements();
            }
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
            setPopup( new SelectorPopupWithSearch() );
            setSearchEnabled( true );
        }
        return popup;
    }

    public void setPopup(SelectorPopup popup) {
        this.popup = popup;
        popup.setPopupHandler(this);
    }

    /**
     * Выполнить после сокрытия попапа
     */
    public void setPopupUnloadHandler( Runnable popupUnloadHandler ){
        this.popupUnloadHandler = popupUnloadHandler;
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
        Iterator<Widget> it = getPopup().getChildContainer().iterator();
        getPopup().setNoElements( !it.hasNext(), isEmpty(getSelector().getSearchString()) ? emptyListText : emptySearchText  );
    }

    private void clearPopupItems() {
        getPopup().getChildContainer().clear();
    }

    private SelectorItem<T> makeItemView(T t, String elementHtml) {
        SelectorItem<T> itemView = makeSelectorItem(t, elementHtml);
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

    public static final String DISABLED = "disabled";
    private Runnable popupUnloadHandler;
}
