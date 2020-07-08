package ru.protei.portal.ui.equipment.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.Objects;

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
        if (!hasPrivileges(event.id)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        view.setVisibilitySettingsForCreated(event.id != null);

        if (equipment != null) {
            fillView(equipment);
            return;
        }

        if (event.id == null) {
            fillView(new Equipment());
            return;
        }

        equipmentService.getEquipment(event.id, new FluentCallback<Equipment>()
                .withErrorMessage(lang.errGetList())
                .withSuccess(this::fillView)
        );
    }

    // Запил PORTAL-413
    // Кривая работа @ContextAware при использовании кнопок истории браузера (кнопка назад)
    // Сбрасываем @ContextAware при спуске на уровень ниже (таблица), при других уровнях работать не будет
    @Event
    public void onShowEquipmentsTable(EquipmentEvents.Show event) {
        equipment = null;
    }

    @Override
    public void onSaveClicked() {

        fillDTO(equipment);

        if (StringUtils.isBlank(equipment.getName())) {
            fireEvent(new NotifyEvents.Show(lang.errFieldsRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        if (equipment.getProjectId() == null) {
            fireEvent(new NotifyEvents.Show(lang.projectRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

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
                            fireEvent(new NotifyEvents.Show(lang.equipmentDecimalNumbeOccupied(), NotifyEvents.NotifyType.ERROR));
                            return;
                        }
                    }

                    defaultErrorHandler.accept(t);
                    fireEvent(new NotifyEvents.Show(t.getMessage(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(result -> {
                    fireEvent(new EquipmentEvents.ChangeModel());
                    fireEvent(new EquipmentEvents.Show(!isNew(this.equipment)));
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new EquipmentEvents.Show(!isNew(this.equipment)));
    }

    @Override
    public void onDecimalNumbersChanged() {}

    @Override
    public void onCreateDocumentClicked() {

        if (equipment == null || equipment.getId() == null) {
            return;
        }

        if (equipment.getProjectId() == null) {
            fireEvent(new NotifyEvents.Show(lang.projectRequired(), NotifyEvents.NotifyType.INFO));
            return;
        }

        Long projectId = equipment.getProjectId();
        List<String> decimalNumbers = DecimalNumberFormatter.formatNumbersWithoutModification(equipment.getDecimalNumbers());

        if (CollectionUtils.isEmpty(decimalNumbers)) {
            fireEvent(new NotifyEvents.Show(lang.decimalNumbersRequired(), NotifyEvents.NotifyType.INFO));
            return;
        }

        fillDTO(equipment);

        fireEvent(new DocumentEvents.CreateWithEquipment(equipment.getId(), projectId, equipment.getProjectName()));
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
        view.setNumbers(equipment.getDecimalNumbers(), isCreate);

        EquipmentShortView linkedEquipment = null;
        if ( equipment.getLinkedEquipmentId() != null ) {
            linkedEquipment = new EquipmentShortView( null, equipment.getLinkedEquipmentId(), equipment.getLinkedEquipmentDecimalNumbers() );
        }
        view.linkedEquipment().setValue( linkedEquipment );

        PersonShortView manager = null;
        if ( equipment.getManagerId() != null ) {
            manager = new PersonShortView();
            manager.setId( equipment.getManagerId() );
            manager.setName( equipment.getManagerShortName() );
        }
        view.manager().setValue( manager );

        Project info = null;
        if (equipment.getProjectId() != null) {
            info = new Project();
            info.setId(equipment.getProjectId());
            info.setName(equipment.getProjectName());
        }
        view.project().setValue( info == null ? null : new EntityOption(info.getName(), info.getId()));

        view.createDocumentButtonEnabled().setEnabled(!isCreate);
        view.documentsVisibility().setVisible(!isCreate);

        view.setLinkedEquipmentFilter(isCreate ?
                        (equipmentShortView -> true) :
                        (equipmentShortView -> !Objects.equals(equipmentShortView.getId(), equipment.getId()))
        );

        fireEvent(new EquipmentEvents.ShowDocumentList(view.documents(), equipment.getId()));
    }

    private void fillDTO(Equipment equipment) {
        equipment.setNameSldWrks(view.nameSldWrks().getValue());
        equipment.setName(view.name().getValue());
        equipment.setComment(view.comment().getValue());
        equipment.setType(view.type().getValue());
        equipment.setDecimalNumbers(view.getNumbers());
        if (view.linkedEquipment().getValue() == null) {
            equipment.setLinkedEquipmentId(null);
            equipment.setLinkedEquipmentDecimalNumbers(null);
        } else {
            equipment.setLinkedEquipmentId(view.linkedEquipment().getValue().getId());
            equipment.setLinkedEquipmentDecimalNumbers(view.linkedEquipment().getValue().getDecimalNumbers());
        }
        if (view.manager().getValue() == null) {
            equipment.setManagerId(null);
            equipment.setManagerShortName(null);
        } else {
            equipment.setManagerId(view.manager().getValue().getId());
            equipment.setManagerShortName(view.manager().getValue().getName());
        }
        if (view.project().getValue() == null) {
            equipment.setProjectId(null);
            equipment.setProjectName(null);
        } else {
            equipment.setProjectId(view.project().getValue().getId());
            equipment.setProjectName(view.project().getValue().getDisplayText());
        }
    }

    private boolean isNew(Equipment equipment) {
        return equipment.getId() == null;
    }

    private boolean hasPrivileges(Long equipmentId) {
        if (equipmentId == null && policyService.hasPrivilegeFor(En_Privilege.EQUIPMENT_CREATE)) {
            return true;
        }

        if (equipmentId != null && policyService.hasPrivilegeFor(En_Privilege.EQUIPMENT_EDIT)) {
            return true;
        }

        return false;
    }

    @Inject
    AbstractEquipmentEditView view;

    @Inject
    Lang lang;

    @ContextAware
    Equipment equipment;

    @Inject
    EquipmentControllerAsync equipmentService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails initDetails;
}
