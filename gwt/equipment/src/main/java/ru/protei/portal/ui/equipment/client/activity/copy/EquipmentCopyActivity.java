package ru.protei.portal.ui.equipment.client.activity.copy;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

/**
 * Активность диалога копирования оборудования
 */
public abstract class EquipmentCopyActivity
        implements Activity,
        AbstractEquipmentCopyActivity, AbstractDialogDetailsActivity
{
    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        dialogView.setActivity( this );

        dialogView.setHeader( lang.equipmentCopyHeader() );
        dialogView.getBodyContainer().add( view.asWidget() );
    }

    @Event
    public void onShow( EquipmentEvents.ShowCopyDialog event ) {
        this.show = event;
        view.name().setValue( null );
        dialogView.getDialogAnimation().show();
    }

    @Override
    public void onSaveClicked() {
        String title = view.name().getValue();
        if ( title == null || title.isEmpty() ) {
            fireEvent( new NotifyEvents.Show( lang.equipmentCopyNotFilledNewName() ) );
            return;
        }

        equipmentService.copyEquipment( show.id, title, new RequestCallback<Long>() {
            @Override
            public void onError( Throwable caught ) {}

            @Override
            public void onSuccess( Long result ) {
                fireEvent( new NotifyEvents.Show( lang.equpmentCopySuccess(), NotifyEvents.NotifyType.SUCCESS ) );
                fireEvent( new EquipmentEvents.Show() );
                dialogView.getDialogAnimation().hide();
            }
        } );
    }

    @Override
    public void onCancelClicked() {
        dialogView.getDialogAnimation().hide();
    }

    EquipmentEvents.ShowCopyDialog show;

    @Inject
    Lang lang;
    @Inject
    AbstractEquipmentCopyView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    EquipmentServiceAsync equipmentService;
}
