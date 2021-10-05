package ru.protei.portal.ui.delivery.client.activity.card.meta;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.cardbatch.CardBatchModel;

import java.util.Date;

public interface AbstractCardMetaView extends IsWidget {
    void setCreateActivity(AbstractCardCreateMetaActivity activity);

    void setEditActivity(AbstractCardEditMetaActivity activity);

    HasValue<CaseState> state();

    HasValue<CardType> type();

    HasEnabled typeEnable();

    HasValue<CardBatch> cardBatch();

    HasEnabled cardBatchEnable();

    CardBatchModel cardBatchModel();

    HasValue<String> article();

    boolean articleIsValid();

    HasValue<PersonShortView> manager();

    HasValue<Date> testDate();

    boolean isTestDateEmpty();

    void setTestDateValid(boolean isValid);
}
