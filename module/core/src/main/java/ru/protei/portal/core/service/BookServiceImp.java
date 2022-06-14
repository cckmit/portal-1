package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.BookDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Book;
import ru.protei.portal.core.model.struct.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookServiceImp implements BookService {
    @Autowired
    private BookDAO bookDAO;

    @Override
    public Result<List<Book>> getAll(AuthToken authToken) {
        return Result.ok(bookDAO.getAll());
    }

    @Override
    public Result<Book> getByBookId(AuthToken token, Long id) {
        if (id != null) {
            Book book = bookDAO.getByBookId(id);
            if (book != null) {
                return Result.ok(book);
            }
        }
        return Result.error(En_ResultStatus.INCORRECT_PARAMS);
    }

    @Override
    public Result<Long> createBook(AuthToken token, Book book) {
        if (book != null) {
            return Result.ok(bookDAO.createBook(book));
        }
        return Result.error(En_ResultStatus.INCORRECT_PARAMS);
    }

    @Override
    public Result<Long> deleteBook(AuthToken token, Long id) {
        if (id != null) {
            Long bookId = bookDAO.deleteBook(id);
            if (bookId != null) {
                return Result.ok(bookId);
            }
        }
        return Result.error(En_ResultStatus.INCORRECT_PARAMS);
    }

    @Override
    public Result<Map<String, Integer>> getCountByAuthor(AuthToken authToken) {
        List<Pair<String , Integer>> pairs = bookDAO.getCountBookByAuthor();
        Map<String , Integer> map = new HashMap<>();
        for (Pair<String, Integer> pair : pairs){
            map.put(pair.getA(), pair.getB());
        }
        return Result.ok(map);
    }
}
