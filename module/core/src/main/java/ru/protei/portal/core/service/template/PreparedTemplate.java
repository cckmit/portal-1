package ru.protei.portal.core.service.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

/**
 * Предварительно подготовленный шаблон (почти готовая модель данных)
 */
public class PreparedTemplate {
    private static Logger logger = LoggerFactory.getLogger(PreparedTemplate.class);

    private Configuration templateConfiguration;
    private Map< String, Object > model;

    private String nameTemplate = "notification/email/crm.body.%s.ftl";

    public PreparedTemplate( String nameTemplate ) {
        this.nameTemplate = nameTemplate;
    }

    public void setTemplateConfiguration( Configuration templateConfiguration ) {
        this.templateConfiguration = templateConfiguration;
    }

    public void setModel( Map< String, Object > model ) {
        this.model = model;
    }

    public String getText( String receiver, String lang, boolean isShowPrivacy ) {
        Writer writer = new StringWriter();

        if ( lang == null ) {
            lang = "ru";
        }

        try {
            model.put( "userName", receiver );
            model.put( "showPrivacy", isShowPrivacy );
            Template template = templateConfiguration.getTemplate( String.format( nameTemplate, lang ), Locale.forLanguageTag( lang ) );
            template.process( model, writer );
            return writer.toString();
        } catch ( Exception e ) {
            logger.error("unable to create text by template", e);
        }

        return null;
    }
}
