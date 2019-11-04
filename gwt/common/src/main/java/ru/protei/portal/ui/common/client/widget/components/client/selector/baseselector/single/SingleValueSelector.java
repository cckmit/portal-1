package ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.single;

import com.google.gwt.user.client.TakesValue;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectionModel;


/**
 * Логика селектора с единичным выбором
 */
public class SingleValueSelector<T> extends AbstractPageableSelector<T>
        implements TakesValue<T> {

    @Override
    public void setValue(T value) {
        selectionModel.select(value);
    }

    @Override
    public T getValue() {
        return selectionModel.get();
    }

    private SingleSelectionModel<T> selectionModel = new SingleSelectionModel<T>() {
        protected T selectedOption = null;


        @Override
        public void select(T value) {
            selectedOption = value;
        }

        @Override
        public T get() {
            return selectedOption;
        }

        @Override
        public boolean isSelected(T option) {
            return option == selectedOption || option != null && option.equals(selectedOption);
        }

        @Override
        public boolean isEmpty() {
            return selectedOption == null;
        }
    };

    @Override
    public SelectionModel<T> getSelectionModel() {
        return selectionModel;
    }
}

