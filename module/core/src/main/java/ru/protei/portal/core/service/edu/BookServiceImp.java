package ru.protei.portal.core.service.edu;

import ru.protei.portal.core.model.ent.Book;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BookServiceImp implements BookService {
    public static final long NOT_FOUND_BY_ID = -1;
    private List<Book> books;

    {
        books = new ArrayList<>();
        Book book1 = new Book();
        book1.setBookName("One");
        book1.setCreator("Author1");
        book1.setCreated(new Date());
        book1.setId(Book.getCount());
        Book book2 = new Book();
        book2.setBookName("Two");
        book2.setCreator("Author2");
        book2.setCreated(new Date());
        book2.setId(Book.getCount());
        books.add(book1);
        books.add(book2);
    }


    public List<Book> getAll() {
        return books;
    }

    public Book getByBookId(Long id) {
        return findById(id);
    }


    public Book createBook(Book book) {
        book.setId(0);
        long id = Book.getCount();
        book.setId(id);
        //валидатор если есть обязательные поля.
        books.add(book);
        return book;
    }


    public Long deleteBook(Long id) {
        Book book = findById(id);
        if (id == null || book == null) {
            return NOT_FOUND_BY_ID;
        }
        books.remove(findById(id));
        return id;
    }

    private Book findById(Long id) {
        if (id != null) {
            for (Book b : books) {
                if (b.getId() == id) {
                    return b;
                }
            }
        }
        return null;
    }
}
