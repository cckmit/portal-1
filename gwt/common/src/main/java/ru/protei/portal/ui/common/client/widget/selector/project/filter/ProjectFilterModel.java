package ru.protei.portal.ui.common.client.widget.selector.project.filter;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.view.filterwidget.AbstractFilterShortView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class ProjectFilterModel extends BaseSelectorModel<AbstractFilterShortView> implements Activity {
    @Override
    protected void requestData(LoadingHandler selector, String searchText) {
        controller.getIssueFilterShortViewList(En_CaseFilterType.PROJECT, new FluentCallback<List<AbstractFilterShortView>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(list -> updateElements(list, selector))
        );
    }

    @Inject
    IssueFilterControllerAsync controller;
    @Inject
    Lang lang;
}
