package ru.protei.portal.core.service;


import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.template.PreparedTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Реализация сервиса управления проектами
 */
public class TemplateServiceImpl implements TemplateService {
    private static Logger log = LoggerFactory.getLogger(TemplateServiceImpl.class);

    Configuration templateConfiguration;

    @PostConstruct
    public void onInit() {
        try {
            templateConfiguration = new Configuration( Configuration.VERSION_2_3_23 );
            templateConfiguration.setClassLoaderForTemplateLoading( ClassLoader.getSystemClassLoader(), "notification/email" );
//            templateConfiguration.setDirectoryForTemplateLoading( new File( URI.create( "classpath:notification/" ) ) );
            templateConfiguration.setDefaultEncoding( "UTF-8" );
            templateConfiguration.setTemplateExceptionHandler( TemplateExceptionHandler.HTML_DEBUG_HANDLER );
        } catch ( Exception e ) {
            log.error( "Freemarker Configuration init failure", e );
            e.printStackTrace();
        }
    }

    @Override
    public PreparedTemplate getCrmEmailNotificationBody(
        CaseObjectEvent caseObjectEvent, List< CaseComment > caseComments, Person manager, Person oldManager
    ) {

        CaseObject oldState = caseObjectEvent.getOldState();

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put( "linkToIssue", "#" );
        templateModel.put( "createdByMe", false );
        templateModel.put( "case", caseObjectEvent.getCaseObject() );
        templateModel.put( "importanceLevel", En_ImportanceLevel.getById( caseObjectEvent.getCaseObject().getImpLevel() ).getCode() );
        templateModel.put( "manager", manager );
        templateModel.put( "caseState", En_CaseState.getById( caseObjectEvent.getCaseObject().getStateId() ).getName() );

        templateModel.put( "productChanged", caseObjectEvent.isProductChanged() );
        templateModel.put( "oldProductName", oldState == null ? null : oldState.getProduct().getName() );
        templateModel.put( "importanceChanged", caseObjectEvent.isCaseImportanceChanged() );
        templateModel.put( "oldImportanceLevel", oldState == null ? null : En_ImportanceLevel.getById( oldState.getImpLevel() ).getCode() );
        templateModel.put( "caseChanged", caseObjectEvent.isCaseStateChanged() );
        templateModel.put( "oldCaseState", oldState == null ? null : En_CaseState.getById( oldState.getStateId() ).getName() );
        templateModel.put( "customerChanged", caseObjectEvent.isInitiatorChanged() || caseObjectEvent.isInitiatorCompanyChanged() );
        templateModel.put( "oldInitiator", oldState == null ? null : oldState.getInitiator() );
        templateModel.put( "oldInitiatorCompany", oldState == null ? null : oldState.getInitiatorCompany() );
        templateModel.put( "managerChanged", caseObjectEvent.isManagerChanged() );
        templateModel.put( "oldManager", oldManager );
        templateModel.put( "infoChanged", caseObjectEvent.isInfoChanged() );

        templateModel.put( "caseComments",  caseComments.stream().map( ( comment ) -> {
            Map< String, Object > caseComment = new HashMap<>();
            caseComment.put( "created", comment.getCreated() );
            caseComment.put( "author", comment.getAuthor() );
            caseComment.put( "text", comment.getText() );
            caseComment.put( "caseState", En_CaseState.getById( comment.getCaseStateId() ) );
            return caseComment;
        } ).collect( toList() ) );

        PreparedTemplate template = new PreparedTemplate();
        template.setModel( templateModel );
        template.setTemplateConfiguration( templateConfiguration );
        return template;
    }
}
