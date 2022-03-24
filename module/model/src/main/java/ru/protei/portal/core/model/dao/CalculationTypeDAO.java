package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CalculationType;

public interface CalculationTypeDAO extends PortalBaseDAO<CalculationType> {

    CalculationType getCalculationTypeBy(String refKey);
}
