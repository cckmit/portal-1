package ru.protei.portal.ui.equipment.client.activity.edit;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentServiceAsync;

/**
 * Активность карточки редактирования единицы оборудования
 */
public abstract class EquipmentEditActivity
        implements Activity,
        AbstractEquipmentEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( EquipmentEvents.Edit event ) {
        if( event.id == null ) {
            Equipment newEquipment = new Equipment();
            fillView(newEquipment);
            return;
        }

        equipmentService.getEquipment(event.id, new AsyncCallback<Equipment>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireErrorMessage(lang.errGetList());
            }

            @Override
            public void onSuccess(Equipment person) {
                fillView(person);
            }
        });
    }

    @Override
    public void onSaveClicked() {
//        if (!validate ()) {
//            return;
//        }

        equipmentService.saveEquipment(applyChanges(), new AsyncCallback<Equipment>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireErrorMessage(throwable.getMessage());
            }

            @Override
            public void onSuccess(Equipment person) {
                fireEvent(new Back());
            }
        });
    }

    private boolean fireErrorMessage( String msg) {
        fireEvent( new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
        return false;
    }

    private Equipment applyChanges () {
        return equipment;
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }


    private void fillView(Equipment person){
        this.equipment = person;

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }


    @Inject
    AbstractEquipmentEditView view;

    @Inject
    Lang lang;

    Equipment equipment;

    @Inject
    EquipmentServiceAsync equipmentService;

    private AppEvents.InitDetails initDetails;
}
