package ru.protei.portal.config;

import freemarker.template.*;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.protei.portal.core.Lang;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Создает локализованные шаблоны через Freemarker.
 */
public class LocalizedTemplateCreator {

    private static final Locale[] LOCALES = new Locale[]{Locale.ENGLISH, Locale.forLanguageTag("ru")};

    /**
     * Create localized templates
     * @param templates template paths
     */
    public static void main(String[] templates) throws Exception {
        if(templates.length == 0)
            return;

        Lang keys = getLang();
        String basePackagePath = Paths.get(LocalizedTemplateCreator.class.getResource("/").toURI()).toFile().getAbsolutePath();

        Map<Locale, Object> models = new HashMap<>(LOCALES.length);
        for (Locale locale : LOCALES) {
            models.put(locale, getModel(keys.getFor(locale)));
        }

        Configuration templateConfiguration = new Configuration( Configuration.VERSION_2_3_23 );
        templateConfiguration.setClassForTemplateLoading( LocalizedTemplateCreator.class, "/" );
        templateConfiguration.setDefaultEncoding( "UTF-8" );
        templateConfiguration.setTemplateExceptionHandler( TemplateExceptionHandler.RETHROW_HANDLER );

        try {
            for (String template: templates){
                createFor(basePackagePath, models, templateConfiguration.getTemplate(template, "UTF-8"));
            }
        }catch (IOException | TemplateException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Creates a file like "name.{lang}.ftl" for each locale
     * @param template template of the file with name like "name.ftl"
     * @param options specifying how the file is created
     *                By default:
     *                #{@link java.nio.file.StandardOpenOption#CREATE},
     *                #{@link java.nio.file.StandardOpenOption#TRUNCATE_EXISTING},
     *                #{@link java.nio.file.StandardOpenOption#WRITE},
     * @throws IOException
     * @throws TemplateException
     */
    private static void createFor(String basePackagePath, Map<Locale, Object> models, Template template, OpenOption... options) throws IOException, TemplateException{
        if(!template.getName().endsWith(".ftl"))
            throw new TemplateException("Name of template "+ template.getName() +" doesn't end with \".ftl\"", null);

        String baseTemplateName = template.getName().substring(0, template.getName().length() - 3);
        for(Map.Entry<Locale, Object> langToModel: models.entrySet()){
            Path path = Paths.get(
                basePackagePath, baseTemplateName + langToModel.getKey().getLanguage() + ".ftl"
            );
            try(Writer writer = Files.newBufferedWriter(path, options)) {
                template.process(langToModel.getValue(), writer);
            }
            System.out.println("Template "+ path.getFileName() +" created!");
        }
    }

    private static Lang getLang(){
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("Lang");
        messageSource.setDefaultEncoding("UTF-8");
        return new Lang(messageSource);
    }

    private static Object getModel(Lang.LocalizedLang messages){
        return new TemplateHashModel(){
            @Override
            public TemplateModel get(String key) throws TemplateModelException {
                return new SimpleScalar(messages.get(key));
            }

            @Override
            public boolean isEmpty() throws TemplateModelException {
                return false;
            }
        };
    }
}
