package ru.protei.portal.ui.web.client.model;

/**
 * Sync with `packages/app/portal/src/js/model/Unit.ts`
 */
public enum TsWebUnit {
    test1("test1"),
    test2("test2"),
    ;

    private String name;

    TsWebUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
