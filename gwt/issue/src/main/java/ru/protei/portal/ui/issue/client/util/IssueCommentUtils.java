package ru.protei.portal.ui.issue.client.util;

import ru.protei.portal.core.model.ent.CaseComment;

import java.util.Date;

/**
 * Утилита по работе с комментариями
 */
public class IssueCommentUtils {

    // todo : check owner
    public static boolean isEnableEdit( CaseComment value ) {
        if ( value == null || value.getCreated() == null ) {
            return false;
        }

        Date now = new Date();
        return now.getTime() + EDIT_PERIOD <= value.getCreated().getTime();
    }

    public static void quoteMessage( String quotedMessage ) {
        quotedMessage = "< " + quotedMessage;
    }

    private static final long EDIT_PERIOD = 300000;
}
