package ru.protei.portal.core.mail;

import javax.mail.internet.MimeMessage;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by michael on 25.04.17.
 */
public class VirtualMailSendChannel implements MailSendChannel {

    Queue<MimeMessage> messageQueue;

    public VirtualMailSendChannel () {
        this.messageQueue = new ArrayDeque<>();
    }

    @Override
    public void send(MimeMessage msg) {
        this.messageQueue.add(msg);
    }

    public MimeMessage get () {
        return this.messageQueue.isEmpty() ? null : this.messageQueue.poll();
    }
}
