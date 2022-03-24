package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CalculationType;

import java.util.List;
import java.util.Set;

public interface CalculationTypeDAO extends PortalBaseDAO<CalculationType> {

    CalculationType getCalculationTypeBy(String refKey);

    List<CalculationType> getCalculationTypesListBy(Set<String> refKeys);
}
