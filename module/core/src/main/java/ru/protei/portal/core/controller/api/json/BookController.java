package ru.protei.portal.core.controller.api.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.controller.api.json.utils.JsonRequest;
import ru.protei.portal.core.controller.api.json.utils.JsonResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Book;
import ru.protei.portal.core.service.BookService;
import ru.protei.portal.core.service.session.SessionService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

@RestController
@RequestMapping(value = "/jsonApi/book", headers = "Accept=application/json",
        produces = "application/json", consumes = "application/json")
@EnableWebMvc
public class BookController {
    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    BookService bookService;

    @PostMapping(value = "/allBooks")
    public JsonResponse<List<Book>> getAllBook(@RequestBody JsonRequest<?> req , HttpServletRequest request) {
        return new JsonResponse<>(req.getRequestId(), getAuthToken(request)
                .flatMap(token  -> bookService.getAll(token))
        );
    }

    @PostMapping(value = "/get")
    public JsonResponse<Book> getBookById(@RequestBody JsonRequest<Long> id, HttpServletRequest request) {
        return new JsonResponse<>(id.getRequestId(), getAuthToken(request)
                .flatMap(token -> bookService.getByBookId(token, id.getData()))
                );
    }

    @PostMapping(value = "/create")
    public JsonResponse<Long> createBook(@RequestBody JsonRequest<Book> book, HttpServletRequest request) {
        return new JsonResponse<>(book.getRequestId(), getAuthToken(request)
                .flatMap(token -> bookService.createBook(token, book.getData()))
        );
    }

    @PostMapping(value = "/delete")
    public JsonResponse<Long> deleteBook(@RequestBody JsonRequest<Long> id, HttpServletRequest request){
        return new JsonResponse<>(id.getRequestId(), getAuthToken(request)
                .flatMap(token -> bookService.deleteBook(token, id.getData()))
                );
    }
    @PostMapping(value = "/countByAuthor")
    public JsonResponse<Map<String, Integer>>  getCountByAuthor(JsonRequest<?> req, HttpServletRequest request){
        return new JsonResponse<>(req.getRequestId(), getAuthToken(request)
                .flatMap(token -> bookService.getCountByAuthor(token))
                );
    }

    private Result<AuthToken> getAuthToken(HttpServletRequest request) {
        AuthToken authToken = sessionService.getAuthToken(request);
        if (authToken == null) {
            return error(En_ResultStatus.SESSION_NOT_FOUND);
        }
        return ok(authToken);
    }
}