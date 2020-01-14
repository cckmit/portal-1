package ru.protei.portal.core.model.youtrack.dto;

import ru.protei.portal.core.model.youtrack.annotation.YtCustomSubclasses;

public class YtDtoWithCustomSubclassesAnnotation extends YtDto {
    public String a;
    public String b;
    @YtCustomSubclasses({ YtInner1.class, YtInner2.class })
    public YtDto c;

    public static class YtInner1 extends YtInner11 {
        public String aa;
        public String cc;
    }

    public static class YtInner11 extends YtDto {
        public String dd;
    }

    public static class YtInner2 extends YtDto {
        public String bb;
    }
}
