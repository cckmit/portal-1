package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_NRPEStatus implements HasId {
    HOST_REACHABLE(0),
    HOST_UNREACHABLE(1),
    SERVER_UNAVAILABLE(2),
    INCORRECT_PARAMS(3),
    ;

    private final int id;

    En_NRPEStatus(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public static En_NRPEStatus find(int id) {
        for (En_NRPEStatus as : En_NRPEStatus.values())
            if (as.id == id)
                return as;

        return null;
    }
}
