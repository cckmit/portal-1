package ru.protei.portal.ui.delivery.client.widget.pcborder.ordertype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PcbOrderType;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderTypeLang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class PcbOrderTypeFormSelector extends FormPopupSingleSelector<En_PcbOrderType> {

    @Inject
    public void init() {
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value));
        setValueRenderer(value -> value == null ? defaultValue : lang.getName(value));
        setModel(elementIndex -> {
            if (size(values) <= elementIndex) return null;
            return values.get(elementIndex);
        });
    }

    @Inject
    En_PcbOrderTypeLang lang;

    private List<En_PcbOrderType> values = Arrays.asList(En_PcbOrderType.values());
}
