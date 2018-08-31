package ru.protei.portal.ui.equipment.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.DefaultNotificationHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
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

    @Event(Type.FILL_CONTENT)
    public void onShow( EquipmentEvents.Edit event ) {
        view.setVisibilitySettingsForCreated(! (event.id == null));
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
        }else if(!view.isDecimalNumbersCorrect()){
            fireEvent( new NotifyEvents.Show( lang.equipmentDecimalNumberNotCorrect(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        equipmentService.saveEquipment(equipment, new FluentCallback<Equipment>()
                .withError(t -> {
                    if (t instanceof RequestFailedException) {
                        if (En_ResultStatus.ALREADY_EXIST_RELATED.equals(((RequestFailedException) t).status)) {
                            notification.accept(lang.equipmentDecimalNumbeOccupied(), NotifyEvents.NotifyType.ERROR);
                            return;
                        }
                    }

                    defaultErrorHandler.accept(t);
                    fireErrorMessage(t.getMessage());

                })
                .withSuccess(result -> {
                    fireEvent(new EquipmentEvents.ChangeModel());
                    fireEvent(new Back());
                })
        );
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
        equipment.setLinkedEquipmentId( view.linkedEquipment().getValue() == null ? null : view.linkedEquipment().getValue().getId() );
        equipment.setDecimalNumbers( view.getNumbers() );
        equipment.setManagerId( view.manager().getValue() == null ? null : view.manager().getValue().getId() );
        equipment.setProjectId( view.project().getValue() == null ? null : view.project().getValue().getId() );
        return equipment;
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onCreateDocumentClicked() {

        if (equipment == null || equipment.getProjectId() == null) {
            return;
        }

        String decimalNumber = getDecimalNumber(equipment);

        if (decimalNumber == null) {
            return;
        }

        fireEvent(new EquipmentEvents.DocumentEdit(equipment.getProjectId(), decimalNumber));
    }

    private void fillView(Equipment equipment) {
        this.equipment = equipment;

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        boolean isCreate = equipment.getId() == null;

        view.nameSldWrks().setValue( equipment.getNameSldWrks() );
        view.name().setValue( equipment.getName() );
        view.date().setValue(DateFormatter.formatDateTime(equipment.getCreated()));
        view.comment().setValue( equipment.getComment() );
        view.type().setValue( isCreate ? En_EquipmentType.DETAIL : equipment.getType() );
        EquipmentShortView linkedEquipment = null;
        if ( equipment.getLinkedEquipmentId() != null ) {
            linkedEquipment = new EquipmentShortView( null, equipment.getLinkedEquipmentId(), equipment.getLinkedEquipmentDecimalNumbers() );
        }
        view.linkedEquipment().setValue( linkedEquipment );
        view.setNumbers(equipment.getDecimalNumbers(), isCreate);

        PersonShortView manager = null;
        if ( equipment.getManagerId() != null ) {
            manager = new PersonShortView();
            manager.setId( equipment.getManagerId() );
            manager.setDisplayShortName( equipment.getManagerShortName() );
        }
        view.manager().setValue( manager );

        ProjectInfo info = null;
        if ( equipment.getProjectId() == null ) {
            info = new ProjectInfo();
            info.setId(equipment.getProjectId());
            info.setName(equipment.getProjectName());
        }
        view.project().setValue( info );

        if (isCreate) {
            view.createDocumentButtonEnabled().setEnabled(false);
            view.documentsVisibility().setVisible(false);
        } else {
            String decimalNumber = getDecimalNumber(equipment);
            view.decimalNumber().setValue(decimalNumber);
            if (decimalNumber == null) {
                view.createDocumentButtonEnabled().setEnabled(false);
                view.documentsVisibility().setVisible(false);
            } else {
                view.createDocumentButtonEnabled().setEnabled(true);
                view.documentsVisibility().setVisible(true);
                fireEvent(new EquipmentEvents.ShowDocumentList(view.documents(), decimalNumber));
            }
        }
    }

    private String getDecimalNumber(Equipment equipment) {

        if (equipment == null || CollectionUtils.isEmpty(equipment.getDecimalNumbers())) {
            return null;
        }

        DecimalNumber decimalNumber = equipment.getDecimalNumbers().stream()
                .filter(dn -> !dn.isReserve())
                .findFirst()
                .orElse(null);

        if (decimalNumber == null) {
            return null;
        }

        return DecimalNumberFormatter.formatNumberWithoutModification(decimalNumber);
    }

    @Inject
    AbstractEquipmentEditView view;

    @Inject
    Lang lang;

    Equipment equipment;

    @Inject
    EquipmentControllerAsync equipmentService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    DefaultNotificationHandler notification;


    private AppEvents.InitDetails initDetails;
}
