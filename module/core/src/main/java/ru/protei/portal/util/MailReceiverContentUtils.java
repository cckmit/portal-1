package ru.protei.portal.util;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import javax.mail.*;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class MailReceiverContentUtils {
    static public MailContent parseContent(Part part) throws IOException, MessagingException {
        Object content = part.getContent();
        if (content == null) {
            return null;
        }
        ContentType contentType = new ContentType(part.getContentType());
        if (contentType.getPrimaryType().equals(MIME_TEXT_TYPE)) {
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
        if (new ContentType(multipart.getContentType()).match(MIME_MULTIPART_ALTERNATIVE)) {
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
    }

    static public String getCleanedContent(String contentType, String content) {
        if (contentType == null || content == null) {
            return null;
        }
        if (contentType.equals(MIME_TEXT_HTML)) {
            content = htmlClean(content);
        }
        return plainClean(content);
    }

    static private String htmlClean(String content) {
        Document document = Jsoup.parse(content);
        boolean isContentRemoved = removeById(document, CONTENT_ID_CRM_BODY_FTL);
        if (!isContentRemoved) isContentRemoved = removeByClass(document, CONTENT_ID_CRM_BODY_FTL);
        if (!isContentRemoved) isContentRemoved = removeByMark(document, CONTENT_BEGIN_MARK_CRM_BODY_FTL);
        return Jsoup.clean(document.html(),
                "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    static private boolean removeById(Document document, String id) {
        Element elementById = document.getElementById(id);
        if (elementById != null) {
            elementById.remove();
            return true;
        }
        return false;
    }

    static private boolean removeByClass(Document document, String elementClass) {
        Elements elementsByClass = document.getElementsByClass(elementClass);
        if (elementsByClass != null && !elementsByClass.isEmpty()) {
            elementsByClass.remove();
            return true;
        }
        return false;
    }

    static private boolean removeByMark(Document document, String beginMark) {
        Elements beginMarkElements = document.getElementsContainingText(beginMark);
        if (beginMarkElements != null) {
            Element last = beginMarkElements.last();
            Element element = last.nextElementSibling();
            if (element != null) {
                element.remove();
                return true;
            }
        }
        return false;
    }

    static private String plainClean(String content) {
        content = cleanNewLineDuplicate(content);
        for (Pattern pattern : crmContentPatterns) {
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                content = content.substring(0, matcher.start()) +
                        content.substring(matcher.end());
                break;
            }
        }
        return cleanNewLineDuplicate(content);
    }

    static private String cleanNewLineDuplicate(String content) {
        for (int i = 0; i < 2; i++) {
            Matcher matcher = newLineDuplicatePattern.matcher(content);
            content = matcher.replaceAll("\n");
        }
        return content;
    }

    static public final String MIME_TEXT_TYPE = "TEXT";
    static private final String MIME_MULTIPART_ALTERNATIVE = "multipart/alternative";
    static public final String MIME_TEXT_HTML = "TEXT/HTML";

    static private final String DOT_DASH_LINE = "\n *(\\.|,){7,} *\n";
    static private final String ANY_SYMBOL = "(\\s|\\S)*";
    static private final String DATE = "\\d{1,2}(\\.|\\\\|-)\\d{1,2}(\\.|\\\\|-)\\d{4}";
    static private final String GMAIL_DATE = "\\d{1,2} \\S{2,7}\\. \\d{4} г\\.";
    static private final String TIME = "([0-1][0-9]|[2][0-3]):([0-5][0-9])";
    static private final String EMAIL = "[-a-zA-Z0-9_\\.]+@[-a-zA-Z0-9_\\.]+\\.\\w{2,4}";
    static private final String THUNDERBIRD_REPLAY = DATE + "\\s+" + TIME + ",\\s+" + EMAIL;
    static private final String MOBILEGMAIL_REPLAY = GMAIL_DATE + "\\s+" + TIME + ANY_SYMBOL + EMAIL;
    static private final String CONTENT_BEGIN_CRM_BODY_FTL = "===ContentBegin_crm\\.body\\.ftl===";
    static private final String CONTENT_END_CRM_BODY_FTL = "===ContentEnd_crm\\.body\\.ftl===";
    static private final String CONTENT_ID_CRM_BODY_FTL = "CrmMailContent";
    static private final String CONTENT_BEGIN_MARK_CRM_BODY_FTL = "===ContentBegin_crm.body.ftl===";

    static private final String CONTENT_NEW_LINE_DUPLICATE = "(\n+) *";

    static private final String DOT_DASH_PATTERN_CONTENT_CRM_BODY_FTL = DOT_DASH_LINE + ANY_SYMBOL + "$";
    static private final String THUNDERBIRD_PATTERN_CONTENT_CRM_BODY_FTL = THUNDERBIRD_REPLAY + ANY_SYMBOL + CONTENT_BEGIN_CRM_BODY_FTL + ANY_SYMBOL  + CONTENT_END_CRM_BODY_FTL;
    static private final String MOBILEGMAIL_PATTERN_CONTENT_CRM_BODY_FTL = MOBILEGMAIL_REPLAY + ANY_SYMBOL + CONTENT_BEGIN_CRM_BODY_FTL + ANY_SYMBOL  + CONTENT_END_CRM_BODY_FTL;
    static private final String BEGIN_END_PATTERN_CONTENT_CRM_BODY_FTL = CONTENT_BEGIN_CRM_BODY_FTL +  ANY_SYMBOL + CONTENT_END_CRM_BODY_FTL;
    static private final String ONLY_BEGIN_PATTERN_CONTENT_CRM_BODY_FTL = CONTENT_BEGIN_CRM_BODY_FTL + ANY_SYMBOL + "$";
    static private final String ONLY_END_PATTERN_CONTENT_CRM_BODY_FTL = CONTENT_END_CRM_BODY_FTL;

    static private final List<Pattern> crmContentPatterns = Arrays.asList(
            Pattern.compile(DOT_DASH_PATTERN_CONTENT_CRM_BODY_FTL),
            Pattern.compile(THUNDERBIRD_PATTERN_CONTENT_CRM_BODY_FTL),
            Pattern.compile(MOBILEGMAIL_PATTERN_CONTENT_CRM_BODY_FTL),
            Pattern.compile(BEGIN_END_PATTERN_CONTENT_CRM_BODY_FTL),
            Pattern.compile(ONLY_BEGIN_PATTERN_CONTENT_CRM_BODY_FTL),
            Pattern.compile(ONLY_END_PATTERN_CONTENT_CRM_BODY_FTL)
    );
    static private final Pattern newLineDuplicatePattern = Pattern.compile(CONTENT_NEW_LINE_DUPLICATE);

    static private final String ISSUE_ID_PREFIX = "CRM-";
    static private final Pattern issueIdPattern = Pattern.compile(ISSUE_ID_PREFIX + "\\d+");
}
