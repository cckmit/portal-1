package ru.protei.portal.core.model.dict;

import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.HasFilterQueryIds;

import java.util.ArrayList;
import java.util.List;

public enum En_DeliveryFilterType {
    /**
     * Фильтр по поставкам
     */
    DELIVERY_OBJECTS(DeliveryQuery.class);

    En_DeliveryFilterType(Class<? extends HasFilterQueryIds> queryClass) {
        this.queryClass = queryClass;
    }

    public Class<? extends HasFilterQueryIds> getQueryClass() {
        return queryClass;
    }

    public static List<En_DeliveryFilterType> getTypesByClass(Class<? extends HasFilterQueryIds> queryClass) {
        List<En_DeliveryFilterType> result = new ArrayList<En_DeliveryFilterType>();

        for (En_DeliveryFilterType nextType : values()) {
            if (nextType.getQueryClass().equals(queryClass)) {
                result.add(nextType);
            }
        }

        return result;
    }

    private final Class<? extends HasFilterQueryIds> queryClass;
}
