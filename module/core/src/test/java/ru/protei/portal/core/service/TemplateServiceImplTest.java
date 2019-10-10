package ru.protei.portal.core.service;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.PortalConfigTestConfiguration;
import ru.protei.portal.config.RendererTestConfiguration;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.portal.core.service.template.PreparedTemplate;
import ru.protei.portal.core.service.template.TemplateService;
import ru.protei.portal.core.service.template.TemplateServiceImpl;
import ru.protei.portal.core.utils.LinkData;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.portal.test.service.CaseCommentServiceTest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.protei.portal.core.utils.WorkTimeFormatter.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PortalConfigTestConfiguration.class, RendererTestConfiguration.class,
        TemplateServiceImplTest.ContextConfiguration.class})
public class TemplateServiceImplTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        public TemplateService getTemplateService() {
            return new TemplateServiceImpl();
        }
    }

    @Test
    public void escapeTextComment_ReplaceLineBreaks() {
        String result = htmlRenderer.plain2html( commentTextWithBreaks, En_TextMarkup.MARKDOWN, false );
        assertEquals( commentTextWithBreaksFormatted, result );
    }

    @Test
    public void  getCrmEmailNotificationBodyTest() throws Exception    {
        assertNotNull(templateService);
        Company company = CaseCommentServiceTest.createNewCompany(new CompanyCategory(2L));
        Person person = CaseCommentServiceTest.createNewPerson(company);
        CaseObject initState = createNewCaseObject(person, 2 * DAY + 3 * HOUR + 21 * MINUTE);
        CaseObject lastState = createNewCaseObject(person, 4 * DAY + 15 * HOUR + 48 * MINUTE);

        Object dummyCaseService = new Object();
        AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent(dummyCaseService, initState, lastState, person);
        List<CaseComment> comments = Collections.EMPTY_LIST;


        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(
                assembledCaseEvent, comments, null, "url", Collections.EMPTY_LIST
        );

        assertNotNull(bodyTemplate);

        NotificationEntry entry =  createNewNotificationEntry();

        String body = bodyTemplate.getText( entry.getAddress(), entry.getLangCode(), true );

        assertNotNull(body);
    }

    @Test
    public void  crmLinksToTasks() throws Exception    {

        Company company = CaseCommentServiceTest.createNewCompany(new CompanyCategory(2L));
        Person person = CaseCommentServiceTest.createNewPerson(company);
        CaseObject initState = BaseServiceTest.createNewCaseObject( person );
        CaseObject lastState = BaseServiceTest.createNewCaseObject( person );

        Object dummyCaseService = new Object();
        AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent(dummyCaseService, initState, lastState, person);
        List<CaseComment> comments = Collections.EMPTY_LIST;

        DiffCollectionResult<LinkData> linkData = new DiffCollectionResult<>();
        linkData.putSameEntry( new LinkData( "http://youtrak/PG-101", "PG-101" ) );
        linkData.putSameEntry( new LinkData( "http://crm/102", "102" ) );

        linkData.putAddedEntry( new LinkData( "http://youtrak/PG-201", "PG-201" ) );
        linkData.putAddedEntry( new LinkData( "http://crm/202", "202" ) );

        linkData.putRemovedEntry( new LinkData( "http://youtrak/PG-301", "PG-301" ) );
        linkData.putRemovedEntry( new LinkData( "http://crm/202", "302" ) );

        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(
                assembledCaseEvent, comments, linkData, "url", Collections.EMPTY_LIST
        );

        assertNotNull(bodyTemplate);

        NotificationEntry entry =  createNewNotificationEntry();

        String body = bodyTemplate.getText( entry.getAddress(), entry.getLangCode(), true );

        assertNotNull("Expected html from template", body);

        Document docFromTemplate = Jsoup.parse( body );
        assertNotNull("Expected parsed Html from template", docFromTemplate);

        docFromTemplate.outputSettings().prettyPrint( true );
        Element elementById = docFromTemplate.getElementById( "test-linkedTasks" );

        assertNotNull("Expected <tr> with linked tasks html element", elementById);

        String fileContent = getFileContent( "crm.body.linksOnTasks.html" );
        Document bodyFragment = Jsoup.parse( fileContent, "", Parser.xmlParser() ); //html парсер всегда добавляет html и head узлы
        bodyFragment.outputSettings().prettyPrint( true );
        String etalonHtml = bodyFragment.outerHtml();

        assertEquals( "Expected links to tasks content:", etalonHtml,  elementById.outerHtml());
    }

    private NotificationEntry createNewNotificationEntry() {
        NotificationEntry notificationEntry = new NotificationEntry();
        notificationEntry.setAddress("notificationEntry Address");
        notificationEntry.setContactItemType(En_ContactItemType.EMAIL);
        notificationEntry.setLangCode("ru");
        return notificationEntry;
    }

    private CaseObject createNewCaseObject(Person person, Long timeElapsed) {
        CaseObject caseObject = CaseCommentServiceTest.createNewCaseObject(person);
        caseObject.setTimeElapsed(timeElapsed);
        caseObject.setImpLevel(En_ImportanceLevel.BASIC.getId());

        return caseObject;
    }

    private String getFileContent( String fileName ) {

        String result = "";

        ClassLoader classLoader = getClass().getClassLoader();
        String aPackage = getClass().getPackage().getName().replace( ".", File.separator );
        try {
            String s = aPackage + File.separator + fileName;
            result = IOUtils.toString( classLoader.getResourceAsStream( s ) );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    @Autowired
    TemplateService templateService;
    @Autowired
    HTMLRenderer htmlRenderer;

    private String commentTextWithBreaks = " ```\n" +
            "ls -l\n" +
            "total 38999\n" +
            "-rw-rw-rw 1 user \n" +
            "```\n" +
            "перенос \n" +
            "строки\n" +
            " работает \\\n" +
            "как-то \n" +
            "так";

    private String commentTextWithBreaksFormatted = "<pre><code>ls -l\n" +
            "total 38999\n" +
            "-rw-rw-rw 1 user \n" +
            "</code></pre>\n" +
            "<p>перенос<br/>строки<br/>работает <br />\n" +
            "как-то<br/>так</p>\n";


}