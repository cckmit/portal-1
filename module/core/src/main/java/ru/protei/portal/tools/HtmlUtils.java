package ru.protei.portal.tools;


import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;

public abstract class HtmlUtils {

    private static final List<Replacements> DEFAULT_HTML_CHARS_REPLACEMENTS = listOf(
            new ReplacementsImpl("&", "&amp;"), new ReplacementsImpl("<", "&lt;"),
            new ReplacementsImpl("\"", "&quot;"), new ReplacementsImpl("'", "&#39;")
    );


    /** Facade to org.springframework.util.Assert.HtmlUtils htmlEscape */
    public static String htmlEscape(String s) {
        return org.springframework.web.util.HtmlUtils.htmlEscape(s);
    }
    public static String htmlEscapeCharacters(String s) {
        return htmlEscapeCharacters(s, DEFAULT_HTML_CHARS_REPLACEMENTS);
    }

    public static String htmlEscapeCharacters(String s, List<Replacements> replacements) {
        for (Replacements replacement: replacements) {
            if (s.contains(replacement.from())) {
                s = s.replaceAll(replacement.from(), replacement.to());
            }
        }
        return s;
    }
    public interface Replacements {
        String from();
        String to();
    }

     static class ReplacementsImpl implements Replacements {
         private final String from;
         private final String to;

         public ReplacementsImpl(String from, String to) {
             this.from = from;
             this.to = to;
         }

         @Override
         public String from() {
             return from;
         }

         @Override
         public String to() {
             return to;
         }
     }
}
