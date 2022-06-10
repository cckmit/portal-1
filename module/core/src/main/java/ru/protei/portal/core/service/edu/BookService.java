package ru.protei.portal.core.service.edu;

import ru.protei.portal.core.model.ent.Book;

import java.util.List;


public interface BookService {


    List<Book> getAll();

    Book getByBookId(Long bookId);

    Long createBook(Book book);

    Long deleteBook(Long id);


}
