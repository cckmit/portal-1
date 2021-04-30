package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.helper.StringUtils;

public class DeliveryQuery extends BaseQuery {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                StringUtils.isNotEmpty(name);
    }

    @Override
    public String toString() {
        return "DeliveryQuery{" +
                "name=" + name +
                '}';
    }
}
