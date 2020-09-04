package ru.protei.portal.ui.common.client.selector.cache;

import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;

import java.util.ArrayList;
import java.util.List;

public class SelectorDataCacheWithFirstElements<T> extends SelectorDataCache<T> {
    @Override
    public T get(int elementIndex, LoadingHandler loadingHandler) {
        if (elementIndex == 0) {
            resetIndex();
        }

        return getNext(loadingHandler);
    }

    @Override
    public void clearCache() {
        super.clearCache();
        resetIndex();
    }

    public void setFirstElements(List<T> firstElements) {
        this.firstElements.clear();
        this.firstElements.addAll(CollectionUtils.emptyIfNull(firstElements));
    }

    private T getNext(LoadingHandler loadingHandler) {
        if (index < firstElements.size()) {
            return firstElements.get(index++);
        }

        T value = super.get(index++, loadingHandler);

        if (value == null) {
            index--;
            return null;
        }

        if (firstElements.contains(value)) {
            return getNext(loadingHandler);
        }

        return value;
    }

    private void resetIndex() {
        index = 0;
    }

    private int index;
    private List<T> firstElements = new ArrayList<>();
}
