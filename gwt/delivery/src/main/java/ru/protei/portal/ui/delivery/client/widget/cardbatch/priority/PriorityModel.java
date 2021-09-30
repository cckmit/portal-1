package ru.protei.portal.ui.delivery.client.widget.cardbatch.priority;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.ImportanceLevelControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PriorityModel extends BaseSelectorModel<ImportanceLevel> implements Activity {

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        clean();
    }

    public void fillOptions(List<ImportanceLevel> options) {
        updateElements(options);
    }

    protected void requestData(LoadingHandler selector, String searchText) {
        importanceService.getImportanceLevels(new FluentCallback<List<ImportanceLevel>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(levels -> updateElements(
                             levels.stream().filter(level -> CARD_BATCH_PRIORITY_IDS.contains(level.getId()))
                                            .collect(Collectors.toList()),
                             selector)));
    }

    @Inject
    Lang lang;
    @Inject
    ImportanceLevelControllerAsync importanceService;

    private static final List<Integer> CARD_BATCH_PRIORITY_IDS = new ArrayList<>(Arrays.asList(1, 2, 3, 8));
}
