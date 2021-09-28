package ru.protei.portal.ui.delivery.client.activity.card.create;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;

/**
 * Абстракция вида карточки создания Платы
 */
public interface AbstractCardCreateView extends IsWidget {

    void setActivity(AbstractCardCreateActivity activity);

    HasEnabled saveEnabled();

    HasValue<String> serialNumber();

    HasValue<String> note();

    HasValue<String> comment();

    HasValue<CaseState> state();

    HasValue<CardType> type();

    HasValue<String> article();

    HasValue<PersonShortView> manager();

    HasValue<Date> testDate();
}
