package ru.protei.portal.ui.common.client.widget.components.client.selector.logic.single;

import com.google.gwt.user.client.TakesValue;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.Selection;


/**
 * Логика селектора с единичным выбором
 */
public class SingleValueSelector<T> extends AbstractPageableSelector<T> implements TakesValue<T> {

    @Override
    public void setValue(T value) {
        selection.clear();
        selection.select(value);
    }

    @Override
    public T getValue() {
        return selection.get();
    }

    private SingleSelection<T> selection = new SingleSelection<T>() {
        protected T options = null;


        @Override
        public void select(T value) {
            options = value;
        }

        @Override
        public T get() {
            return options;
        }

        @Override
        public boolean isSelected(T option) {
            return option == options || option != null && option.equals( options );
        }

        @Override
        public boolean isEmpty() {
            return options == null;
        }

        @Override
        public void clear() {
            options = null;
        }
    };

    @Override
    public Selection<T> getSelection() {
        return selection;
    }
}

