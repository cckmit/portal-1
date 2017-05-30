package ru.protei.portal.core.service.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import ru.protei.portal.core.model.ent.Person;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

/**
 * Created by shagaleev on 30/05/17.
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

    public String getText( Person receiver, String lang ) {
        Writer writer = new StringWriter();

        try {
            model.put( "userName", receiver.getDisplayName() );
            Template template = templateConfiguration.getTemplate( "crm.body.ftl", Locale.forLanguageTag( lang ) );
            template.process( model, writer );
            return writer.toString();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return null;
    }
}
