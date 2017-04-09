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
        if ( event.identity == null ) {
            return;
        }

        identity = event.identity;
        view.setText( event.text );
        if ( event.confirmButtonText != null ) {
            view.setConfirmButtonText( event.confirmButtonText );
        }
        view.center();
    }

    @Override
    public void onConfirmClicked() {
        view.hide();
        fireEvent( new ConfirmDialogEvents.Confirm( identity ) );
    }

    @Override
    public void onCancelClicked() {
        view.hide();
        fireEvent( new ConfirmDialogEvents.Cancel( identity ) );
    }

    @Inject
    AbstractConfirmDialogView view;

    String identity;
}
