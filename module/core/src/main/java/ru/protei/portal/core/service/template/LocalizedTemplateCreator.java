package ru.protei.portal.core.service.template;

import freemarker.template.*;
import ru.protei.portal.core.Lang;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Создает локализованные шаблоны через Freemarker.
 */
public class LocalizedTemplateCreator {

    private String basePackagePath;
    private Map<Locale, Object> models;

    /**
     * Привязка ключей через бандлы.
     * #{@link ru.protei.portal.config.MainConfiguration#lang}
     * @param basePackagePath path to save templates
     */
    public LocalizedTemplateCreator(String basePackagePath, Lang keys, Locale... locales) {
        this.basePackagePath = basePackagePath;
        models = new HashMap<>(locales.length);
        for (Locale locale : locales) {
            models.put(locale, getModel(keys.getFor(locale)));
        }
    }

    /**
     * @param basePackagePath path to save templates
     * @param langToModels map of locale with the suitable template model
     */
    public LocalizedTemplateCreator(String basePackagePath, Map<Locale, Object> langToModels) {
        this.basePackagePath = basePackagePath;
        models = langToModels;
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
    public void createFor(Template template, OpenOption... options) throws IOException, TemplateException{
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
        }
    }

    private Object getModel(Lang.LocalizedLang messages){
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
