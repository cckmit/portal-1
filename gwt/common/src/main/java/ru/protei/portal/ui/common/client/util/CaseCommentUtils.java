package ru.protei.portal.ui.common.client.util;

import com.google.gwt.core.client.GWT;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.Date;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

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

    public static String appendQuote(String text, String quotation, En_TextMarkup textMarkup) {
        if (StringUtils.isBlank(text)) {
            return quoteMessage(quotation, textMarkup);
        } else {
            return text + NEW_LINE_SYMBOL + quoteMessage(quotation, textMarkup);
        }
    }

    public static String quoteMessage( String message, En_TextMarkup textMarkup ) {
        StringBuilder sb = new StringBuilder();
        switch (textMarkup) {
            case MARKDOWN: quoteMessageMarkdown(message, sb); break;
            case JIRA_WIKI_MARKUP: quoteMessageWikiMarkup(message, sb); break;
        }
        return sb.toString();
    }

    public static void quoteMessageMarkdown(String message, StringBuilder sb) {
        String[] lines = message.split(NEW_LINE_SYMBOL);
        for (String line : lines) {
            sb.append("> ").append(line).append(NEW_LINE_SYMBOL);
        }
    }

    public static void quoteMessageWikiMarkup(String message, StringBuilder sb) {
        if (message.contains(NEW_LINE_SYMBOL)) {
            sb.append("{quote}");
            sb.append(NEW_LINE_SYMBOL);
            sb.append(message);
            sb.append(NEW_LINE_SYMBOL);
            sb.append("{quote}");
        } else {
            sb.append("bq. ").append(message).append(NEW_LINE_SYMBOL);
        }
    }

    public static String addImageInMessage(String message, boolean addInPosition, Integer position, Attachment attach) {
        if (addInPosition) {
            return message.substring(0, position) + NEW_LINE_SYMBOL + makeImageString(attach) + NEW_LINE_SYMBOL + message.substring(position);
        } else {
            return isEmpty(message)? makeImageString(attach) : message + NEW_LINE_SYMBOL + makeImageString(attach);
        }
    }

    public static String makeImageString(Attachment attach) {
        return ("![alt=" +  attach.getFileName() +"]("+ DOWNLOAD_PATH + attach.getExtLink() +")");
    }

    private static final long EDIT_PERIOD = 300000;
    private final static String NEW_LINE_SYMBOL = "\n";
    private static final String DOWNLOAD_PATH = GWT.getModuleBaseURL() + "springApi/files/";
}
