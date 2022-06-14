package ru.protei.portal.core.controller.api.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.core.controller.api.json.utils.JsonRequest;
import ru.protei.portal.core.controller.api.json.utils.JsonResponse;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Book;
import ru.protei.portal.core.service.BookService;
import ru.protei.portal.core.service.session.SessionService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
    private BookService bookService;

    @PostMapping(value = "/allBooks")
    public JsonResponse<List<Book>> getAllBook(@RequestBody JsonRequest<?> req , HttpServletRequest request) {
        AuthToken token = sessionService.getAuthToken(request);
        return new JsonResponse<>(req.getRequestId(), bookService.getAll(token));
    }

    @PostMapping(value = "/get/book")
    public JsonResponse<Book> getBookById(@RequestBody JsonRequest<Long> id, HttpServletRequest request) {
        AuthToken token = sessionService.getAuthToken(request);
        return new JsonResponse<>(id.getRequestId(), bookService.getByBookId(token, id.getData()));
    }

    @PostMapping(value = "/create")
    public JsonResponse<Long> createBook(@RequestBody JsonRequest<Book> book, HttpServletRequest request) {
        AuthToken token = sessionService.getAuthToken(request);
        return new JsonResponse<>(book.getRequestId(), bookService.createBook(token, book.getData()));
    }

    @PostMapping(value = "/delete")
    public JsonResponse<Long> deleteBook(@RequestBody JsonRequest<Long> id, HttpServletRequest request){
        AuthToken token = sessionService.getAuthToken(request);
        return new JsonResponse<>(id.getRequestId(), bookService.deleteBook(token, id.getData()));
    }
    @PostMapping(value = "/countByAuthor")
    public JsonResponse<Map<String, Integer>>  getCountByAuthor(JsonRequest<?> req, HttpServletRequest request){
        AuthToken authToken = sessionService.getAuthToken(request);
        return new JsonResponse<>(req.getRequestId(), bookService.getCountByAuthor(authToken));
    }
}