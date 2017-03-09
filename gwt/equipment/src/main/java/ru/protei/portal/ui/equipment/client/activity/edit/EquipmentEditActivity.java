package ru.protei.portal.ui.equipment.client.activity.edit;


import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.view.PersonShortView;
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
            public void onSuccess(Equipment equipment) {
                fillView(equipment);
            }
        });
    }

    @Override
    public void onSaveClicked() {
        Equipment equipment = applyChanges();
        if ( equipment.getDecimalNumbers() == null || equipment.getDecimalNumbers().isEmpty() ) {
            fireEvent( new NotifyEvents.Show( lang.equipmentDecimalNumberNotDefinied(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        equipmentService.saveEquipment(equipment, new RequestCallback<Equipment>() {
            @Override
            public void onError(Throwable throwable) {
                fireErrorMessage(throwable.getMessage());
            }

            @Override
            public void onSuccess(Equipment equipment) {
                fireEvent( new EquipmentEvents.ChangeModel() );
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
        equipment.setManagerId( view.manager().getValue() == null ? null : view.manager().getValue().getId() );
        equipment.setProject( view.project().getValue() );
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

        boolean isCreate = equipment.getId() == null;

        view.nameSldWrks().setValue( equipment.getNameSldWrks() );
        view.name().setValue( equipment.getName() );
        view.comment().setValue( equipment.getComment() );
        view.type().setValue( isCreate ? En_EquipmentType.DETAIL : equipment.getType() );
        view.stage().setValue( equipment.getStage() );
        view.linkedEquipment().setValue( new Equipment( equipment.getLinkedEquipmentId() ) );
        view.numbers().setValue( equipment.getDecimalNumbers() );
        PersonShortView manager = new PersonShortView();
        manager.setId( equipment.getManagerId() );
        view.manager().setValue( manager );
        view.project().setValue( equipment.getProject() );

        view.nameEnabled().setEnabled( isCreate );
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
