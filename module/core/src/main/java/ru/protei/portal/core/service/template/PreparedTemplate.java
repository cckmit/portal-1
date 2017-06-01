package ru.protei.portal.core.service.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import ru.protei.portal.core.model.ent.Person;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

/**
 * Предварительно подготовленный шаблон (почти готовая модель данных)
 */
public class PreparedTemplate {
    private Configuration templateConfiguration;
    private Map< String, Object > model;

    public void setTemplateConfiguration( Configuration templateConfiguration ) {
        this.templateConfiguration = templateConfiguration;
    }

    public void setModel( Map< String, Object > model ) {
        this.model = model;
    }

    public String getText( String receiver, String lang ) {
        Writer writer = new StringWriter();

        if ( lang == null ) {
            lang = "ru";
        }

        try {
            model.put( "userName", receiver );
            Template template = templateConfiguration.getTemplate( "notification/email/crm.body."+lang+".ftl" );
            template.process( model, writer );
            return writer.toString();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return null;
    }
}
