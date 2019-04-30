package ru.protei.portal.core.model.struct;

import java.io.Serializable;

public class Base64Facade implements Serializable {

    private String base64;
    private String name;
    private String type;
    private Long size;

    public Base64Facade() {}

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
