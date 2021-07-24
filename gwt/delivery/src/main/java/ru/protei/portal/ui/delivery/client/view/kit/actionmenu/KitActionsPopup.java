package ru.protei.portal.ui.delivery.client.view.kit.actionmenu;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.stringselectpopup.StringSelectPopup;
import ru.protei.portal.ui.delivery.client.activity.kit.handler.KitActionsHandler;

import java.util.Arrays;

public class KitActionsPopup extends StringSelectPopup {

    @Inject
    public KitActionsPopup(Lang lang) {
        setValues(Arrays.asList(lang.buttonCopy(), lang.buttonState(), lang.buttonRemove()));

        addValueChangeHandler(event -> {
            if (handler == null) {
                return;
            }
            if (lang.buttonCopy().equals(event.getValue())) {
                handler.onCopy();
                return;
            }
            if (lang.buttonState().equals(event.getValue())) {
                handler.onChangeState();
                return;
            }
            if (lang.buttonRemove().equals(event.getValue())) {
                handler.onRemove();
                return;
            }
        });
    }

    public void setHandler(KitActionsHandler handler) {
        this.handler = handler;
    }
    private KitActionsHandler handler;
}
