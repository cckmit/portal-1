package ru.protei.portal.test.utils;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.protei.portal.util.MailReceiverContentUtils;

import java.io.File;
import java.io.IOException;

import static ru.protei.portal.util.MailReceiverContentUtils.MIME_TEXT_HTML;
import static ru.protei.portal.util.MailReceiverContentUtils.MIME_TEXT_TYPE;

public class MailReceiverContentUtilsTest {
    private final String FILE_PATH_MAIL_REMOVED_TXT = "mail.removed.html";
    private final String FILE_PATH_MAIL_NOT_REMOVED_THUNDERBIRD_HTML = "mail.not.removed.thunderbird.html";
    private final String FILE_PATH_MAIL_NOT_REMOVED_MOBILEGMAIL_HTML = "mail.not.removed.mobilegmail.html";
    private final String FILE_PATH_MAIL_DOT_HTML = "mail.dot.html";
    private final String FILE_PATH_MAIL_DASH_HTML = "mail.dash.html";


    private String mailRemovedTxt;
    private String mailNotRemovedThunderbirdHtml;
    private String mailNotRemovedMobileGmailHtml;
    private String mailDotHtml;
    private String mailDashHtml;

    @Before
    public void readFiles() throws IOException {
        mailRemovedTxt = getFileContent(FILE_PATH_MAIL_REMOVED_TXT);
        mailNotRemovedThunderbirdHtml = getFileContent(FILE_PATH_MAIL_NOT_REMOVED_THUNDERBIRD_HTML);
        mailNotRemovedMobileGmailHtml = getFileContent(FILE_PATH_MAIL_NOT_REMOVED_MOBILEGMAIL_HTML);
        mailDotHtml = getFileContent(FILE_PATH_MAIL_DOT_HTML);
        mailDashHtml = getFileContent(FILE_PATH_MAIL_DASH_HTML);
    }

    @Test
    public void mailRemovedTxt() {
        String cleanedContent = MailReceiverContentUtils.getCleanedContent(MIME_TEXT_TYPE, mailRemovedTxt);
        Assert.assertEquals("С удалением\n" +
                "на несколько строк\n" +
                "3\n",
                cleanedContent);
    }

    @Test
    public void mailNotRemovedMobileGmailHtml() {
        String cleanedContent = MailReceiverContentUtils.getCleanedContent(MIME_TEXT_HTML, mailNotRemovedThunderbirdHtml);
        Assert.assertEquals("\n" +
                        "Ответ без удаления \n" +
                        "на несколько строк \n",
                cleanedContent);
    }

    @Test
    public void mailNotRemovedThunderbirdHtml() {
        String cleanedContent = MailReceiverContentUtils.getCleanedContent(MIME_TEXT_HTML, mailNotRemovedMobileGmailHtml);
        Assert.assertEquals("\n" +
                        "Гмайл\n" +
                        "Без разделителя\n" +
                        "3\n" +
                        "4\n",
                cleanedContent);
    }

    @Test
    public void mailDotHtml() {
        String cleanedContent = MailReceiverContentUtils.getCleanedContent(MIME_TEXT_HTML, mailDotHtml);
        Assert.assertEquals("\n" +
                        "с точками \n" +
                        "2 \n" +
                        "3 ",
                cleanedContent);
    }

    @Test
    public void mailDashHtml() {
        String cleanedContent = MailReceiverContentUtils.getCleanedContent(MIME_TEXT_HTML, mailDashHtml);
        Assert.assertEquals("\n" +
                        "C запятыми   \n" +
                        "2 \n" +
                        "3 ",
                cleanedContent);
    }

    private String getFileContent( String fileName ) {

        String result = "";

        ClassLoader classLoader = getClass().getClassLoader();
        String aPackage = getClass().getPackage().getName().replace( ".", File.separator );
        try {
            String s = aPackage + File.separator + fileName;
            result = IOUtils.toString( classLoader.getResourceAsStream( s ) );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
