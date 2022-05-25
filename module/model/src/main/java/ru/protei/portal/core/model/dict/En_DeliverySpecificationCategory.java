package ru.protei.portal.core.model.dict;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import ru.protei.winter.core.utils.enums.HasId;

public enum En_DeliverySpecificationCategory implements HasId {
    /**
     * Корпусная комплектация, разрабатываемая нами
     */
    OWN_ENCLOSURE(1),

    /**
     * - Корпусная комплектация покупная - термопрокладки,
     * резинки, световоды и подобное
     */
    PURCHASED_ENCLOSURE(2),

    /**
     * Метизы
     */
    SUNDRIES(3),

    /**
     * Электронная комплектация - печатные платы,
     * видеокамеры, экраны
     */
    ELECTRICAL(4),

    /**
     * Кабели и кабельная комплектация
     */
    CABLES(5),

    /**
     * Электронная комплектация печатных плат
     * (применять будем для комплектации печатных плат)
     */
    PCB(9),
    ;

    private final int id;

    En_DeliverySpecificationCategory(int id) {
        this.id = id;
    }

    @Override
    @JsonValue
    public int getId() {
        return id;
    }

    @JsonCreator
    public static En_DeliverySpecificationCategory find(int id) {
        for (En_DeliverySpecificationCategory as : En_DeliverySpecificationCategory.values())
            if (as.id == id)
                return as;

        return null;
    }
}
