package ru.protei.portal.ui.delivery.client.widget.pcborder.stenciltype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_StencilType;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderStencilTypeLang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class PcbOrderStencilTypeFormSelector extends FormPopupSingleSelector<En_StencilType> {

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
    En_PcbOrderStencilTypeLang lang;

    private List<En_StencilType> values = Arrays.asList(En_StencilType.values());
}
