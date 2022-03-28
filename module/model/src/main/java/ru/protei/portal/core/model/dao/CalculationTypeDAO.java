package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CalculationType;

import java.util.List;
import java.util.Set;

public interface CalculationTypeDAO extends PortalBaseDAO<CalculationType> {

    CalculationType getCalculationTypeByRefKey(String refKey);

    List<CalculationType> getCalculationTypeListByRefKeys(Set<String> refKeys);
}
