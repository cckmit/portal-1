package ru.protei.portal.test.utils;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.protei.portal.util.MailReceiverUtils;

import java.io.File;
import java.io.IOException;

import static ru.protei.portal.util.MailReceiverUtils.MIME_TEXT_HTML;
import static ru.protei.portal.util.MailReceiverUtils.MIME_TEXT_TYPE;

public class MailReceiverUtilsTest {
    private final String FILE_PATH_MAIL_REMOVED_TXT = "mail.removed.html";
    private final String FILE_PATH_MAIL_NOT_REMOVED_THUNDERBIRD_HTML = "mail.not.removed.thunderbird.html";
    private final String FILE_PATH_MAIL_NOT_REMOVED_MOBILEGMAIL_HTML = "mail.not.removed.mobilegmail.html";
    private final String FILE_PATH_MAIL_NOT_REMOVED_TXT_HTML = "mail.not.removed.txt.html";

    private String mailRemovedTxt;
    private String mailNotRemovedThunderbirdHtml;
    private String mailNotRemovedMobileGmailHtml;
    private String mailNotRemovedTxt;

    @Before
    public void readFiles() throws IOException {
        mailRemovedTxt = getFileContent(FILE_PATH_MAIL_REMOVED_TXT);
        mailNotRemovedThunderbirdHtml = getFileContent(FILE_PATH_MAIL_NOT_REMOVED_THUNDERBIRD_HTML);
        mailNotRemovedMobileGmailHtml = getFileContent(FILE_PATH_MAIL_NOT_REMOVED_MOBILEGMAIL_HTML);
        mailNotRemovedTxt = getFileContent(FILE_PATH_MAIL_NOT_REMOVED_TXT_HTML);
    }

    @Test
    public void mailRemovedTxt() {
        String cleanedContent = MailReceiverUtils.getCleanedContent(MIME_TEXT_TYPE, mailRemovedTxt);
        Assert.assertEquals("С удалением\n" +
                "на несколько строк\n" +
                "3",
                cleanedContent);
    }

    @Test
    public void mailNotRemovedMobileGmailHtml() {
        String cleanedContent = MailReceiverUtils.getCleanedContent(MIME_TEXT_HTML, mailNotRemovedThunderbirdHtml);
        Assert.assertEquals("Ответ без удаления \n" +
                        "на несколько строк",
                cleanedContent);
    }

    @Test
    public void mailNotRemovedThunderbirdHtml() {
        String cleanedContent = MailReceiverUtils.getCleanedContent(MIME_TEXT_HTML, mailNotRemovedMobileGmailHtml);
        Assert.assertEquals("Гмайл\n" +
                        "Без разделителя\n" +
                        "3\n" +
                        "4",
                cleanedContent);
    }

    @Test
    public void mailNotRemovedTxt() {
        String cleanedContent = MailReceiverUtils.getCleanedContent(MIME_TEXT_TYPE, mailNotRemovedTxt);
        Assert.assertEquals("С разделителем\n" +
                        "на несколько строк\n" +
                        "3",
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
