package ru.protei.portal.ui.product.client.widget.type;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.lang.En_DevUnitTypeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

public class ProductTypeBtnGroup extends ToggleBtnGroup<En_DevUnitType> {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();
        for (En_DevUnitType type : En_DevUnitType.getValidValues()) {
            addBtnWithImage(
                    type.getImgSrc(),
                    "btn btn-default du-type",
                    null,
                    type,
                    typeLang.getName(type)
            );
            setEnsureDebugId(type, DebugIdsHelper.PRODUCT_TYPE.byId(type.getId()));
        }
    }

    @Inject
    En_DevUnitTypeLang typeLang;
}
