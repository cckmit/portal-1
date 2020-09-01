package ru.protei.portal.util;

import org.apache.commons.io.IOUtils;
import ru.protei.portal.core.model.struct.receivedmail.MailReceiveContentAndType;

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

public class MailReceiverParsers {
    static public List<MailReceiveContentAndType> parseContent(Part part) throws IOException, MessagingException {
        List<MailReceiveContentAndType> list = new ArrayList<>();
        Object content = part.getContent();
        if (content == null) {
            return list;
        } else if (part.getContentType().startsWith(MIME_TEXT)) {
            list.add(new MailReceiveContentAndType((String)content, part.getContentType()));
        } else if (content instanceof Part) {
            return parseContent((Part)content);
        } else if (content instanceof Multipart) {
            return parseContent((Multipart) content);
        } else if (content instanceof InputStream) {
            String s = IOUtils.toString((InputStream) content, StandardCharsets.UTF_8);
            list.add(new MailReceiveContentAndType(s, part.getContentType()));
        }
        return list;
    }

    static private List<MailReceiveContentAndType> parseContent(Multipart multipart) throws MessagingException {
        return extractContentAll(multipart, new ContentType(multipart.getContentType()).match(MIME_MULTIPART_ALTERNATIVE));
    }

    static private List<MailReceiveContentAndType> extractContentAll(Multipart multipart, boolean alternative) throws MessagingException {
        return IntStream.range(0, multipart.getCount())
                .mapToObj(i -> parseContentBodyPart(multipart, i))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    static private List<MailReceiveContentAndType> parseContentBodyPart(Multipart multipart, int i) {
        try {
            return parseContent(multipart.getBodyPart(i));
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
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

    static private final String ISSUE_ID_PREFIX = "CRM-";
    static private final String MIME_MULTIPART_ALTERNATIVE = "multipart/alternative";
    static private final String MIME_TEXT = "TEXT/";
    static private final Pattern issueIdPattern = Pattern.compile(ISSUE_ID_PREFIX + "\\d+");
}
