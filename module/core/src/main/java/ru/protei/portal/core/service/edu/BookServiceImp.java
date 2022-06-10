package ru.protei.portal.core.service.edu;

import ru.protei.portal.core.model.ent.Book;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BookServiceImp implements BookService {
    public static final long NOT_FOUND_BY_ID = -1;
    private final List<Book> books;

    public BookServiceImp() {
        books = new ArrayList<>();
        Book book1 = new Book();
        book1.setBookName("One");
        book1.setAuthor("Author1");
        book1.setCreated(new Date());
        book1.setId(Book.getCount());
        Book book2 = new Book();
        book2.setBookName("Two");
        book2.setAuthor("Author2");
        book2.setCreated(new Date());
        book2.setId(Book.getCount());
        books.add(book1);
        books.add(book2);
    }

    @Override
    public List<Book> getAll() {
        return books;
    }

    @Override
    public Book getByBookId(Long id) {
        return findById(id);
    }

    @Override
    public Long createBook(Book book) {
        book.setId(0);
        long id = Book.getCount();
        book.setId(id);
        books.add(book);
        return id;
    }

    @Override
    public Long deleteBook(Long id) {
        Book book = findById(id);
        if (id == null || book == null) {
            return NOT_FOUND_BY_ID;
        }
        books.remove(book);
        return id;
    }

    private Book findById(Long id) {
        if (id != null) {
            return books.stream().filter(b -> b.getId() == id).findAny().orElse(null);
        }
        return null;
    }
}
