package ru.protei.portal.ui.common.client.widget.components.client.buttonselector;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.widget.components.client.selector.search.SearchHandler;
import ru.protei.portal.ui.common.client.widget.components.client.selector.*;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.*;
import ru.protei.portal.ui.common.client.widget.components.client.selector.popup.*;

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
    public void setSelectorModel(SelectorModel<T> selectorModel) {
        this.getSelector().setSelectorModel(selectorModel);
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
        Widget itemView = makeItemView(element, elementHtml);
        getPopup().getChildContainer().add(itemView);
    }

    @Override
    public void onSearch(String searchString) {
        searchHandler.onSearch(searchString);
    }

    public void setSearchAutoFocus(boolean isSearchAutoFocus){//TODO
        if(getPopup() instanceof SelectorPopupWithSearch) ((SelectorPopupWithSearch) getPopup()).setSearchAutoFocus(isSearchAutoFocus);
    }

    public void setPageSize(int pageSize) {
        getSelector().setPageSize(pageSize);
    }

    public void setAsyncSelectorModel(final AsyncSelectorModel<T> selectorModel) {
        this.getSelector().setSelectorModel(new SelectorModel<T>() {
            @Override
            public T get(int elementIndex) {
                return selectorModel.get(elementIndex, AbstractPopupSelector.this);
            }
        });
    }

    public void setAsyncSearchSelectorModel(final AsyncSearchSelectorModel<T> selectorModel) {
        this.getSelector().setSelectorModel(new SelectorModel<T>() {
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
            setPopup(makeSearchPopup());
        } else {
            setPopup(makeSimplePopup());
        }
    }

    public SimpleSelectorPopup makeSimplePopup() {
        final SimpleSelectorPopup popup = new SimpleSelectorPopup();
        popup.setPopupHandler(this);
        return popup;
    }

    public SelectorPopupWithSearch makeSearchPopup() {
        final SelectorPopupWithSearch popup = new SelectorPopupWithSearch();
        popup.setPopupHandler(this);
        popup.setSearchHandler(this);
        return popup;
    }


    public SelectorPopup getPopup() {
        if (popup == null) {
            setPopup(makeSimplePopup());
        }
        return popup;
    }

    public void setPopup(SelectorPopup popup) {
        this.popup = popup;
        popup.setPopupHandler(this);
        if (getPopup() instanceof SelectorPopupWithSearch) {
            ((SelectorPopupWithSearch) getPopup()).setSearchHandler(this);
        }
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

    private Widget makeItemView(T t, String elementHtml) {
        SelectorItem itemView = makeSelectorItem(t, elementHtml);
        itemView.setValue(t);
        itemView.addSelectorHandler(this);
        itemView.addKeyUpHandler( keyUpItemHandler );
        return itemView.asWidget();
    }

    private SearchHandler searchHandler = new SearchHandler() {
        @Override
        public void onSearch(String searchString) {
            clearPopup();
            getSelector().setSearchString(searchString);
            getSelector().fillFromBegin(AbstractPopupSelector.this);
        }
    };

    private void onArrowUp( SelectorItem item) {
        HTMLPanel panel = (HTMLPanel) getPopup().getChildContainer();
        int selectedItemIndex = panel.getWidgetIndex(item);
        if (selectedItemIndex == 0) {
            return;
        }
        SelectorItem previousSelectorItem = (SelectorItem) panel.getWidget(--selectedItemIndex);
        previousSelectorItem.setFocus(true);
    }

    private void onArrowDown( SelectorItem item) {
        HTMLPanel panel = (HTMLPanel) getPopup().getChildContainer();
        int selectedItemIndex = panel.getWidgetIndex(item);
        if (selectedItemIndex == panel.getWidgetCount() - 1) {
            return;
        }
        SelectorItem nextSelectorItem = (SelectorItem) panel.getWidget(++selectedItemIndex);
        nextSelectorItem.setFocus(true);
    }


    KeyUpHandler keyUpItemHandler = keyUpEvent -> {
        keyUpEvent.preventDefault();
        if (keyUpEvent.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
            onArrowDown((SelectorItem) keyUpEvent.getSource());
        }

        if (keyUpEvent.getNativeKeyCode() == KeyCodes.KEY_UP) {
            onArrowUp((SelectorItem) keyUpEvent.getSource());
        }
    };


    private SelectorPopup popup;

    private String emptyListText = "-- No elements --";

    public static final String DISABLED = "disabled";
}
