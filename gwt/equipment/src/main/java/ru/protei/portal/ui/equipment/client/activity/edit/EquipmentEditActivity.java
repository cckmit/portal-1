package ru.protei.portal.ui.equipment.client.activity.edit;


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
import ru.protei.portal.ui.common.shared.model.RequestCallback;

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

        equipmentService.getEquipment(event.id, new RequestCallback<Equipment>() {
            @Override
            public void onError(Throwable throwable) {
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

        equipmentService.saveEquipment(applyChanges(), new RequestCallback<Equipment>() {
            @Override
            public void onError(Throwable throwable) {
                fireErrorMessage(throwable.getMessage());
            }

            @Override
            public void onSuccess(Equipment equipment) {
                fireEvent(new Back());
            }
        });
    }

    private boolean fireErrorMessage( String msg) {
        fireEvent( new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
        return false;
    }

    private Equipment applyChanges () {
        equipment.setNameSldWrks( view.nameSldWrks().getValue() );
        equipment.setName( view.name().getValue() );
        equipment.setComment( view.comment().getValue() );
        equipment.setType( view.type().getValue() );
        equipment.setStage( view.stage().getValue() );
        equipment.setLinkedEquipmentId( view.linkedEquipment().getValue() == null ? null : view.linkedEquipment().getValue().getId() );
        equipment.setDecimalNumbers( view.numbers().getValue() );

        return equipment;
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }


    private void fillView(Equipment equipment){
        this.equipment = equipment;

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        view.nameSldWrks().setValue( equipment.getNameSldWrks() );
        view.name().setValue( equipment.getName() );
        view.comment().setValue( equipment.getComment() );
        view.type().setValue( equipment.getType() );
        view.stage().setValue( equipment.getStage() );
        view.linkedEquipment().setValue( new Equipment( equipment.getLinkedEquipmentId() ) );

        boolean isCreate = equipment.getId() == null;
        view.nameEnabled().setEnabled( isCreate );
        view.numbers().setValue( equipment.getDecimalNumbers() );
        view.typeEnabled().setEnabled( isCreate );
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
