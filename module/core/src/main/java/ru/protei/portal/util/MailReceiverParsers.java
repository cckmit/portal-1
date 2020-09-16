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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MailReceiverParsers {
    static public String parseContent(Part part) throws IOException, MessagingException {
        Object content = part.getContent();
        if (content == null) {
            return null;
        } else if (part.getContentType().startsWith(MIME_TEXT)) {
            return (String)content;
        } else if (content instanceof Part) {
            return parseContent((Part)content);
        } else if (content instanceof Multipart) {
            return parseContent((Multipart) content);
        } else if (content instanceof InputStream) {
            return IOUtils.toString((InputStream) content, StandardCharsets.UTF_8);
        }
        return null;
    }

    static private String parseContent(Multipart multipart) throws MessagingException {
        if (new ContentType(multipart.getContentType()).match(MIME_MULTIPART_ALTERNATIVE)) {
            return extractMostRichAlternativeContent(multipart);
        } else {
            return extractContentAll(multipart);
        }
    }

    static private String extractContentAll(Multipart multipart) throws MessagingException {
        return IntStream.range(0, multipart.getCount())
                .mapToObj(i -> parseContentBodyPart(multipart, i))
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
    }

    static private String extractMostRichAlternativeContent(Multipart multipart) throws MessagingException {
        int size = multipart.getCount();
        return IntStream.range(0, size)
                .mapToObj(i -> parseContentBodyPart(multipart, (size-1) - i))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    static private String parseContentBodyPart(Multipart multipart, int i) {
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
