package ru.protei.portal.ui.common.client.widget.selector.card.type;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.CardControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class CardTypeModel extends BaseSelectorModel<CardType> implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        clean();
    }

    @Override
    protected void requestData( LoadingHandler selector, String searchText ) {
        cardController.getCardTypeList(new FluentCallback<List<CardType>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess( result -> updateElements( result, selector ) ));
    }

    @Inject
    CardControllerAsync cardController;
    @Inject
    Lang lang;
}
