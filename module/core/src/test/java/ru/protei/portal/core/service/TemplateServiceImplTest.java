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
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.PortalConfigTestConfiguration;
import ru.protei.portal.config.RendererTestConfiguration;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dao.EmployeeShortViewDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.util.DiffResult;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.portal.core.service.template.PreparedTemplate;
import ru.protei.portal.core.service.template.TemplateService;
import ru.protei.portal.core.service.template.TemplateServiceImpl;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.portal.core.utils.LinkData;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.portal.test.service.CaseCommentServiceTest;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.protei.portal.core.event.AssembledEventFactory.makeComment;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeDateWithOffset;
import static ru.protei.portal.core.utils.WorkTimeFormatter.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PortalConfigTestConfiguration.class,
        RendererTestConfiguration.class,
        TemplateServiceImplTest.ContextConfiguration.class})
public class TemplateServiceImplTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        public TemplateService getTemplateService() {
            return new TemplateServiceImpl();
        }
        @Bean
        public CaseStateDAO getStateDAO() {
            return mock( CaseStateDAO.class );
        }
        @Bean
        public EmployeeShortViewDAO getEmployeeShortViewDAO() {
            return mock( EmployeeShortViewDAO.class );
        }
        @Bean
        public Lang lang() {
            ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.setBasenames("Lang");
            messageSource.setDefaultEncoding("UTF-8");
            return new Lang(messageSource);
        }
    }

    @Test
    public void getBirthdayNotificationBodyTest() {
        assertNotNull( templateService );

        Date from = makeDateWithOffset(-2);
        Date to = makeDateWithOffset(9);

        List<EmployeeShortView> employees = Arrays.asList(
                createEmployee("Иванов Иван Иванович", makeDateWithOffset(1)),
                createEmployee("Петров Петр Петрович", makeDateWithOffset(4)),
                createEmployee("Сидоров Сидор Сидорович", makeDateWithOffset(1)),
                createEmployee("Анжела", makeDateWithOffset(5)),
                createEmployee("Кристина", makeDateWithOffset(14))
        );

        NotificationEntry entry = createNewNotificationEntry("frost@protei.ru");
        List<NotificationEntry> notifiers = Arrays.asList(entry);

        BirthdaysNotificationEvent event = new BirthdaysNotificationEvent( new Object(), employees, from, to, Arrays.asList(entry));

         assertNotNull( event );

        PreparedTemplate subjectTemplate = templateService.getBirthdaysNotificationSubject(from, to);

        LinkedHashMap<Date, TreeSet<EmployeeShortView>> dateToEmployeesMap = CollectionUtils.stream(event.getEmployees())
                .peek(employee -> employee.setBirthday(selectDateThisYear(employee.getBirthday())))
                .sorted(Comparator.comparing(EmployeeShortView::getBirthday))
                .collect(groupingBy(
                        EmployeeShortView::getBirthday,
                        LinkedHashMap::new,
                        Collectors.toCollection(() -> new TreeSet<>(
                                Comparator.comparing(EmployeeShortView::getDisplayName)
                        ))));

        List<DayOfWeek> dayOfWeeks = makeDaysOfWeek(dateToEmployeesMap);

        PreparedTemplate bodyTemplate = templateService.getBirthdaysNotificationBody(dateToEmployeesMap, dayOfWeeks,
                notifiers.stream().map(NotificationEntry::getAddress).collect( Collectors.toList()), new EnumLangUtil(lang));

        assertNotNull( subjectTemplate );
        assertNotNull( bodyTemplate );

        String subject = subjectTemplate.getText(entry.getAddress(), entry.getLangCode(), true);
        String body = bodyTemplate.getText(entry.getAddress(), entry.getLangCode(), true);

        assertNotNull( subject );
        assertNotNull( body );
    }

    @Test
    public void escapeTextComment_ReplaceLineBreaks() {
        String result = htmlRenderer.plain2html( commentTextWithBreaks, En_TextMarkup.MARKDOWN, false );
        assertEquals( commentTextWithBreaksFormatted, result );
    }

    @Test
    public void getCrmEmailNotificationBodyTest() {
        assertNotNull( templateService );
        Company company = CaseCommentServiceTest.createNewCompany( En_CompanyCategory.PARTNER );
        Person person = CaseCommentServiceTest.createNewPerson( company );
        CaseObject initState = createNewCaseObject( person, 2 * DAY + 3 * HOUR + 21 * MINUTE );
        CaseObject lastState = createNewCaseObject( person, 4 * DAY + 15 * HOUR + 48 * MINUTE );

        Object dummyCaseService = new Object();
        CaseNameAndDescriptionEvent caseNameAndDescriptionEvent = new CaseNameAndDescriptionEvent(
                dummyCaseService,
                initState.getId(),
                new DiffResult<>(initState.getName(), lastState.getName()),
                new DiffResult<>(initState.getInfo(), lastState.getInfo()),
                person.getId(),
                ServiceModule.JIRA,
                En_ExtAppType.JIRA);
        CaseObjectMetaEvent caseObjectMetaEvent = new CaseObjectMetaEvent( dummyCaseService, ServiceModule.GENERAL, person.getId(), En_ExtAppType.forCode(initState.getExtAppType()), new CaseObjectMeta(initState), new CaseObjectMeta(lastState) );
        AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent(caseNameAndDescriptionEvent);
        assembledCaseEvent.attachCaseNameAndDescriptionEvent(caseNameAndDescriptionEvent);
        assembledCaseEvent.attachCaseObjectMetaEvent(caseObjectMetaEvent);
        assembledCaseEvent.setLastCaseObject(lastState);

        List<CaseComment> comments = Collections.EMPTY_LIST;


        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(
                assembledCaseEvent, comments, new ArrayList<>(), null, "url",
                Collections.EMPTY_LIST, new EnumLangUtil(lang)
        );

        assertNotNull( bodyTemplate );

        NotificationEntry entry = createNewNotificationEntry();

        String body = bodyTemplate.getText( entry.getAddress(), entry.getLangCode(), true);

        assertNotNull( body );
    }

    @Test
    public void crmLinksToTasks() {

        Company company = CaseCommentServiceTest.createNewCompany(En_CompanyCategory.PARTNER );
        Person person = CaseCommentServiceTest.createNewPerson( company );
        CaseObject initState = BaseServiceTest.createNewCaseObject( person );
        CaseObject lastState = BaseServiceTest.createNewCaseObject( person );

        Object dummyCaseService = new Object();
        CaseNameAndDescriptionEvent caseNameAndDescriptionEvent = new CaseNameAndDescriptionEvent(
                dummyCaseService,
                initState.getId(),
                new DiffResult<>(initState.getName(), lastState.getName()),
                new DiffResult<>(initState.getInfo(), lastState.getInfo()),
                person.getId(),
                ServiceModule.JIRA,
                En_ExtAppType.JIRA);
        CaseObjectMetaEvent caseObjectMetaEvent = new CaseObjectMetaEvent( dummyCaseService, ServiceModule.GENERAL, person.getId(), En_ExtAppType.forCode(initState.getExtAppType()), new CaseObjectMeta(initState), new CaseObjectMeta(lastState) );
        AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent(caseNameAndDescriptionEvent);
        assembledCaseEvent.attachCaseNameAndDescriptionEvent(caseNameAndDescriptionEvent);
        assembledCaseEvent.attachCaseObjectMetaEvent(caseObjectMetaEvent);
        assembledCaseEvent.setLastCaseObject(lastState);

        List<CaseComment> comments = Collections.EMPTY_LIST;

        DiffCollectionResult<LinkData> linkData = new DiffCollectionResult<>();
        linkData.putSameEntry( new LinkData( "http://youtrak/PG-101", "PG-101" ) );
        linkData.putSameEntry( new LinkData( "http://crm/102", "102" ) );

        linkData.putAddedEntry( new LinkData( "http://youtrak/PG-201", "PG-201" ) );
        linkData.putAddedEntry( new LinkData( "http://crm/202", "202" ) );

        linkData.putRemovedEntry( new LinkData( "http://youtrak/PG-301", "PG-301" ) );
        linkData.putRemovedEntry( new LinkData( "http://crm/202", "302" ) );

        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(
                assembledCaseEvent, comments, new ArrayList<>(), linkData, "url",
                Collections.EMPTY_LIST, new EnumLangUtil(lang)
        );

        assertNotNull( bodyTemplate );

        NotificationEntry entry = createNewNotificationEntry();

        String body = bodyTemplate.getText( entry.getAddress(), entry.getLangCode(), true);

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
        Company company = CaseCommentServiceTest.createNewCompany( En_CompanyCategory.PARTNER );
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

        CaseObjectCreateEvent caseObjectCreateEvent = new CaseObjectCreateEvent( new Object(), ServiceModule.GENERAL, person.getId(), lastState);
        CaseObjectMetaEvent caseObjectMetaEvent = new CaseObjectMetaEvent(
                new Object(), ServiceModule.GENERAL, person.getId(), En_ExtAppType.forCode(lastState.getExtAppType()), null, new CaseObjectMeta(lastState) );
        AssembledCaseEvent assembled = new AssembledCaseEvent(caseObjectCreateEvent);
        assembled.attachCaseObjectCreateEvent(caseObjectCreateEvent);
        assembled.attachCaseObjectMetaEvent( caseObjectMetaEvent );

        assembled.setExistingCaseComments( existing );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, chang1, chang1new, rem1 ) );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, chang2, chang2new, rem2 ) );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, null, add1, null ) );
        assembled.attachCommentEvent( new CaseCommentEvent( this, null, null, null, false, null, add2, null ) );

        List<CaseState> caseStateList = new ArrayList<>();
        caseStateList.add(new CaseState(1L));
        when( caseStateDAO.getAllByCaseType( En_CaseType.CRM_SUPPORT ) ).thenReturn( caseStateList );

        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(
                assembled, assembled.getAllComments(), new ArrayList<>(), null, "url",
                Collections.EMPTY_LIST, new EnumLangUtil(lang)
        );
        assertNotNull( bodyTemplate );
        NotificationEntry entry = createNewNotificationEntry();

        String body = bodyTemplate.getText( entry.getAddress(), entry.getLangCode(), true);

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

    private EmployeeShortView createEmployee (String displayName, Date birthday) {
        EmployeeShortView employee = new EmployeeShortView();
        employee.setDisplayName(displayName);
        employee.setBirthday(birthday);
        return employee;
    }

    private NotificationEntry createNewNotificationEntry(String email) {
        return NotificationEntry.email(email, CrmConstants.DEFAULT_LOCALE);
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
        caseObject.setImpLevel( CrmConstants.ImportanceLevel.BASIC );

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

    private Date selectDateThisYear(Date date) {
        Date now = new Date();
        if (now.getMonth() == Calendar.DECEMBER && date.getMonth() == Calendar.JANUARY) {
            date.setYear(now.getYear() + 1);
        } else if (now.getMonth() == Calendar.JANUARY && date.getMonth() == Calendar.DECEMBER) {
            date.setYear(now.getYear() - 1);
        } else {
            date.setYear(now.getYear());
        }
        return date;
    }

    private List<DayOfWeek> makeDaysOfWeek(LinkedHashMap<Date, TreeSet<EmployeeShortView>> dateToEmployeesMap) {
        List<DayOfWeek> daysOfWeek = new ArrayList<>();

        Set<Date> dates = dateToEmployeesMap.keySet();
        for (Date date : dates) {
            Instant instant = date.toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            LocalDate localDate = zdt.toLocalDate();
            daysOfWeek.add(localDate.getDayOfWeek());
        }
        return daysOfWeek;
    }

    @Autowired
    TemplateService templateService;
    @Autowired
    HTMLRenderer htmlRenderer;
    @Autowired
    CaseStateDAO caseStateDAO;
    @Autowired
    Lang lang;

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
