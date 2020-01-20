package ru.protei.portal.core.model.youtrack.dto;

import ru.protei.portal.core.model.youtrack.annotation.YtAlwaysInclude;

public class YtDtoWithAlwaysIncludeAnnotation extends YtDto {
    public String a;
    public String b;
    @YtAlwaysInclude
    public String c;
}
