package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;


public class BookTest {
    public Book book;

    @Before
    public void initBook() {
        book = new Book();
        book.setId(1L);
        book.setBookName("BOOK");
        book.setAuthor("author");
        Calendar calendar = new GregorianCalendar(2001, Calendar.DECEMBER, 30);
        TimeZone itTimeZone = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(itTimeZone);
        Date date = calendar.getTime();
        book.setCreated(date);
        book.setNullData("not null now");
    }

    @Test
    public void testGetBookByJson() {
        ObjectMapper mapper = new ObjectMapper();
        Book actualBook;
        String testBook = "{\n"
                + "\"id\" : 1,\n"
                + "\"bookName\" : \"BOOK\",\n"
                + "\"creator\": \"author\",\n"
                + "\"created\": \"30.12.2001\",\n"
                + "\"null_data\": \"not null now\"\n"
                + "}";
        try {
            actualBook = mapper.readValue(testBook, Book.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assert.assertNotNull(actualBook);
        Assert.assertEquals(book, actualBook);
    }

    @Test
    public void testCreateBookByJson() {
        ObjectMapper mapper = new ObjectMapper();
        String actualBook;
        Book bookNew;
        try {
            actualBook = mapper.writeValueAsString(book);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assert.assertNotNull(actualBook);
        String testBook = "{\"id\":1,\"book_name\":\"BOOK\",\"creator\":\"author\",\"created\":\"30.12.2001\",\"null_data\":\"not null now\"}";
        Assert.assertEquals(testBook, actualBook);
    }

    @Test
    public void testListBook() {
        ObjectMapper mapper = new ObjectMapper();
        List<Book> books = new ArrayList<>();
        books.add(book);
        books.add(book);
        books.add(book);

        String listJson;
        List<Book> newListBooks;
        try {
            listJson = mapper.writeValueAsString(books);
            CollectionType type = TypeFactory.defaultInstance().constructCollectionType(List.class, Book.class);
            newListBooks = mapper.readValue(listJson, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assert.assertNotNull(listJson);
        Assert.assertEquals(book, newListBooks.get(1));
        Assert.assertEquals(books, newListBooks);
    }
}