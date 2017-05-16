package ru.protei.portal.test.hpsm;

import org.springframework.integration.core.MessageSource;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;


/**
 * Created by michael on 16.05.17.
 */
public class TestInMail {

    public static void main (String argv[]) {

        ImapMailReceiver imapMailReceiver = new ImapMailReceiver("imaps://crm_test-user:hae7Eito@imap.protei.ru:993/INBOX");
//        imapMailReceiver.setShouldMarkMessagesAsRead(false);
//        imapMailReceiver.setShouldDeleteMessages(false);
        imapMailReceiver.setEmbeddedPartsAsBytes(false);
//        imapMailReceiver.setUserFlag(null);


        MessageSource<Object> source = new MailReceivingMessageSource(imapMailReceiver);
        Object x = source.receive();

        if (x != null)
            System.out.println(x);
        else
            System.out.println("no data");


    }
}
