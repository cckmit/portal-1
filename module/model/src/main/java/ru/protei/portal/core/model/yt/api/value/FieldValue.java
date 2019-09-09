package ru.protei.portal.core.model.yt.api.value;

import ru.protei.portal.core.model.yt.api.YoutrackObject;

public abstract class FieldValue extends YoutrackObject {
    public String localizedName;
    public String name;
    public abstract String getValue();
}
