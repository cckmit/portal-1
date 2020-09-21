package ru.protei.portal.ui.roomreservation.client.widget.selector.month;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

import static ru.protei.portal.ui.common.client.util.DateUtils.getMonthName;

public class MonthButtonSelector extends ButtonPopupSingleSelector<Integer> {

    @Inject
    void init(MonthModel model, Lang lang) {
        setModel(model);
        setSearchEnabled(false);
        setItemRenderer(month -> makeItemView(month, lang));
    }

    private String makeItemView(Integer month, Lang lang) {
        return month == null ? defaultValue : getMonthName(month, lang);
    }
}
