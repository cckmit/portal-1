package ru.protei.portal.core.nrpe;

import ru.protei.winter.core.utils.enums.HasId;

public enum NRPEStatus implements HasId {
    HOST_REACHABLE(0),
    HOST_UNREACHABLE(1),
    SERVER_UNAVAILABLE(2),
    INCORRECT_PARAMS(3),
    ;

    private final int id;

    NRPEStatus(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public static NRPEStatus find(int id) {
        for (NRPEStatus as : NRPEStatus.values())
            if (as.id == id)
                return as;

        return null;
    }
}
