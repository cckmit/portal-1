package ru.protei.portal.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.service.template.PreparedTemplate;
import ru.protei.portal.core.utils.WorkTimeFormatter;
import ru.protei.portal.test.service.CaseCommentServiceTest;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static ru.protei.portal.core.utils.WorkTimeFormatter.DAY;
import static ru.protei.portal.core.utils.WorkTimeFormatter.HOUR;
import static ru.protei.portal.core.utils.WorkTimeFormatter.MINUTE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, MainTestsConfiguration.class})
public class TemplateServiceImplTest {

    @Autowired
    TemplateService templateService;
    @Autowired
    CaseService caseService;

    @Test
    public void  getCrmEmailNotificationBodyTest() throws Exception    {
        assertNotNull(templateService);
        Company company = CaseCommentServiceTest.createNewCompany(new CompanyCategory(2L));
        Person person = CaseCommentServiceTest.createNewPerson(company);
        CaseObject initState = createNewCaseObject(company, person, 2 * DAY + 3 * HOUR + 21 * MINUTE);
        CaseObject lastState = createNewCaseObject(company, person, 4 * DAY + 15 * HOUR + 48 * MINUTE);

        AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent(caseService, initState, lastState, person);
        List<CaseComment> comments = Collections.EMPTY_LIST;


        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(
                assembledCaseEvent, comments, "url", Collections.EMPTY_LIST
        );

        assertNotNull(bodyTemplate);

        NotificationEntry entry =  createNewNotificationEntry();

        String body = bodyTemplate.getText( entry.getAddress(), entry.getLangCode(), true );

        assertNotNull(body);
    }

    private NotificationEntry createNewNotificationEntry() {
        NotificationEntry notificationEntry = new NotificationEntry();
        notificationEntry.setAddress("notificationEntry Address");
        notificationEntry.setContactItemType(En_ContactItemType.EMAIL);
        notificationEntry.setLangCode("ru");
        return notificationEntry;
    }

    private CaseObject createNewCaseObject(Company company, Person person, Long timeElapsed) {
        CaseObject caseObject = CaseCommentServiceTest.createNewCaseObject(person);
        caseObject.setCaseNumber(111L);
        caseObject.setTimeElapsed(timeElapsed);
        caseObject.setImpLevel(En_ImportanceLevel.BASIC.getId());

        return caseObject;
    }

}