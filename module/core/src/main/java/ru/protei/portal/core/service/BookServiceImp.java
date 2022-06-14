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
        if (id == null) {
            return Result.error(En_ResultStatus.INCORRECT_PARAMS);
        }
        Book book = bookDAO.get(id);
        if (book == null) {
            return Result.error(En_ResultStatus.NOT_FOUND);
        }
        return Result.ok(book);
    }

    @Override
    public Result<Long> createBook(AuthToken token, Book book) {
        if (book == null) {
            return Result.error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return Result.ok(bookDAO.persist(book));
    }

    @Override
    public Result<Long> deleteBook(AuthToken token, Long id) {
        if (id == null) {
            return Result.error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (bookDAO.removeByKey(id)) {
            return Result.ok(id);
        }
        return Result.error(En_ResultStatus.NOT_FOUND);
    }

    @Override
    public Result<Map<String, Integer>> getCountByAuthor(AuthToken authToken) {
        List<Pair<String, Integer>> pairs = bookDAO.getCountBookByAuthor();
        Map<String, Integer> map = new HashMap<>();
        for (Pair<String, Integer> pair : pairs) {
            map.put(pair.getA(), pair.getB());
        }
        return Result.ok(map);
    }
}
