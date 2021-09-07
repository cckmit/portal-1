package ru.protei.portal.ui.common.client.widget.selector.report.youtrackwork;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.YoutrackProject;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.YoutrackControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class ReportYtWorkYtProjectMultiModel extends BaseSelectorModel<YoutrackProject> implements Activity {

    @Override
    protected void requestData(LoadingHandler selector, String searchText) {
        controller.getProjects(0, 1000, new FluentCallback<List<YoutrackProject>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(list -> updateElements(list, selector))
        );
    }

    @Inject
    YoutrackControllerAsync controller;
    @Inject
    Lang lang;
}
