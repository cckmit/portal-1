package ru.protei.portal.ui.roomreservation.client.widget.selector.year;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class YearButtonSelector extends ButtonPopupSingleSelector<Integer> {

    @Inject
    void init(YearModel model) {
        setModel(model);
        setSearchEnabled(false);
        setItemRenderer(this::makeItemView);
    }

    private String makeItemView(Integer year) {
        return year == null ? defaultValue : String.valueOf(year);
    }
}
