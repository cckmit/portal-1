package ru.protei.portal.util;

import org.apache.commons.io.IOUtils;
import ru.protei.portal.core.model.struct.MailReceiveContentAndType;

import javax.mail.*;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MailReceiverUtils {
    static public List<MailReceiveContentAndType> extractText(Message message) throws MessagingException, IOException {
        Object content = message.getContent();
        if (content == null) {
            return null;
        }
        List<MailReceiveContentAndType> list = new ArrayList<>();
        if (message.getContentType().startsWith("TEXT/")) {
            list.add(new MailReceiveContentAndType((String)content, message.getContentType()));
        } else if (content instanceof Part) {
            list.addAll(extractText((Part) content));
        } else {
            list.addAll(extractText((Multipart) content));
        }
        return list ;
    }

    static private List<MailReceiveContentAndType> extractText(Part part) throws IOException, MessagingException {
        Object content = part.getContent();
        if (content == null) {
            return null;
        }
        List<MailReceiveContentAndType> list = new ArrayList<>();
        if (part.getContentType().startsWith("TEXT")) {
            list.add(new MailReceiveContentAndType((String)content, part.getContentType()));
            return list;
        } else if (content instanceof Part) {
            return extractText((Part)content);
        } else if (content instanceof Multipart) {
            return extractText((Multipart) content);
        } else {
            if (!part.getContentType().startsWith("text")) {
                return null;
            }
            if (content instanceof InputStream) {
                String s = IOUtils.toString((InputStream) content, "UTF-8");
                list.add(new MailReceiveContentAndType(s, part.getContentType()));
            }
            return null;
        }
    }

    static private List<MailReceiveContentAndType> extractText(Multipart multipart) throws MessagingException {
        return extractTextAll(multipart, new ContentType(multipart.getContentType()).match("multipart/alternative"));
    }

    static private List<MailReceiveContentAndType> extractTextAll(Multipart multipart, boolean alternative) throws MessagingException {
        return IntStream.range(0, multipart.getCount())
                .mapToObj(i -> extractTextBodyPart(multipart, i))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    static private List<MailReceiveContentAndType> extractTextBodyPart(Multipart multipart, int i) {
        try {
            return extractText(multipart.getBodyPart(i));
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static public Long getCaseNo(Message message) {
        try {
            String subject = message.getSubject();
            if (subject == null) {
                return null;
            }
            Matcher matcher = issueIdPattern.matcher(subject);
            if (!matcher.find()) {
                return null;
            } else {
                String caseNo = subject.substring(matcher.start() + issueIdPrefix.length(), matcher.end());
                return Long.valueOf(caseNo);
            }
        } catch (MessagingException | NumberFormatException e) {
            return null;
        }
    }

    static public String getSenderEmail(Message message) {
        try {
            Address[] from = message.getFrom();
            if (from == null) {
                return null;
            }
            if (from[0] instanceof InternetAddress) {
                return ((InternetAddress)from[0]).getAddress();
            } else {
                return null;
            }
        } catch (MessagingException e) {
            return null;
        }
    }

    static private final String issueIdPrefix = "CRM-";
    static private final Pattern issueIdPattern = Pattern.compile(issueIdPrefix + "\\d+");
}
