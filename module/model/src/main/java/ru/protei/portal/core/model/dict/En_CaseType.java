package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

/**
 * Created by michael on 19.05.16.
 */
public enum En_CaseType implements HasId {
    BUG(1, "bug"),
    TASK(2, "task"),
    FREQ(3, "freq"),
    CRM_SUPPORT(4, "crm.sr"),
    CRM_MARKET(5, "crm.mr"),
    SYSADM(6, "sysadm"),
    PLAN(7, "plan"),
    ORDER(8, "order"),
    PROJECT(9, "project"),
    OFFICIAL(10, "official"),
    EMPLOYEE_REGISTRATION(11, "employee-reg"),
    CONTRACT(12, "contract"),
    SF_PLATFORM(13, "sf-platform"),
    DELIVERY(14, "delivery"),
    KIT(15, "kit"),
    MODULE(16, "module"),
    CARD_BATCH(17, "card-batch"),
    CARD(18, "card")
    ;

    En_CaseType (int id, String code) {
        this.id = id;
        this.code = code;
    }

    private final int id;
    private final String code;

    @Override
    public int getId() {
        return id;
    }

    public String makeGUID(long objId) {
        return this.code + "." + objId;
    }

    public static En_CaseType find (int id) {
        for (En_CaseType ct : En_CaseType.values())
            if (ct.id == id)
                return ct;

        return null;
    }
}
