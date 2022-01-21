package ru.protei.portal.core.model.dict;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /**
     * подписан на площадке
     */
    SIGNED_ON_SITE(7),

    /**
     * подписан ЭЦП
     */
    EDS_SIGNED(8);

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

    @JsonIgnore
    private static final List<En_ContractState> contractStatesByDefault;

    static {
        contractStatesByDefault = new ArrayList<>(Arrays.asList(values()));
        contractStatesByDefault.remove(CANCELLED);
    }

    private int id;

    @JsonValue
    public int getId() {
        return id;
    }

    @JsonIgnore
    public static List<En_ContractState> contractStatesByDefault() { return contractStatesByDefault; }

}
