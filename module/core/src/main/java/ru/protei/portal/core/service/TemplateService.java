package ru.protei.portal.core.service;

import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.template.PreparedTemplate;

import java.util.Collection;
import java.util.List;

/**
 * Сервис формирования шаблонов
 */
public interface TemplateService {
    PreparedTemplate getCrmEmailNotificationBody(
            AssembledCaseEvent caseObject, List< CaseComment > caseComments, String urlTemplate, Collection< String > recipients );

    PreparedTemplate getCrmEmailNotificationSubject( CaseObject caseObject, Person currentPerson );
}
