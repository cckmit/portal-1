package ru.protei.portal.ui.common.client.widget.decimalnumber.single;

import ru.protei.portal.ui.common.client.widget.decimalnumber.box.DecimalNumberBox;
import ru.protei.portal.ui.common.client.widget.decimalnumber.box.DecimalNumberBoxHandler;

public class SingleDecimalNumberInput extends DecimalNumberBox implements DecimalNumberBoxHandler {
    public SingleDecimalNumberInput() {
        setHandler(this);
    }

    @Override
    public void onGetNextNumber(DecimalNumberBox box) {
    }

    @Override
    public void onGetNextModification(DecimalNumberBox box) {
    }
}
