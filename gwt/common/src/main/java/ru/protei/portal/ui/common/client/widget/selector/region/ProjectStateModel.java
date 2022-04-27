package ru.protei.portal.ui.common.client.widget.selector.region;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

/**
 * Модель состояния проекта
 */
public abstract class ProjectStateModel extends BaseSelectorModel<CaseState> implements Activity {

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        clean();
    }

    @Override
    protected void requestData(LoadingHandler selector, String searchText) {
        caseStateController.getCaseStatesOmitPrivileges(En_CaseType.PROJECT, new FluentCallback<List<CaseState>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(caseStates -> updateElements(caseStates, selector)));
    }

    @Inject
    Lang lang;
    @Inject
    CaseStateControllerAsync caseStateController;
}
