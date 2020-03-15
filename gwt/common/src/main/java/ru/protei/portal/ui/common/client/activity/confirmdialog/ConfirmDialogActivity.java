package ru.protei.portal.ui.common.client.activity.confirmdialog;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Активность окна подтверждения.
 */
public abstract class ConfirmDialogActivity implements Activity, AbstractConfirmDialogActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onConfirmDialogShow( ConfirmDialogEvents.Show event ) {
        if (event.confirmAction == null) {
            return;
        }

        confirmAction = event.confirmAction;

        fillView(event);
    }

    private void fillView(ConfirmDialogEvents.Show event) {
        view.setText(event.text);
        view.confirmButtonText().setText(StringUtils.isBlank(event.confirmButtonText) ? lang.buttonYes() : event.confirmButtonText);
        view.cancelButtonText().setText(lang.buttonNo());
        view.center();
    }

    @Override
    public void onConfirmClicked() {
        view.hide();
        confirmAction.run();
    }

    @Override
    public void onCancelClicked() {
        view.hide();
    }

    @Inject
    AbstractConfirmDialogView view;

    @Inject
    Lang lang;

    private Runnable confirmAction;
}
