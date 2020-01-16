package ru.protei.portal.core.model.youtrack.dto;

import ru.protei.portal.core.model.youtrack.annotation.YtFieldName;

public class YtDtoWithFieldNameAnnotation extends YtDto {
    public String a;
    @YtFieldName("b") public String hello;
    public String c;
}
