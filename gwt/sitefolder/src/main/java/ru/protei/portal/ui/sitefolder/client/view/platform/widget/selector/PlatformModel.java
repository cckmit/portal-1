package ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderPlatformEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.client.widget.components.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.widget.components.client.selector.impl.BaseSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class PlatformModel extends BaseSelectorModel<PlatformOption> implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        clean();
    }

    @Event
    public void onPlatformListChanged(SiteFolderPlatformEvents.ChangeModel event) {
        clean();
    }

    @Override
    protected void requestData( LoadingHandler selector, String searchText ) {
        siteFolderController.getPlatformsOptionList(new PlatformQuery(), new FluentCallback<List<PlatformOption>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess( result -> updateElements( result, selector ) ));
    }

    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    Lang lang;
}
