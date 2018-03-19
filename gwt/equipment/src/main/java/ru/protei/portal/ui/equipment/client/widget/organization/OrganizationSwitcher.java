package ru.protei.portal.ui.equipment.client.widget.organization;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

import java.util.HashSet;

public class OrganizationSwitcher extends OrganizationBtnGroupMulti {
    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
        super.onValueChange(event);
        selected = new HashSet<>(1);
        selected.add(itemViewToModel.get(event.getSource()));
        refreshValue();
    }
}
