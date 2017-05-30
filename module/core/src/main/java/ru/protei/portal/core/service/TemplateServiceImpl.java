package ru.protei.portal.core.service;


import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
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
            templateConfiguration.setDirectoryForTemplateLoading( new File( URI.create( "classpath:notification" ) ) );
            templateConfiguration.setDefaultEncoding( "UTF-8" );
            templateConfiguration.setTemplateExceptionHandler( TemplateExceptionHandler.HTML_DEBUG_HANDLER );
        } catch ( Exception e ) {
            log.error( "Freemarker Configuration init failure", e );
            e.printStackTrace();
        }
    }

    @Override
    public PreparedTemplate getCrmEmailNotificationBody( CaseObject caseObject, List< CaseComment > caseComments ) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put( "linkToIssue", "#" );
        templateModel.put( "case", caseObject );
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
