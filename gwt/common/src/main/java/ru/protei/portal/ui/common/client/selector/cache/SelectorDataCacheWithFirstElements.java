package ru.protei.portal.ui.common.client.selector.cache;

import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;

import java.util.ArrayList;
import java.util.List;

public class SelectorDataCacheWithFirstElements<T> extends SelectorDataCache<T> {
    public T getNext(LoadingHandler loadingHandler) {
        if (index < firstElements.size()) {
            return firstElements.get(index++);
        }

        T value = get(index++, loadingHandler);

        if (value == null) {
            index--;
            return null;
        }

        if (firstElements.contains(value)) {
            return getNext(loadingHandler);
        }

        return value;
    }

    public void setTotal(int total) {
        super.setTotal(total);
    }

    public void resetIndex() {
        index = 0;
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

    private int index;
    private List<T> firstElements = new ArrayList<>();
}
