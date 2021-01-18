package ru.protei.portal.ui.common.client.widget.popupselector;

import ru.protei.portal.ui.common.client.selector.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.composite.popper.PopperComposite;

public class RemovablePopupSingleSelector<T> extends PopupSingleSelector<T> {
    @Override
    public void onPopupHide(SelectorPopup selectorPopup) {
        super.onPopupHide(selectorPopup);
        getPopup().asWidget().getElement().removeFromParent();
    }

    public void showPopup() {
        showPopup(PopperComposite.Placement.BOTTOM_END, 0, DISTANCE);
    }

    private static final int DISTANCE = 2;
}
