package ru.protei.portal.ui.common.client.activity.confirmdialog;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;

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
        if ( event.identity == null && event.action == null ) {
            return;
        }

        identity = event.identity;
        action = event.action;
        view.setText( event.text );
        if ( event.confirmButtonText != null ) {
            view.setConfirmButtonText( event.confirmButtonText );
        }
        view.center();
    }

    @Override
    public void onConfirmClicked() {
        view.hide();

        String identityTmp = identity;
        if (identityTmp != null) {
            fireEvent( new ConfirmDialogEvents.Confirm(identityTmp) );
            identity = null;
        }

        ConfirmDialogEvents.Show.Action actionTmp = action;
        if (actionTmp != null) {
            action.onConfirm();
            action = null;
        }
    }

    @Override
    public void onCancelClicked() {
        view.hide();

        String identityTmp = identity;
        if (identityTmp != null) {
            fireEvent( new ConfirmDialogEvents.Cancel( identityTmp ) );
            identity = null;
        }

        ConfirmDialogEvents.Show.Action actionTmp = action;
        if (actionTmp != null) {
            action.onCancel();
            action = null;
        }
    }

    @Inject
    AbstractConfirmDialogView view;

    private String identity;
    private ConfirmDialogEvents.Show.Action action;
}
