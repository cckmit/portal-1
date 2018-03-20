package ru.protei.portal.ui.equipment.client.widget.organization;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import ru.protei.portal.core.model.dict.En_OrganizationCode;

import java.util.HashSet;
import java.util.Set;

public class OrganizationSwitcher extends OrganizationBtnGroupMulti {
    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
        super.onValueChange(event);
        selected = new HashSet<>(1);
        selected.add(itemViewToModel.get(event.getSource()));
        refreshValue();
    }

    public void setSingleValue(En_OrganizationCode organizationCode) {
        setValue(oneItemSet(organizationCode));
    }

    private static <T> Set<T> oneItemSet(T el) {
        Set<T> s = new HashSet<>(1);
        s.add(el);
        return s;
    }

    public En_OrganizationCode getSingleValue() {
        if (getValue() == null) {
            return null;
        }
        return getValue().stream().findFirst().get();
    }
}
