package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_DutyType implements HasId {

    // дежурный 112 (безопасный город)
    BG(1),
    // дежурный ИП
    IP(2),
    // дежурный Биллинга
    BILLING(3),
    // дежурный Mobile
    MOBILE(4),
    // дежурный DPI
    DPI(5),
    // дежурный МКСП/ВКС
    MKSP_VKS(6),
    // дежурный NGN
    NGN(7),
    // дежурный СОРМ
    SORM(8),
    // дежурный ЦОВ
    COV(9),
    // дежурный IMS
    IMS(10);

    En_DutyType(int id) {
        this.id = id;
    }

    private final int id;
    public int getId() { return id; }

    public static En_DutyType byId(int id) {
        for (En_DutyType type : En_DutyType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }
}
