package ru.protei.portal.ui.common.shared.util;

import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;

import static ru.protei.portal.core.model.helper.StringUtils.emptyIfNull;

public class HtmlUtils {

    public static String sanitizeHtml(String html) {
        return SimpleHtmlSanitizer.sanitizeHtml(emptyIfNull(html)).asString();
    }
}
