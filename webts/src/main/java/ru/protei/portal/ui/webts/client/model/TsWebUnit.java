package ru.protei.portal.ui.webts.client.model;

/**
 * Sync with `packages/app/portal/src/js/model/Unit.ts`
 */
public enum TsWebUnit {
    delivery("delivery"),
    ;

    private String name;

    TsWebUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
