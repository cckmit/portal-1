package ru.protei.portal.ui.product.client.widget.type;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.ui.common.client.lang.En_DevUnitTypeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

public class ProductTypeBtnGroupMulti extends ToggleBtnGroupMulti<En_DevUnitType> {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();
        for (En_DevUnitType type : En_DevUnitType.getValidValues()) {
            addBtnWithImage(
                    type.getImgSrc(),
                    "btn btn-white btn-without-border du-type",
                    null,
                    type,
                    typeLang.getName(type)
            );
        }
    }

    @Inject
    En_DevUnitTypeLang typeLang;
}
