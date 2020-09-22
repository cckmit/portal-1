package ru.protei.portal.ui.common.client.widget.popupselector;

import ru.protei.portal.ui.common.client.selector.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.composite.popper.PopperComposite;

public class RemovablePopupSingleSelector<T> extends PopupSingleSelector<T> {
    @Override
    public void onPopupHide(SelectorPopup selectorPopup) {
        super.onPopupHide(selectorPopup);
        getPopup().asWidget().getElement().removeFromParent();
    }

    public void hidePopup() {
        getPopup().hide();
    }

    public void clearPopup() {
        getPopup().getChildContainer().clear();
    }

    public void showPopup() {
        getPopup().showNear(relative, PopperComposite.Placement.BOTTOM, -(POPUP_WIDTH - relative.getOffsetWidth()) / 2, DISTANCE);
    }

    private static final int POPUP_WIDTH = 225;
    private static final int DISTANCE = 2;
}
