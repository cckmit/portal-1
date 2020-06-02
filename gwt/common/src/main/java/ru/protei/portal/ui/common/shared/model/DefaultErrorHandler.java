package ru.protei.portal.ui.common.shared.model;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.function.Consumer;


/**
 * Обрабока ошибок асинхронных серверных запросов
 */
public class DefaultErrorHandler implements Consumer<Throwable> {

    @Override
    public final void accept( Throwable throwable ) {
        if ( throwable instanceof IncompatibleRemoteServiceException ) {
            displayReloadPageDialog();
            return;
        }

        if (!( throwable instanceof RequestFailedException )) {
            return;
        }

        RequestFailedException rf = (RequestFailedException) throwable;
        activity.fireEvent(new NotifyEvents.Show( resultStatusLang.getMessage(rf.status), NotifyEvents.NotifyType.ERROR));
    }

    private void displayReloadPageDialog() {
        dialogView.setHeader( lang.reloadPageAfterUpdateHeader());
        dialogView.getBodyContainer().add( new Label(lang.reloadPageAfterUpdateMessage()) );
        dialogView.saveButtonVisibility().setVisible(true);
        dialogView.setSaveOnEnterClick(true);
        dialogView.setSaveButtonName(lang.reloadPageAfterUpdateDoReloadPage());
        dialogView.showPopup();

        dialogView.setActivity( new AbstractDialogDetailsActivity() {
            @Override
            public void onSaveClicked() {
                Window.Location.reload();
            }

            @Override
            public void onCancelClicked() {
                //ignore
            }
        } );
    }

    @Inject
    static En_ResultStatusLang resultStatusLang;
    @Inject
    static NotifyActivity activity;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    static Lang lang;
}
