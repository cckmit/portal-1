package ru.protei.portal.core.service;

import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.service.template.PreparedTemplate;

import java.util.List;

/**
 * Сервис формирования шаблонов
 */
public interface TemplateService {
    PreparedTemplate getCrmEmailNotificationBody( CaseObject caseObject, List<CaseComment> caseComments );
}
