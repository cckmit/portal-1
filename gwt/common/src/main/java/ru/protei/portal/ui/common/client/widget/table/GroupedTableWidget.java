package ru.protei.portal.ui.common.client.widget.table;

import com.google.gwt.user.client.ui.InlineLabel;
import ru.brainworm.factory.widget.table.client.TableWidget;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GroupedTableWidget<T, G> extends TableWidget<T> {
    public void setGroupFunctions(HasGroupFunctions<T, G> hasGroupFunctions) {
        this.hasGroupFunctions = hasGroupFunctions;
    }

    public void addRecords(List<T> objects) {
        Map<G, List<T>> groupToObjects = new LinkedHashMap<>();

        for (T nextObject : objects) {
            G group = hasGroupFunctions.makeGroup(nextObject);

            groupToObjects.computeIfAbsent(group, gr -> new ArrayList<>());
            groupToObjects.get(group).add(nextObject);
        }

        groupToObjects.forEach((group, values) -> {
            addCustomRow(
                    new InlineLabel(hasGroupFunctions.makeGroupName(group)).getElement(),
                    "separator-cell",
                    "separator-row"
            );
            values.forEach(this::addRow);
        });
    }

    private HasGroupFunctions<T, G> hasGroupFunctions;
}
