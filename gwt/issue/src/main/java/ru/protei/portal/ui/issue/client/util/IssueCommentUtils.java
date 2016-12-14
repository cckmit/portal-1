package ru.protei.portal.ui.issue.client.util;

import ru.protei.portal.core.model.ent.CaseComment;

import java.util.Date;

/**
 * Утилита по работе с комментариями
 */
public class IssueCommentUtils {

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

    public static String quoteMessage( String message ) {
        return ">" + message + "\n";
    }

    public static String prewrapMessage( String message ) {
        return message.replaceAll(">(.*?)(\\n|$)+", "<blockquote>$1</blockquote>");
    }

    private static final long EDIT_PERIOD = 300000;
}
