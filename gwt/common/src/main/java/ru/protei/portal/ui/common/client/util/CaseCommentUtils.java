package ru.protei.portal.ui.common.client.util;

import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.Date;

/**
 * Утилита по работе с комментариями
 */
public class CaseCommentUtils {

    public static boolean isEnableEdit( CaseComment value, Long personId ) {
        if ( value == null || value.getCreated() == null ) {
            return false;
        }

        if ( !personId.equals( value.getAuthorId() ) ) {
            return false;
        }

        Date now = new Date();
        return now.getTime() - value.getCreated().getTime() <= EDIT_PERIOD;
    }

    public static String appendQuote(String text, String quotation) {
        if (StringUtils.isBlank(text)) {
            return quoteMessage(quotation);
        } else {
            return text + NEW_LINE_SYMBOL + quoteMessage(quotation);
        }
    }

    public static String quoteMessage( String message ) {
        StringBuilder sb = new StringBuilder();
        String[] lines = message.split(NEW_LINE_SYMBOL);
        for (String line : lines) {
            sb.append("> ").append(line).append(NEW_LINE_SYMBOL);
        }
        return sb.toString();
    }

    private static final long EDIT_PERIOD = 300000;
    private final static String NEW_LINE_SYMBOL = "\n";

}
