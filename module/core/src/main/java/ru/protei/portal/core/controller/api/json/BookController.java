package ru.protei.portal.core.controller.api.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.core.model.ent.Book;
import ru.protei.portal.core.service.edu.BookService;
import ru.protei.portal.core.service.edu.BookServiceImp;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/jsonApi/book", headers = "Accept=application/json",
        produces = "application/json", consumes = "application/json")
@EnableWebMvc
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping(value = "/allBooks")
    public List<Book> getAllBook() {
        return bookService.getAll();
    }


    @GetMapping(value = "/get/book")
    public Book getBookById(@RequestParam Long id, HttpServletResponse resp) throws IOException {
        Book book = bookService.getByBookId(id);
        if (book == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return book;
    }

    @PostMapping(value = "/create")
    public Book createBook(@RequestBody Book book) {
       return bookService.createBook(book);
    }

    @PostMapping(value = "/delete/{id}")
    public Long deleteBook(@PathVariable Long id, HttpServletResponse resp) throws IOException {
       long isDelete = bookService.deleteBook(id);
        if (isDelete == BookServiceImp.NOT_FOUND_BY_ID) {
           resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return id;
    }
}