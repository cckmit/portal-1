package ru.protei.portal.ui.common.client.widget.selector.cardbatch;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.CardBatchControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

public abstract class CardBatchModel extends BaseSelectorModel<CardBatch> implements Activity {
    public boolean isTypePresent() {
        return cardType != null;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void updateCardType(CardType cardType ) {
        this.cardType = cardType;
        clean();
    }

    @Override
    protected void requestData(LoadingHandler selector, String searchText) {
        controllerAsync.getListCardBatchByType(cardType, new RequestCallback<List<CardBatch>>() {

            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<CardBatch> options) {
                updateElements(options, selector);
            }
        });
    }

    @Inject
    CardBatchControllerAsync controllerAsync;
    @Inject
    Lang lang;

    private CardType cardType;
}
