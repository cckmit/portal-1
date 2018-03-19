package ru.protei.portal.ui.document.client.widget.number;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.ui.equipment.client.widget.number.item.DecimalNumberBox;

public class DecimalNumberInput extends DecimalNumberBox {
    @Inject
    public void setRemoveNotVisible() {
        DecimalNumber decimalNumber = new DecimalNumber();
        decimalNumber.setOrganizationCode(En_OrganizationCode.PAMR);
        setValue(decimalNumber);

        setRemoveVisible(false);
        setShowSwitcher(true);
        setReserveVisible(false);
    }
}
