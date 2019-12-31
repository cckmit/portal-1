package ru.protei.portal.ui.common.client.widget.components.client.button;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.widget.components.client.selector.*;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.SelectorItem;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.SelectorItemHandler;
import ru.protei.portal.ui.common.client.widget.components.client.selector.popup.ItemsContainer;
import ru.protei.portal.ui.common.client.widget.components.client.selector.popup.PopupHandler;
import ru.protei.portal.ui.common.client.widget.components.client.selector.popup.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.components.client.selector.popup.SelectorPopupWithSearch;
import ru.protei.portal.ui.common.client.widget.components.client.selector.search.SearchHandler;

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
    public void setSelectorItemRenderer(SelectorItemRenderer<T> selectorItemRenderer) {
        getSelector().setSelectorItemRenderer(selectorItemRenderer);
    }

    @Override
    public void setSelectorModel(SelectorModel<T> selectorModel) {
        this.getSelector().setSelectorModel(selectorModel);
    }

    @Override
    public void onPopupUnload(SelectorPopup selectorPopup) {
        clearPopupItems();
        getPopup().setNoElements(false, null);
        if (popupUnloadHandler != null) popupUnloadHandler.run();
    }

    @Override
    public void onUnload() {
        clearPopupItems();
        if (getPopup() instanceof SelectorPopupWithSearch) {
            ((SelectorPopupWithSearch) getPopup()).clearSearchField();
            getSelector().setSearchString(null);
        }
    }

    @Override
    public void onEndOfScroll() {
        getSelector().fillContinue(this);
    }

    @Override
    public void fill( T element, String elementHtml) {
        SelectorItem itemView = makeItemView(element, elementHtml);
        getPopup().getChildContainer().add(itemView.asWidget());
    }

    @Override
    public void onSearch(String searchString) {
        searchHandler.onSearch(searchString);
    }

    public void setPageSize(int pageSize) {
        getSelector().setPageSize(pageSize);
    }

    public boolean isSelected(T element){
        return getSelector().getSelection().isSelected( element );
    }

    public void setAsyncSelectorModel(final AsyncSelectorModel<T> selectorModel) {
        getSelector().setSelectorModel(new SelectorModel<T>() {
            @Override
            public T get(int elementIndex) {
                return selectorModel.get(elementIndex, AbstractPopupSelector.this);
            }
        });
    }

    public void setAsyncSearchSelectorModel(final AsyncSearchSelectorModel<T> selectorModel) {
        getSelector().setSelectorModel(new SelectorModel<T>() {
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
    protected abstract SelectorItem makeSelectorItem( T element, String elementHtml );

    /**
     * Логинка селектора
     */
    protected abstract AbstractPageableSelector getSelector();

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

    private SelectorItem makeItemView(T t, String elementHtml) {
        SelectorItem itemView = makeSelectorItem(t, elementHtml);
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
