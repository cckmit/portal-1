package ru.protei.portal.ui.document.client.widget.number;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.equipment.client.provider.AbstractDecimalNumberDataProvider;
import ru.protei.portal.ui.equipment.client.widget.number.item.DecimalNumberBox;
import ru.protei.portal.ui.equipment.client.widget.number.item.DecimalNumberBoxHandler;
import ru.protei.winter.web.common.client.common.DisplayStyle;

import java.util.Collections;

public class DecimalNumberInput extends DecimalNumberBox implements DecimalNumberBoxHandler {
    public DecimalNumberInput() {
        setHandler(this);
    }

    public void setSingleNumberView() {
        setRemoveVisible(false);
        setOrganizationCodeEnabled(true);
        setReserveVisible(false);
    }

    @Override
    public void onGetNextNumber(DecimalNumberBox box) {
        DecimalNumberQuery query = new DecimalNumberQuery(box.getValue(), Collections.emptySet());

        dataProvider.getNextAvailableRegisterNumber(query, new RequestCallback<Integer>() {
            @Override
            public void onError(Throwable throwable) {
                box.showMessage(lang.equipmentErrorGetNextAvailableNumber(), DisplayStyle.DANGER);
            }

            @Override
            public void onSuccess(Integer registerNumber) {
                DecimalNumber number = box.getValue();
                number.setRegisterNumber(registerNumber);
                number.setModification(null);

                box.setValue(number);
                box.setFocusToRegisterNumberField(true);
            }
        });
    }

    @Override
    public void onGetNextModification(DecimalNumberBox box) {
        DecimalNumber value = box.getValue();
        DecimalNumberQuery query = new DecimalNumberQuery(box.getValue(), Collections.emptySet());

        dataProvider.getNextAvailableModification(query, new RequestCallback<Integer>() {
            @Override
            public void onError(Throwable throwable) {
                box.showMessage(lang.equipmentErrorGetNextAvailableNumber(), DisplayStyle.DANGER);
            }

            @Override
            public void onSuccess(Integer modification) {
                value.setModification(modification);
                box.setValue(value);
                box.clearBoxState();
            }
        });
    }

    @Inject
    AbstractDecimalNumberDataProvider dataProvider;

    @Inject
    Lang lang;
}
