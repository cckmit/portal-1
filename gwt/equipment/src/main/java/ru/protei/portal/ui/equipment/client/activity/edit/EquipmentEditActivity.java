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
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.equipment.client.common.EquipmentUtils;

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
        equipment.setNameSldWrks( view.nameBySldWrks().getValue() );
        equipment.setName( view.nameBySpecification().getValue() );
        equipment.setComment( view.comment().getValue() );
        equipment.setType( view.type().getValue() );
        equipment.setStage( view.stage().getValue() );
        equipment.setLinkedEquipment( view.primaryUse().getValue() );

        DecimalNumber pamrNumber = view.pamrNumber().getValue();
        DecimalNumber pdraNumber = view.pdraNumber().getValue();

        if ( pamrNumber != null ) {
            equipment.setClassifierCode( pamrNumber.getClassifierCode() );
            String num = pamrNumber.getRegisterNumber();
            if ( pamrNumber.getModification() != null ) {
                num += "-" + pamrNumber.getModification();
            }
            equipment.setPAMR_RegisterNumber( num );
        }

        if ( pdraNumber != null ) {
            equipment.setClassifierCode( pdraNumber.getClassifierCode() );
            String num = pdraNumber.getRegisterNumber();
            if ( pdraNumber.getModification() != null ) {
                num += "-" + pdraNumber.getModification();
            }
            equipment.setPDRA_RegisterNumber( num );
        }

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

        view.nameBySldWrks().setValue( equipment.getNameSldWrks() );
        view.nameBySpecification().setValue( equipment.getName() );
        view.comment().setValue( equipment.getComment() );
        view.type().setValue( equipment.getType() );
        view.stage().setValue( equipment.getStage() );
        view.primaryUse().setValue( equipment.getLinkedEquipment() );

        boolean isCreate = equipment.getId() == null;
        view.nameBySpecificationEnabled().setEnabled( isCreate );

        DecimalNumber pamrNumber = !isCreate && equipment.getPAMR_RegisterNumber() != null
                ? EquipmentUtils.getDecimalNumberByStringValues( En_OrganizationCode.PAMR, equipment.getClassifierCode(), equipment.getPAMR_RegisterNumber() )
                : null;
        view.pamrNumber().setValue( pamrNumber );

        DecimalNumber pdraNumber = !isCreate && equipment.getPDRA_RegisterNumber() != null
                ? EquipmentUtils.getDecimalNumberByStringValues( En_OrganizationCode.PDRA, equipment.getClassifierCode(), equipment.getPDRA_RegisterNumber() )
                : null ;
        view.pdraNumber().setValue( pdraNumber );

        view.pamrNumberEnabled().setEnabled( isCreate );
        view.pdraNumberEnabled().setEnabled( isCreate );
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
