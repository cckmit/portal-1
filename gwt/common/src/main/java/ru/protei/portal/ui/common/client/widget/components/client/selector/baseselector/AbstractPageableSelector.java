package ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector;

import ru.protei.portal.ui.common.client.widget.components.client.selector.Selector;
//import ru.protei.portal.ui.common.client.widget.components.client.selector.SelectorFilter;
import ru.protei.portal.ui.common.client.widget.components.client.selector.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.widget.components.client.selector.SelectorModel;
import ru.protei.portal.ui.common.client.widget.components.client.selector.popup.ItemsContainer;

/**
 * Селектор с постраничным отображение списка доступных значений
 */
public abstract class AbstractPageableSelector<T> implements Selector<T> {

    @Override
    public void setSelectorModel(SelectorModel selectorModel) {
        this.selectorModel = selectorModel;
    }

    @Override
    public void setSelectorItemRenderer(SelectorItemRenderer<T> selectorItemRenderer) {
        this.selectorItemRenderer = selectorItemRenderer;
    }

    public void fillFromBegin(ItemsContainer<T> itemsContainer) {
        fromIndex = 0;
        if (hasNullValue) {
            itemsContainer.fill(null, makeElementHtml(null));
        }
        fromIndex = fillElements(itemsContainer, fromIndex, pageSize);
    }

    public void fillContinue(ItemsContainer<T> itemsContainer) {
        int limit = (pageSize + fromIndex) % pageSize;
        limit = (limit == 0) ? pageSize : pageSize - limit;
        fromIndex = fillElements(itemsContainer, fromIndex, limit);
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public abstract SelectionModel<T> getSelectionModel();

    public void setHasNullValue(boolean hasNullValue) {
        this.hasNullValue = hasNullValue;
    }

    public boolean hasNullValue() {
        return hasNullValue;
    }

    public void setHideSelectedFromChose(boolean hideSelectedFromChose) {
        this.hideSelectedFromChose = hideSelectedFromChose;
    }

    public boolean isHideSelectedFromChose() {
        return hideSelectedFromChose;
    }

    public void setFilter( ru.protei.portal.ui.common.client.widget.selector.base.Selector.SelectorFilter<T> selectorFilter) {
        filter = selectorFilter;
    }

    public String makeElementName(T t) {
        return selectorItemRenderer.getElementName(t);
    }

    public String makeElementHtml(T t) {
        return selectorItemRenderer.getElementHtml(t);
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    private int fillElements(ItemsContainer<T> container, int from, int limit) {
        for (int i = 0; i < limit; ) {

            T element = selectorModel.get(from);
            if (element == null) break;
            from++;

            if (hideSelectedFromChose && getSelectionModel().isSelected(element)) {
                continue;
            }
            if (filter != null && !filter.isDisplayed(element)) {
                continue;
            }
            String elementName = makeElementName(element);
            if (elementName != null
                    && !isEmpty(searchString)
                    && !elementName.contains(searchString)) {
                continue;
            }

            container.fill(element, makeElementHtml(element));

            i++;
        }

        return from;
    }

    private boolean isEmpty(String searchString) {
        return searchString == null || searchString.length() < 1;
    }

    protected boolean hasNullValue = true;

    protected boolean hideSelectedFromChose = false;

    protected ru.protei.portal.ui.common.client.widget.selector.base.Selector.SelectorFilter<T> filter = null;

    protected SelectorItemRenderer<T> selectorItemRenderer = new SelectorItemRenderer<T>() {
        @Override
        public String getElementName(T t) {
            return String.valueOf(t);
        }
    };

    protected SelectorModel<T> selectorModel = new SelectorModel<T>() {
        @Override
        public T get(int elementIndex) {
            return null;
        }
    };

    private String searchString;

    private int fromIndex = 0;

    private static final int UNLIMITED_BY_DEFAULT = Integer.MAX_VALUE;
    private int pageSize = UNLIMITED_BY_DEFAULT;
}

