package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.dict.En_ExpiringTechnicalSupportValidityPeriod;
import ru.protei.portal.core.model.dto.ProjectTechnicalSupportValidityReportInfo;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;
import java.util.Map;

public class ExpiringTechnicalSupportValidityNotificationEvent extends ApplicationEvent {

    private final Map<En_ExpiringTechnicalSupportValidityPeriod, List<ProjectTechnicalSupportValidityReportInfo>> infos;
    private final Person headManager;

    public ExpiringTechnicalSupportValidityNotificationEvent(Object source,
                                                             Person headManager,
                                                             Map<En_ExpiringTechnicalSupportValidityPeriod, List<ProjectTechnicalSupportValidityReportInfo>> infos) {
        super(source);
        this.infos = infos;
        this.headManager = headManager;
    }

    public Map<En_ExpiringTechnicalSupportValidityPeriod, List<ProjectTechnicalSupportValidityReportInfo>> getInfos() {
        return infos;
    }

    public Person getHeadManager() {
        return headManager;
    }
}
