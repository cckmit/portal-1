package ru.protei.portal.ui.common.client.widget.selector.base;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.winter.core.utils.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Селектор заполняется значениями при его аттаче на страницу и очищается при деттаче.
 */
public abstract class LifecycleSelectorModel<T> implements Activity, SelectorModel<T> {

    @Override
    public void onSelectorLoad(SelectorWithModel<T> selector) {
        if (selector == null) {
            return;
        }
        subscribers.add(selector);
        if (CollectionUtils.isNotEmpty(selector.getValues())) {
            return;
        }
        if (CollectionUtils.isNotEmpty(cache)) {
            notifySubscriber(selector, cache);
            return;
        }
        refreshOptions();
    }

    @Override
    public void onSelectorUnload(SelectorWithModel<T> selector) {
        if (selector == null) {
            return;
        }
        subscribers.remove(selector);
        selector.clearOptions();
    }

    protected void clear() {
        cache.clear();
        subscribers.forEach(SelectorWithModel::clearOptions);
    }

    protected void notifySubscribers(List<T> data) {
        cache.clear();
        cache.addAll(data);
        for (SelectorWithModel<T> selector : subscribers) {
            notifySubscriber(selector, data);
        }
    }

    protected void notifySubscriber(SelectorWithModel<T> selector, List<T> data) {
        selector.fillOptions(data);
        selector.refreshValue();
    }

    protected abstract void refreshOptions();

    protected List<T> cache = new ArrayList<>();
    protected List<SelectorWithModel<T>> subscribers = new ArrayList<>();
}
