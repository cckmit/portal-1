package ru.protei.portal.ui.common.client.widget.table;

import com.google.gwt.user.client.ui.InlineLabel;
import ru.brainworm.factory.widget.table.client.TableWidget;

import java.util.*;

public class GroupedTableWidget<T, G> extends TableWidget<T> {
    public void setGroupFunctions(HasGroupFunctions<T, G> hasGroupFunctions) {
        this.hasGroupFunctions = hasGroupFunctions;
    }

    public void addRecords(List<T> objects) {
        Comparator<? super Map.Entry<G, List<T>>> comparator = (Comparator<Map.Entry<G, List<T>>>) (o1, o2) -> 0;
        addRecords(objects, comparator);
    }

    public void addRecords(List<T> objects, Comparator<? super Map.Entry<G, List<T>>> comparator) {
        Map<G, List<T>> groupToObjects = new LinkedHashMap<>();

        for (T nextObject : objects) {
            G group = hasGroupFunctions.makeGroup(nextObject);

            groupToObjects.computeIfAbsent(group, gr -> new ArrayList<>());
            groupToObjects.get(group).add(nextObject);
        }

        groupToObjects.entrySet().stream().sorted(comparator).forEach((entry) -> {
            addCustomRow(
                    new InlineLabel(hasGroupFunctions.makeGroupName(entry.getKey())).getElement(),
                    "separator-cell",
                    "separator-row"
            );
            entry.getValue().forEach(this::addRow);
        });
    }

    private HasGroupFunctions<T, G> hasGroupFunctions;
}
