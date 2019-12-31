package ru.protei.portal.ui.common.client.widget.components.client.selector.logic.multi;

import com.google.gwt.user.client.TakesValue;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.Selection;

import java.util.*;


/**
 * Логика селектора со множественным выбором
 */
public class MultiValueSelector<T> extends AbstractPageableSelector<T>
        implements TakesValue<Set<T>> {

    @Override
    public Selection<T> getSelection() {
        return selection;
    }

    @Override
    public void setValue(Set<T> value) {
        selection.clear();
        if (value == null) {
            return;
        }
        for (T t : value) {
            selection.select(t);
        }
    }

    @Override
    public Set<T> getValue() {
        return selection.get();
    }

    private MultiSelection<T> selection = new MultiSelection<T>() {
        protected List<T> options = new ArrayList<>();

        @Override
        public void select(T value) {
            if (options.contains(value)) {
                options.remove(value);
            } else {
                options.add(value);
            }
        }

        @Override
        public Set<T> get() {
            return new HashSet<>( options );
        }

        @Override
        public boolean isSelected(T option) {
            return option != null && options.contains(option);
        }

        @Override
        public boolean isEmpty() {
            return options.isEmpty();
        }

        @Override
        public void clear() {
            options.clear();
        }
    };


}

