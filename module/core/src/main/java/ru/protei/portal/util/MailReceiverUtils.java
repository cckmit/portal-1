package ru.protei.portal.util;

import org.apache.commons.io.IOUtils;
import ru.protei.portal.core.model.struct.MailReceiveContentAndType;

import javax.mail.*;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MailReceiverUtils {
    static public List<MailReceiveContentAndType> getContent(Message message) throws MessagingException, IOException {
        List<MailReceiveContentAndType> list = new ArrayList<>();
        Object content = message.getContent();
        if (content == null) {
            return list;
        }
        if (message.getContentType().startsWith(MIME_TEXT)) {
            list.add(new MailReceiveContentAndType((String)content, message.getContentType()));
        } else if (content instanceof Part) {
            list.addAll(getContent((Part) content));
        } else {
            list.addAll(getContent((Multipart) content));
        }
        return list;
    }

    static private List<MailReceiveContentAndType> getContent(Part part) throws IOException, MessagingException {
        List<MailReceiveContentAndType> list = new ArrayList<>();
        Object content = part.getContent();
        if (content == null) {
            return list;
        }
        if (part.getContentType().startsWith(MIME_TEXT)) {
            list.add(new MailReceiveContentAndType((String)content, part.getContentType()));
            return list;
        } else if (content instanceof Part) {
            return getContent((Part)content);
        } else if (content instanceof Multipart) {
            return getContent((Multipart) content);
        } else {
            if (!part.getContentType().startsWith(MIME_TEXT)) {
                return list;
            }
            if (content instanceof InputStream) {
                String s = IOUtils.toString((InputStream) content, StandardCharsets.UTF_8);
                list.add(new MailReceiveContentAndType(s, part.getContentType()));
            }
            return list;
        }
    }

    static private List<MailReceiveContentAndType> getContent(Multipart multipart) throws MessagingException {
        return extractContentAll(multipart, new ContentType(multipart.getContentType()).match(MIME_MULTIPART_ALTERNATIVE));
    }

    static private List<MailReceiveContentAndType> extractContentAll(Multipart multipart, boolean alternative) throws MessagingException {
        return IntStream.range(0, multipart.getCount())
                .mapToObj(i -> getContentBodyPart(multipart, i))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    static private List<MailReceiveContentAndType> getContentBodyPart(Multipart multipart, int i) {
        try {
            return getContent(multipart.getBodyPart(i));
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    static public Long getCaseNo(Message message) throws MessagingException {
        try {
            String subject = message.getSubject();
            if (subject == null) {
                return null;
            }
            Matcher matcher = issueRePattern.matcher(subject);
            if (!matcher.find()) {
                return null;
            }
            matcher = issueIdPattern.matcher(subject.substring(matcher.start(), matcher.end()));
            if (!matcher.find()) {
                return null;
            }
            String caseNo = subject.substring(matcher.start() + ISSUE_ID_PREFIX.length(), matcher.end());
            return Long.valueOf(caseNo);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static public String getSenderEmail(Message message) throws MessagingException {
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

    static private final String ISSUE_ID_PREFIX = "CRM-";
    static private final String ISSUE_RE_PREFIX = "Re:";
    static private final String MIME_MULTIPART_ALTERNATIVE = "multipart/alternative";
    static private final String MIME_TEXT = "TEXT/";
    static private final Pattern issueRePattern = Pattern.compile("^" + ISSUE_RE_PREFIX + ".+" + ISSUE_ID_PREFIX + "\\d+");
    static private final Pattern issueIdPattern = Pattern.compile(ISSUE_ID_PREFIX + "\\d+");
}
