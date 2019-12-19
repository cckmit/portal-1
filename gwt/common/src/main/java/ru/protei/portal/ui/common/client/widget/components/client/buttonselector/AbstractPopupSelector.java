package ru.protei.portal.ui.common.client.widget.components.client.buttonselector;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.Widget;
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

/**
 * селектор c выпадающим списком
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
        getPopup().setNoElements(false, emptyListText);
    }

    @Override
    public void onLoadingComplete() {
        getPopup().showLoading(false);
        if (!isAttached()) return;
        getSelector().fillContinue(this);
        Iterator<Widget> it = getPopup().getChildContainer().iterator();
        getPopup().setNoElements(!it.hasNext(), emptyListText);
    }

    @Override
    public void onSelectorItemClicked(SelectorItem<T> selectorItem) {
        T value = selectorItem.getValue();
        getSelector().getSelectionModel().select(value);

        onSelectionChanged();
    }

    @Override
    public void setSelectorItemRenderer(SelectorItemRenderer<T> selectorItemRenderer) {
        getSelector().setSelectorItemRenderer(selectorItemRenderer);
    }

    @Override
    public void onPopupUnload(SelectorPopup selectorPopup) {
            clearPopup();
    }

    @Override
    public void onUnload() {
        clearPopup();
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

    @Override
    public void setSelectorModel(SelectorModel<T> selectorModel) {
        getSelector().setSelectorModel(selectorModel);
    }

    public boolean isSelected(T element){
        return getSelector().getSelectionModel().isSelected( element );
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
                clearPopup();
                selectorModel.setSearchString(searchString);
                getSelector().fillFromBegin(AbstractPopupSelector.this);
            }
        };
    }

    public void setSearchAutoFocus(boolean isSearchAutoFocus){//TODO
        if(getPopup() instanceof SelectorPopupWithSearch) ((SelectorPopupWithSearch) getPopup()).setSearchAutoFocus(isSearchAutoFocus);
    }

    public void setPageSize(int pageSize) {
        getSelector().setPageSize(pageSize);
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

    public void setFilter( ru.protei.portal.ui.common.client.widget.selector.base.Selector.SelectorFilter<T> selectorFilter) {
        getSelector().setFilter(selectorFilter);
    }

    public void setSearchEnabled(boolean isSearchEnabled) {
        if (isSearchEnabled) {
            getPopup().setSearchHandler(this);
        } else {
            getPopup().setSearchHandler(null);
        }
    }

    public SelectorPopup getPopup() {
        if(popup==null) setPopup( new SelectorPopupWithSearch() );
        return popup;
    }

    public void setPopup(SelectorPopup popup) {
        popup.setPopupHandler(this);
        this.popup = popup;
    }

    /**
     * Основной метод рендеринга элемента
     */
    protected abstract SelectorItem makeSelectorItem( T element, String elementHtml );

    protected abstract AbstractPageableSelector getSelector();

    protected abstract void onSelectionChanged();

    private void clearPopup() {
        getPopup().getChildContainer().clear();
    }

    private SelectorItem makeItemView(T t, String elementHtml) {
        SelectorItem itemView = makeSelectorItem(t, elementHtml);
        itemView.setValue(t);
        itemView.addSelectorHandler(this);
        return itemView;
    }

    private SearchHandler searchHandler = new SearchHandler() {
        @Override
        public void onSearch(String searchString) {
            clearPopup();
            getSelector().setSearchString(searchString);
            getSelector().fillFromBegin(AbstractPopupSelector.this);
        }
    };

    private SelectorPopup popup;

    private String emptyListText = "-- No elements --";

    public static final String DISABLED = "disabled";
}
