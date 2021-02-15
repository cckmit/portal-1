package ru.protei.portal.ui.common.client.selector.pageable;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import ru.protei.portal.ui.common.client.selector.selection.Selection;

import java.util.function.Supplier;

/**
 * Селектор с постраничным отображение списка доступных значений
 */
public abstract class AbstractPageableSelector<T> implements Selector<T> {

    @Override
    public void setModel( SelectorModel selectorModel) {
        this.selectorModel = selectorModel;
    }

    @Override
    public void setItemRenderer( SelectorItemRenderer<T> selectorItemRenderer) {
        this.selectorItemRenderer = selectorItemRenderer;
    }

    public void setNullItem(Supplier<T> selectorNullItem) {
        this.selectorNullItem = selectorNullItem;
    }

    public void fillFromBegin(ItemsContainer<T> itemsContainer) {
        fromIndex = 0;
        if (hasNullValue) {
            if (!(hideSelectedFromChose && getSelection().isEmpty())) {
                itemsContainer.fill(selectorNullItem.get(), makeElementHtml(selectorNullItem.get()));
            }
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

    public abstract Selection<T> getSelection();

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

    public String getSearchString() {
        return searchString;
    }

    private int fillElements( ItemsContainer<T> container, int from, int limit) {
        RegExp ignoreCasePattern = null;
        if(!isEmpty(searchString)){
            ignoreCasePattern = ignoreCasePattern( searchString );
        }

        for (int i = 0; i < limit; ) {

            T element = selectorModel.get(from);
            if (element == null) break;
            from++;

            if (hideSelectedFromChose && getSelection().isSelected(element)) {
                continue;
            }
            if (filter != null && !filter.isDisplayed(element)) {
                continue;
            }
            String elementName = makeElementName(element);
            if (elementName != null
                    && !isEmpty(searchString)
                    && !contains(elementName, ignoreCasePattern)) {
                continue;
            }

            container.fill(element, makeElementHtml(element));

            i++;
        }

        return from;
    }

    private static boolean contains( String string, RegExp pattern ) {
        if ( string == null || pattern == null ) {
            return false;
        }
        MatchResult m = pattern.exec( string );

        return (m != null && m.getGroupCount() > 0);
    }

    private static RegExp ignoreCasePattern( String patternString ) {
        if ( patternString == null ) {
            return null;
        }
        return RegExp.compile( RegExp.quote( patternString ), "i" );
    }

    private boolean isEmpty(String string) {
        return string == null || string.length() < 1;
    }

    protected boolean hasNullValue = false;

    protected boolean hideSelectedFromChose = false;

    protected ru.protei.portal.ui.common.client.widget.selector.base.Selector.SelectorFilter<T> filter = null;

    protected SelectorItemRenderer<T> selectorItemRenderer = String::valueOf;

    protected SelectorModel<T> selectorModel = elementIndex -> null;

    private Supplier<T> selectorNullItem = () -> null;

    private String searchString;

    private int fromIndex = 0;

    private static final int UNLIMITED_BY_DEFAULT = Integer.MAX_VALUE;
    private int pageSize = UNLIMITED_BY_DEFAULT;
}

