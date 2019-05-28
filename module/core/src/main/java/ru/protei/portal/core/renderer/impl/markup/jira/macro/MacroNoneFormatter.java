package ru.protei.portal.core.renderer.impl.markup.jira.macro;

import com.atlassian.renderer.v2.macro.code.formatter.AbstractFormatter;

/**
 * "java" language used by default when no language defined
 * @see com.atlassian.renderer.v2.macro.code.formatter.NoneFormatter
 * @see com.atlassian.renderer.v2.macro.code.CodeMacro#getLanguage(java.util.Map)
 */
public class MacroNoneFormatter extends AbstractFormatter {

    private final String[] SUPPORTED_LANGUAGES = new String[]{"none", "java"};

    @Override
    public String[] getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }
}
