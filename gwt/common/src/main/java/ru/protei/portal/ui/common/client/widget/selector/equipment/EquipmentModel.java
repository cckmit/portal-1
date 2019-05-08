package ru.protei.portal.ui.common.client.widget.selector.equipment;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class EquipmentModel extends LifecycleSelectorModel<EquipmentShortView> {

    @Event
    public void onInit(AuthEvents.Success event) {
        clear();
    }

    @Event
    public void onEquipmentListChanged(EquipmentEvents.ChangeModel event) {
        refreshOptions();
    }

    @Override
    protected void refreshOptions() {
        EquipmentQuery query = makeQuery();
        equipmentService.equipmentOptionList(query, new FluentCallback<List<EquipmentShortView>>()
                .withErrorMessage(lang.errGetList())
                .withSuccess(this::notifySubscribers));
    }

    private EquipmentQuery makeQuery() {
        EquipmentQuery query = new EquipmentQuery();
        if (projectId != null) {
            query.setProjectId(projectId);
        }
        if (types != null) {
            query.setTypes(types);
        } else {
            query.setTypes(defaultEquipmentTypes);
        }
        return query;
    }

    public void setVisibleTypes(Set<En_EquipmentType> types) {
        this.types = types;
        refreshOptions();
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
        refreshOptions();
    }

    @Inject
    EquipmentControllerAsync equipmentService;
    @Inject
    Lang lang;

    private Long projectId = null;
    private Set<En_EquipmentType> types = null;
    private Set<En_EquipmentType> defaultEquipmentTypes = new HashSet<>(
            Arrays.asList(En_EquipmentType.ASSEMBLY_UNIT, En_EquipmentType.COMPLEX, En_EquipmentType.PRODUCT)
    );
}
