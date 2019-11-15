package ru.protei.portal.core.model.struct;

public class CaseObjectInfo {
    private Long id;
    private String name;
    private String info;

    public CaseObjectInfo(Long id, String name, String info) {
        this.id = id;
        this.name = name;
        this.info = info;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }
}
