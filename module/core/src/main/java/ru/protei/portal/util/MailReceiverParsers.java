package ru.protei.portal.util;

import org.apache.commons.io.IOUtils;

import javax.mail.*;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class MailReceiverParsers {
    static public MailContent parseContent(Part part) throws IOException, MessagingException {
        Object content = part.getContent();
        if (content == null) {
            return null;
        }
        ContentType contentType = new ContentType(part.getContentType());
        if (contentType.getPrimaryType().equals(MailContent.MIME_TEXT_TYPE)) {
            return new MailContent((String)content, contentType.getBaseType());
        } else if (content instanceof Part) {
            return parseContent((Part)content);
        } else if (content instanceof Multipart) {
            return parseContent((Multipart) content);
        } else if (content instanceof InputStream) {
            return new MailContent(IOUtils.toString((InputStream) content, StandardCharsets.UTF_8), contentType.getBaseType());
        }
        return null;
    }

    static private MailContent parseContent(Multipart multipart) throws MessagingException {
        if (new ContentType(multipart.getContentType()).match(MailContent.MIME_MULTIPART_ALTERNATIVE)) {
            return extractMostRichAlternativeContent(multipart);
        } else {
            return extractContentAll(multipart);
        }
    }

    static private MailContent extractContentAll(Multipart multipart) throws MessagingException {
        return IntStream.range(0, multipart.getCount())
                .mapToObj(i -> parseContentBodyPart(multipart, i))
                .findAny()
                .orElse(null);
    }

    static private MailContent extractMostRichAlternativeContent(Multipart multipart) throws MessagingException {
        int size = multipart.getCount();
        return IntStream.range(0, size)
                .mapToObj(i -> parseContentBodyPart(multipart, (size - 1) - i))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    static private MailContent parseContentBodyPart(Multipart multipart, int i) {
        try {
            return parseContent(multipart.getBodyPart(i));
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static public Long parseCaseNo(Message message) throws MessagingException {
        try {
            String subject = message.getSubject();
            if (subject == null) {
                return null;
            }

            Matcher matcher = issueIdPattern.matcher(subject);
            if (!matcher.find()) {
                return null;
            } else {
                String caseNo = subject.substring(matcher.start() + ISSUE_ID_PREFIX.length(), matcher.end());
                return Long.valueOf(caseNo);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static public boolean isInBlackList(Message message, Pattern blackListPattern) throws MessagingException {
        String subject = message.getSubject();
        if (subject == null) {
            return true;
        }

        Matcher matcher = blackListPattern.matcher(subject);
        return matcher.find();
    }

    static public String parseSenderEmail(Message message) throws MessagingException {
        Address[] from = message.getFrom();
        if (from == null) {
            return null;
        }
        if (from[0] instanceof InternetAddress) {
            return ((InternetAddress)from[0]).getAddress();
        } else {
            return null;
        }
    }

    static public String parseServiceInfo(Message message) {
        try {
            String[] messageId = message.getHeader("Message-ID");
            String[] messageReceived = message.getHeader("Received");
            return "Message-ID : " + String.join(", ", messageId) + ", Received : "+ String.join(", ", messageReceived);
        } catch (MessagingException e) {
            return "failed parse service info";
        }
    }

    static public class MailContent {
        private final String content;
        private final String contentType;

        public MailContent(String content, String contentType) {
            this.content = content;
            this.contentType = contentType;
        }

        public String getContent() {
            return content;
        }

        public String getContentType() {
            return contentType;
        }

        static public final String MIME_TEXT_TYPE = "TEXT";
        static public final String MIME_MULTIPART_ALTERNATIVE = "multipart/alternative";
        static public final String MIME_TEXT_HTML = "TEXT/HTML";

        static public final String ANY_SYMBOL = "(\\s|\\S)*";
        static public final String DATE = "\\d{1,2}(\\.|\\\\|-)\\d{1,2}(\\.|\\\\|-)\\d{4}";
        static public final String TIME = "([0-1][0-9]|[2][0-3]):([0-5][0-9])";
        static public final String EMAIL = "[-a-zA-Z0-9_\\.]+@[-a-zA-Z0-9_\\.]+\\.\\w{2,4}";
        static public final String THUNDERBIRD_REPLAY = DATE + "\\s+" + TIME + ",\\s+" + EMAIL;
        static public final String CONTENT_BEGIN_CRM_BODY_FTL = "===ContentBegin_crm\\.body\\.ftl===";
        static public final String CONTENT_END_CRM_BODY_FTL = "===ContentEnd_crm\\.body\\.ftl===";
        static public final String CONTENT_ID_CRM_BODY_FTL = "CrmMailContent";

        static public final String THUNDERBIRD_PATTERN_CONTENT_CRM_BODY_FTL = THUNDERBIRD_REPLAY + ANY_SYMBOL + CONTENT_BEGIN_CRM_BODY_FTL + ANY_SYMBOL  + CONTENT_END_CRM_BODY_FTL;
        static public final String BEGIN_END_PATTERN_CONTENT_CRM_BODY_FTL = CONTENT_BEGIN_CRM_BODY_FTL +  ANY_SYMBOL + CONTENT_END_CRM_BODY_FTL;
        static public final String BEGIN_PATTERN_CONTENT_CRM_BODY_FTL = CONTENT_BEGIN_CRM_BODY_FTL +  ANY_SYMBOL + "$";
        static public final String ONLY_END_PATTERN_CONTENT_CRM_BODY_FTL = CONTENT_END_CRM_BODY_FTL;
    }

    static private final String ISSUE_ID_PREFIX = "CRM-";
    static private final Pattern issueIdPattern = Pattern.compile(ISSUE_ID_PREFIX + "\\d+");
}
