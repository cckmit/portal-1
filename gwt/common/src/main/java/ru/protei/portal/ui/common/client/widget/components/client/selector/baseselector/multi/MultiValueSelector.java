package ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.multi;

import com.google.gwt.user.client.TakesValue;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Логика селектора со множественным выбором
 */
public class MultiValueSelector<T> extends AbstractPageableSelector<T>
        implements TakesValue<Set<T>> {

    @Override
    public SelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    public void setSelectionModel(MultiSelectionModel<T> selectionModel) {
        this.selectionModel = selectionModel;
    }

    @Override
    public void setValue(Set<T> value) {
        if (value == null) {
            selectionModel.clear();
            return;
        }
        for (T t : value) {
            selectionModel.select(t);
        }
    }

    @Override
    public Set<T> getValue() {
        return selectionModel.get();
    }

    private MultiSelectionModel<T> selectionModel = new MultiSelectionModel<T>() {
        protected Set<T> selectedOption = new TreeSet<T>();

        @Override
        public void select(T value) {
            if (selectedOption.contains(value)) {
                selectedOption.remove(value);
            } else {
                selectedOption.add(value);
            }
        }

        @Override
        public Set<T> get() {
            return selectedOption;
        }

        @Override
        public boolean isSelected(T option) {
            return option != null && selectedOption.contains(option);
        }

        @Override
        public boolean isEmpty() {
            return selectedOption.isEmpty();
        }

        @Override
        public void clear() {
            selectedOption.clear();
        }
    };


}

