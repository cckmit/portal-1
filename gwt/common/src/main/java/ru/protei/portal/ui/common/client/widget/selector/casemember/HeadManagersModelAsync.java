package ru.protei.portal.ui.common.client.widget.selector.casemember;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PersonEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

/**
 * Модель регионов
 */
public abstract class HeadManagersModelAsync extends BaseSelectorModel<PersonShortView>
        implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        clean();
    }

    @Event
    public void onPersonListChanged(PersonEvents.ChangePersonModel event) {
        clean();
    }

    @Override
    protected void requestData(LoadingHandler selector, String searchText ) {
        personController.getCaseMembersList(En_DevUnitPersonRoleType.HEAD_MANAGER, new FluentCallback<List<PersonShortView>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess( result -> updateElements( result, selector ) ));
    }

    @Inject
    PersonControllerAsync personController;

    @Inject
    Lang lang;
}
