package ru.protei.portal.core;

import org.springframework.context.MessageSource;

import java.util.HashMap;
import java.util.Locale;

/**
 * Вспомогательный класс для работы с бандлами
 */
public class Lang{

    private MessageSource messageSource;
    private HashMap<Locale, LocalizedLang> cache = new HashMap<>(2);

    public Lang(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public LocalizedLang getFor(Locale locale){
        return cache.computeIfAbsent(locale, LocalizedLang::new);
    }

    public String get(String key, Locale locale){
        return messageSource.getMessage(key, null, locale);
    }

    public String get(String key, Object[] args, Locale locale){
        return messageSource.getMessage(key, args, locale);
    }

    public class LocalizedLang {
        private Locale locale;
        private LocalizedLang(Locale locale) {
            this.locale = locale;
        }
        public String get(String key){
            return messageSource.getMessage(key, null, locale);
        }

        public String get(String key, Object[] args){
            return messageSource.getMessage(key, args, locale);
        }
        public String getLanguageTag() {
            return locale.toLanguageTag();
        }
    }
}
