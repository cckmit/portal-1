package ru.protei.portal.core.model.dict;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Состояния договоров
 */
public enum En_ContractState {
    /**
     * согласование
     */
    AGREEMENT(1),

    /**
     * есть оригинал
     */
    HAVE_ORIGINAL(2),

    /**
     * есть скан, 2 экз. направлены Заказчику
     */
    COPIES_SEND_TO_CUSTOMER(3),

    /**
     * есть скан, ждем 2 экз. от Заказчика
     */
    WAITING_COPIES_FROM_CUSTOMER(4),

    /**
     * ожидание оригинала от заказчика
     */
    WAIT_ORIGINAL(5),

    /**
     * отменен
     */
    CANCELLED(6),
    ;

    @JsonCreator
    public static En_ContractState getById(Integer id) {
        if(id == null)
            return null;

        for (En_ContractState cs : En_ContractState.values())
            if (cs.id == id)
                return cs;

        return null;
    }

    En_ContractState(int id) {
        this.id = id;
    }

    @JsonValue
    public int getId() {
        return id;
    }

    private int id;
}
