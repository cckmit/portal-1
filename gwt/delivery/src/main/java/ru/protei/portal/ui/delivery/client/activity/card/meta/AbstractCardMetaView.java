package ru.protei.portal.ui.delivery.client.activity.card.meta;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;

public interface AbstractCardMetaView extends IsWidget {
    void setActivity(AbstractCardMetaActivity activity);

    HasValue<CaseState> state();

    HasValue<CardType> type();

    HasValue<String> article();

    HasValue<PersonShortView> manager();

    HasValue<Date> testDate();

    boolean isTestDateEmpty();

    void setTestDateValid(boolean isValid);

    void setAllowChangingState(boolean isAllow);
}
