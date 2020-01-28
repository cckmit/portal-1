package ru.protei.portal.core.model.youtrack.dto;

import ru.protei.portal.core.model.youtrack.annotation.YtEntityName;

@YtEntityName("OverrideName")
public class YtDtoWithEntityNameAnnotation extends YtDto {
    public String a;
    public String b;
}
