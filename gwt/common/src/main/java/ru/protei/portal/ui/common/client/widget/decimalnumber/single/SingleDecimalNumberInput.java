package ru.protei.portal.ui.common.client.widget.decimalnumber.single;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.decimalnumber.box.DecimalNumberBox;
import ru.protei.portal.ui.common.client.widget.decimalnumber.box.DecimalNumberBoxHandler;
import ru.protei.portal.ui.common.client.widget.decimalnumber.provider.DecimalNumberDataProvider;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.web.common.client.common.DisplayStyle;

import java.util.Collections;

public class SingleDecimalNumberInput extends DecimalNumberBox implements DecimalNumberBoxHandler {
    public SingleDecimalNumberInput() {
        setHandler(this);
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
    DecimalNumberDataProvider dataProvider;

    @Inject
    Lang lang;
}
