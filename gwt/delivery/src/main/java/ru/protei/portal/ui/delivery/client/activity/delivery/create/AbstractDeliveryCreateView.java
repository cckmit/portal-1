package ru.protei.portal.ui.delivery.client.activity.delivery.create;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.delivery.client.view.delivery.meta.DeliveryMetaView;

import java.util.Date;
import java.util.Set;

/**
 * Абстракция вида карточки создания Поставки
 */
public interface AbstractDeliveryCreateView extends IsWidget {

    void setActivity(AbstractDeliveryCreateActivity activity);

    HasEnabled saveEnabled();

    HasValue<String> name();

    HasValue<String> description();

    DeliveryMetaView  getMetaView();

    HasValue<CaseState> state();

    HasValue<En_DeliveryType> type();

    HasValue<ProjectInfo> project();

    HasValue<PersonShortView> initiator();

    HasValue<En_DeliveryAttribute> attribute();

    HasValue<ContractInfo> contract();

    HasValue<Date> departureDate();

    void setDepartureDateValid(boolean isValid);

    void setSubscribers(Set<Person> persons);

    Set<Person> getSubscribers();

    HasWidgets getKitsContainer();
}
