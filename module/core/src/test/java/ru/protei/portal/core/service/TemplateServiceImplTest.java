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
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.event.CaseObjectMetaEvent;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_ExtAppType;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.protei.portal.core.event.AssembledEventFactory.makeAssembledEvent;
import static ru.protei.portal.core.event.AssembledEventFactory.makeComment;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
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
    public void getCrmEmailNotificationBodyTest() {
        assertNotNull( templateService );
        Company company = CaseCommentServiceTest.createNewCompany( new CompanyCategory( 2L ) );
        Person person = CaseCommentServiceTest.createNewPerson( company );
        CaseObject initState = createNewCaseObject( person, 2 * DAY + 3 * HOUR + 21 * MINUTE );
        CaseObject lastState = createNewCaseObject( person, 4 * DAY + 15 * HOUR + 48 * MINUTE );

        Object dummyCaseService = new Object();
        CaseObjectEvent caseObjectEvent = new CaseObjectEvent( dummyCaseService, ServiceModule.GENERAL, person.getId(), initState, lastState );
        CaseObjectMetaEvent caseObjectMetaEvent = new CaseObjectMetaEvent( dummyCaseService, ServiceModule.GENERAL, person.getId(), En_ExtAppType.forCode(initState.getExtAppType()), new CaseObjectMeta(initState), new CaseObjectMeta(lastState) );
        AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent( caseObjectEvent );
        assembledCaseEvent.attachCaseObjectEvent( caseObjectEvent );
        assembledCaseEvent.attachCaseObjectMetaEvent( caseObjectMetaEvent );

        List<CaseComment> comments = Collections.EMPTY_LIST;


        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(
                assembledCaseEvent, comments, null, "url", Collections.EMPTY_LIST
        );

        assertNotNull( bodyTemplate );

        NotificationEntry entry = createNewNotificationEntry();

        String body = bodyTemplate.getText( entry.getAddress(), entry.getLangCode(), true );

        assertNotNull( body );
    }

    @Test
    public void crmLinksToTasks() {

        Company company = CaseCommentServiceTest.createNewCompany( new CompanyCategory( 2L ) );
        Person person = CaseCommentServiceTest.createNewPerson( company );
        CaseObject initState = BaseServiceTest.createNewCaseObject( person );
        CaseObject lastState = BaseServiceTest.createNewCaseObject( person );

        Object dummyCaseService = new Object();
        CaseObjectEvent caseObjectEvent = new CaseObjectEvent( dummyCaseService, ServiceModule.GENERAL, person.getId(), initState, lastState );
        CaseObjectMetaEvent caseObjectMetaEvent = new CaseObjectMetaEvent( dummyCaseService, ServiceModule.GENERAL, person.getId(), En_ExtAppType.forCode(initState.getExtAppType()), new CaseObjectMeta(initState), new CaseObjectMeta(lastState) );
        AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent( caseObjectEvent );
        assembledCaseEvent.attachCaseObjectEvent( caseObjectEvent );
        assembledCaseEvent.attachCaseObjectMetaEvent( caseObjectMetaEvent );

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

        assertNotNull( bodyTemplate );

        NotificationEntry entry = createNewNotificationEntry();

        String body = bodyTemplate.getText( entry.getAddress(), entry.getLangCode(), true );

        assertNotNull( "Expected html from template", body );

        Document docFromTemplate = Jsoup.parse( body );
        assertNotNull( "Expected parsed Html from template", docFromTemplate );

        docFromTemplate.outputSettings().prettyPrint( true );
        Element elementById = docFromTemplate.getElementById( "test-linkedTasks" );

        assertNotNull( "Expected <tr> with linked tasks html element", elementById );

        String fileContent = getFileContent( "crm.body.linksOnTasks.html" );
        Document bodyFragment = Jsoup.parse( fileContent, "", Parser.xmlParser() ); //html парсер всегда добавляет html и head узлы
        bodyFragment.outputSettings().prettyPrint( true );
        String etalonHtml = bodyFragment.outerHtml();

        assertEquals( "Expected links to tasks content:", etalonHtml, elementById.outerHtml() );
    }

    @Test
    public void comments() throws Exception {
        Company company = CaseCommentServiceTest.createNewCompany( new CompanyCategory( 2L ) );
        Person person = CaseCommentServiceTest.createNewPerson( company );
        CaseObject lastState = BaseServiceTest.createNewCaseObject( person );

        List<CaseComment> old = listOf( makeComment("old1", parseDate( "01-01-2019 12:30:31" )), makeComment("old2", parseDate( "01-01-2019 12:30:32" )) );
        CaseComment chang1 = makeComment( "chang1", parseDate( "01-01-2019 12:30:40" ) );
        CaseComment chang2 = makeComment( "chang2", parseDate( "01-01-2019 12:30:41" ) );
        CaseComment rem1 = makeComment( "rem1", parseDate( "01-01-2019 12:30:42" ) );
        CaseComment rem2 = makeComment( "rem2", parseDate( "01-01-2019 12:30:43" ) );
        CaseComment add1 = makeComment( "add1", parseDate( "01-01-2019 12:30:44" ) );
        CaseComment add2 = makeComment( "add2", parseDate( "01-01-2019 12:30:45" ) );

        CaseComment chang1new = makeComment("chang1new", parseDate( "01-01-2019 12:30:50" ) );
        chang1new.setId( chang1.getId() );
        chang1new.setText( "update chang1" );
        CaseComment chang2new = makeComment("chang2new", parseDate( "01-01-2019 12:30:51" ) );
        chang2new.setId( chang2.getId() );
        chang2new.setText( "update chang2" );

        List<CaseComment> existing = new ArrayList<>( old );
        existing.add(chang1new);
        existing.add(chang2new);
        existing.add( add1 );
        existing.add( add2 );

        old.add( chang1 );
        old.add( chang2 );

        CaseObjectEvent caseObjectEvent = new CaseObjectEvent( new Object(), ServiceModule.GENERAL, person.getId(), null, lastState );
        CaseObjectMetaEvent caseObjectMetaEvent = new CaseObjectMetaEvent( new Object(), ServiceModule.GENERAL, person.getId(), En_ExtAppType.forCode(lastState.getExtAppType()), null, new CaseObjectMeta(lastState) );
        AssembledCaseEvent assembled = new AssembledCaseEvent( caseObjectEvent );
        assembled.attachCaseObjectEvent( caseObjectEvent );
        assembled.attachCaseObjectMetaEvent( caseObjectMetaEvent );

        assembled.setExistingCaseComments( existing );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, chang1, chang1new, rem1 ) );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, chang2, chang2new, rem2 ) );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, null, add1, null ) );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, null, add2, null ) );

        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(
                assembled, assembled.getAllComments(), null, "url", Collections.EMPTY_LIST
        );
        assertNotNull( bodyTemplate );
        NotificationEntry entry = createNewNotificationEntry();

        String body = bodyTemplate.getText( entry.getAddress(), entry.getLangCode(), true );

        assertNotNull( "Expected html from template", body );

        Document docFromTemplate = Jsoup.parse( body );
        assertNotNull( "Expected parsed Html from template", docFromTemplate );

        docFromTemplate.outputSettings().prettyPrint( false );
        Element elementById = docFromTemplate.getElementById( "test-case-comments" );

        assertNotNull( "Expected <tr> with linked tasks html element", elementById );

        String fileContent = getFileContent( "crm.body.CaseComments.html" );
        Document bodyFragment = Jsoup.parse( fileContent, "", Parser.xmlParser() ); //html парсер всегда добавляет html и head узлы
        bodyFragment.outputSettings().prettyPrint( false );
        String etalonHtml = bodyFragment.outerHtml();

        assertEquals( "Comments are not equal to the template:", etalonHtml, elementById.outerHtml() );
    }

    private Date parseDate( String dateString ) throws ParseException {
        return new SimpleDateFormat( "dd-MM-yyyy HH:mm:ss" ).parse( dateString );
    }

    private NotificationEntry createNewNotificationEntry() {
        NotificationEntry notificationEntry = new NotificationEntry();
        notificationEntry.setAddress( "notificationEntry Address" );
        notificationEntry.setContactItemType( En_ContactItemType.EMAIL );
        notificationEntry.setLangCode( "ru" );
        return notificationEntry;
    }

    private CaseObject createNewCaseObject( Person person, Long timeElapsed ) {
        CaseObject caseObject = CaseCommentServiceTest.createNewCaseObject( person );
        caseObject.setTimeElapsed( timeElapsed );
        caseObject.setImpLevel( En_ImportanceLevel.BASIC.getId() );

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