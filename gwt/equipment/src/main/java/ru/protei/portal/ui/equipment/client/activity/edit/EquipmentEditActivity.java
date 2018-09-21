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
import ru.protei.portal.core.model.helper.StringUtils;
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

import java.util.ArrayList;
import java.util.List;

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

        view.setVisibilitySettingsForCreated(event.id != null);

        if (event.id == null) {
            fillView(new Equipment());
            return;
        }

        equipmentService.getEquipment(event.id, new FluentCallback<Equipment>()
                .withErrorMessage(lang.errGetList())
                .withSuccess(this::fillView)
        );
    }

    @Override
    public void onSaveClicked() {

        fillDTO(equipment);

        if (equipment.getDecimalNumbers() == null || equipment.getDecimalNumbers().isEmpty()) {
            fireEvent(new NotifyEvents.Show(lang.equipmentDecimalNumberNotDefinied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        if (!view.isDecimalNumbersCorrect()) {
            fireEvent(new NotifyEvents.Show(lang.equipmentDecimalNumberNotCorrect(), NotifyEvents.NotifyType.ERROR));
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

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onDecimalNumbersChanged() {
        String decimalNumberOld = view.decimalNumbersForDocuments().getValue();
        List<String> numbers = getDecimalNumbersWithoutModification(view.getNumbers());
        if (numbers.isEmpty()) {
            numbers.add(null);
        }
        view.setDecimalNumbersForDocuments(numbers);
        if (decimalNumberOld != null && numbers.contains(decimalNumberOld)) {
            view.decimalNumbersForDocuments().setValue(decimalNumberOld, false);
        } else {
            view.decimalNumbersForDocuments().setValue(numbers.get(0), true);
        }
    }

    @Override
    public void onDecimalNumberForDocumentsSelected() {
        String decimalNumber = view.decimalNumbersForDocuments().getValue();
        boolean isEdit = !isNew(equipment);
        boolean isEnabled = decimalNumber != null && isEdit;
        view.createDocumentButtonEnabled().setEnabled(isEnabled);
        view.documentsVisibility().setVisible(isEnabled);
        view.decimalNumbersForDocumentsEnabled().setEnabled(isEdit);
        if (isEnabled) {
            fireEvent(new EquipmentEvents.ShowDocumentList(view.documents(), decimalNumber));
        }
    }

    @Override
    public void onCreateDocumentClicked() {

        if (equipment == null || equipment.getProjectId() == null) {
            return;
        }

        String decimalNumberWithoutModification = view.decimalNumbersForDocuments().getValue();

        if (decimalNumberWithoutModification == null) {
            return;
        }

        fireEvent(new EquipmentEvents.DocumentEdit(equipment.getProjectId(), decimalNumberWithoutModification));
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

        onDecimalNumbersChanged();
    }

    private void fillDTO(Equipment equipment) {
        equipment.setNameSldWrks(view.nameSldWrks().getValue());
        equipment.setName(view.name().getValue());
        equipment.setComment(view.comment().getValue());
        equipment.setType(view.type().getValue());
        equipment.setLinkedEquipmentId(view.linkedEquipment().getValue() == null ? null : view.linkedEquipment().getValue().getId());
        equipment.setDecimalNumbers(view.getNumbers());
        equipment.setManagerId(view.manager().getValue() == null ? null : view.manager().getValue().getId());
        equipment.setProjectId(view.project().getValue() == null ? null : view.project().getValue().getId());
    }

    private boolean isNew(Equipment equipment) {
        return equipment.getId() == null;
    }

    private List<String> getDecimalNumbersWithoutModification(List<DecimalNumber> decimalNumbers) {

        List<String> result = new ArrayList<>();

        if (decimalNumbers == null) {
            return result;
        }

        decimalNumbers.forEach(decimalNumber -> {
            String number = DecimalNumberFormatter.formatNumberWithoutModification(decimalNumber);
            if (StringUtils.isNotBlank(number)) {
                result.add(number);
            }
        });

        return result;
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
