package ru.protei.portal.ui.common.client.widget.selector.equipment;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;

public abstract class EquipmentModel implements Activity, SelectorModel<EquipmentShortView> {

    @Event
    public void onEmployeeListChanged( EquipmentEvents.ChangeModel event ) {
        refreshOptions();
    }

    public void subscribe(SelectorWithModel<EquipmentShortView> selector, Long projectId, Set<En_EquipmentType> equipmentTypes) {
        SubscriberToken token = new SubscriberToken(projectId, equipmentTypes);
        if (!subscribersMap.containsKey(token)) {
            subscribersMap.put(token, new ArrayList<>());
            subscribersMap.get(token).add(selector);
            if (selector.isAttached()) {
                refreshOptionsForToken(token);
            }
        } else {
            subscribersMap.get(token).add(selector);
            selector.fillOptions(valuesMap.get(token));
        }
    }

    @Override
    public void onSelectorLoad(SelectorWithModel<EquipmentShortView> selector) {
        if (selector == null) {
            return;
        }
        if (CollectionUtils.isEmpty(selector.getValues())) {
            refreshOptions();
        }
    }

    @Override
    public void onSelectorUnload(SelectorWithModel<EquipmentShortView> selector) {
        if (selector == null) {
            return;
        }
        selector.clearOptions();
    }

    private void refreshOptions() {
        subscribersMap.forEach((token, subscribers) -> refreshOptionsForToken(token));
    }

    private void refreshOptionsForToken(SubscriberToken token) {
        EquipmentQuery query = makeQuery(token);
        equipmentService.equipmentOptionList(query, new FluentCallback<List<EquipmentShortView>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(options -> {
                    valuesMap.put(token, options);
                    notifySubscribers(token);
                }));
    }

    private EquipmentQuery makeQuery(SubscriberToken token) {
        EquipmentQuery query = new EquipmentQuery();
        if (token.projectId != null) {
            query.setProjectId(token.projectId);
        }
        if (token.equipmentTypes != null) {
            query.setTypes(token.equipmentTypes);
        } else {
            query.setTypes(defaultEquipmentTypes);
        }
        return query;
    }

    private void notifySubscribers(SubscriberToken token) {
        List<SelectorWithModel<EquipmentShortView>> subscribers = subscribersMap.get(token);
        List<EquipmentShortView> values = valuesMap.get(token);
        for (SelectorWithModel<EquipmentShortView> selector : subscribers) {
            selector.fillOptions(values);
            selector.refreshValue();
        }
    }

    @Inject
    EquipmentControllerAsync equipmentService;
    @Inject
    Lang lang;

    private Set<En_EquipmentType> defaultEquipmentTypes = new HashSet<>(
            Arrays.asList( En_EquipmentType.ASSEMBLY_UNIT, En_EquipmentType.COMPLEX, En_EquipmentType.PRODUCT)
    );
    private Map<SubscriberToken, List<SelectorWithModel<EquipmentShortView>>> subscribersMap = new HashMap<>();
    private Map<SubscriberToken, List<EquipmentShortView>> valuesMap = new HashMap<>();

    private class SubscriberToken {

        private Long projectId;
        private Set<En_EquipmentType> equipmentTypes;

        private SubscriberToken() {
            this(null, null);
        }

        public SubscriberToken(Long projectId) {
            this(projectId, null);
        }

        public SubscriberToken(Set<En_EquipmentType> equipmentTypes) {
            this(null, equipmentTypes);
        }

        private SubscriberToken(Long projectId, Set<En_EquipmentType> equipmentTypes) {
            this.projectId = projectId;
            this.equipmentTypes = equipmentTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SubscriberToken that = (SubscriberToken) o;
            return Objects.equals(projectId, that.projectId) &&
                    Objects.equals(equipmentTypes, that.equipmentTypes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(projectId, equipmentTypes);
        }
    }
}
