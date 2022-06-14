package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Book;

import java.util.List;
import java.util.Map;


public interface BookService {


    Result<List<Book>> getAll(AuthToken token);

    Result<Book> getByBookId(AuthToken token, Long bookId);

    Result<Long> createBook(AuthToken token, Book book);

    Result<Long> deleteBook(AuthToken token, Long id);

    Result<Map<String, Integer>> getCountByAuthor(AuthToken authToken);
}
