package ru.protei.portal.core.mail;

import ru.protei.portal.core.model.helper.HelperFunc;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReferenceableMimeMessage extends MimeMessage {

    private final String pattern;
    private final String messageId;
    private final String inReplyTo;
    private final List<String> references;

    public ReferenceableMimeMessage(Session session, String pattern, String messageId, String inReplyTo, List<String> references) {
        super(session);
        this.pattern = pattern;
        this.messageId = messageId;
        this.inReplyTo = inReplyTo;
        this.references = references;
    }

    @Override
    protected void updateMessageID() throws MessagingException {
        if (HelperFunc.isNotEmpty(messageId)) {
            setHeader("Message-ID", "<" + getId(messageId) + ">");
        } else {
            super.updateMessageID();
        }
        if (HelperFunc.isNotEmpty(inReplyTo)) {
            setHeader("In-Reply-To", "<" + getId(inReplyTo) + ">");
        }
        if (references != null && references.size() > 0) {
            setHeader("References", references.stream()
                    .filter(Objects::nonNull)
                    .map(reference -> "<" + getId(reference) + ">")
                    .collect(Collectors.joining(" "))
            );
        }
    }

    private String getId(String id) {
        if (pattern == null || id == null) {
            return id;
        }
        return pattern.replace("%id%", id);
    }
}
