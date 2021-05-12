package ru.protei.portal.ui.delivery.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;
import java.util.Set;

/**
 * Абстракция вида карточки создания/редактирования проекта
 */
public interface AbstractDeliveryMetaView extends IsWidget {

    void setActivity(AbstractDeliveryMetaActivity activity);

    HasValue<CaseState> state();

    HasValue<En_DeliveryType> type();

    HasValue<ProjectInfo> project();

    void setCustomerCompany(String value);

    void setCustomerType(String value);

    void updateInitiatorModel(Long companyId);

    HasValue<PersonShortView> initiator();

    HasEnabled initiatorEnable();

    void setManagerCompany(String value);

    void setManager(String value);

    HasValue<En_DeliveryAttribute> attribute();

    HasValue<EntityOption> contract();

    HasEnabled contractEnable();

    void setContractFieldMandatory(boolean isMandatory);

    void updateContractModel(Long projectId);

    void setProducts(String value);

    HasValue<Date> departureDate();

    boolean isDepartureDateEmpty();

    void setDepartureDateValid(boolean isValid);

    void setSubscribers(Set<Person> persons);

    Set<Person> getSubscribers();
}
