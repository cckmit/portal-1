package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ru.protei.portal.core.model.util.CrmConstants.Time.HOUR;


public class BookTest {

    @Test
    public void testGetBookByJson() {
        ObjectMapper mapper = new ObjectMapper();
        Book book;
        Book actualBook;
        try {
            book = new Book();
            book.setId(1L);
            book.setBookName("BOOK");
            book.setCreator("author");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date data = simpleDateFormat.parse("30.12.2001");
            Date newDate = new Date(data.getTime() + 3 * HOUR);
            book.setCreated(newDate);
            book.setNullData("not null now");

            String testBook = "{\n"
                    + "\"id\" : 1,\n"
                    + "\"bookName\" : \"BOOK\",\n"
                    + "\"creator\": \"author\",\n"
                    + "\"created\": \"30.12.2001\",\n"
                    + "\"null_data\": \"not null now\"\n"
                    + "}";
            actualBook = mapper.readValue(testBook, Book.class);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        Assert.assertNotNull(actualBook);
        Assert.assertEquals(book, actualBook);
    }
}