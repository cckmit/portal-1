package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.dict.En_ExpiringProjectTSVPeriod;
import ru.protei.portal.core.model.dto.ProjectTSVReportInfo;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;
import java.util.Map;

public class ExpiringProjectTSVNotificationEvent extends ApplicationEvent {

    private final Map<En_ExpiringProjectTSVPeriod, List<ProjectTSVReportInfo>> infos;
    private final Person headManager;

    public ExpiringProjectTSVNotificationEvent(Object source, Person headManager,
                   Map<En_ExpiringProjectTSVPeriod, List<ProjectTSVReportInfo>> infos) {
        super(source);
        this.infos = infos;
        this.headManager = headManager;
    }

    public Map<En_ExpiringProjectTSVPeriod, List<ProjectTSVReportInfo>> getInfos() {
        return infos;
    }

    public Person getHeadManager() {
        return headManager;
    }
}
