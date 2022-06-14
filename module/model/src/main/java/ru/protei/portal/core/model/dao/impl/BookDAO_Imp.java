package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.protei.portal.core.model.dao.BookDAO;
import ru.protei.portal.core.model.ent.Book;
import ru.protei.portal.core.model.struct.Pair;

import java.util.List;


public class BookDAO_Imp extends PortalBaseJdbcDAO<Book> implements BookDAO {
    public static final String MAIN_TABLE = "book";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Book> getAllBooks() {
        String sql = "SELECT * FROM " + MAIN_TABLE;
        return jdbcTemplate.query(sql, new BookRowMapper());
    }

    @Override
    public Book getByBookId(Long id) {
        String sql = "SELECT * FROM " + MAIN_TABLE + " WHERE id = ?";
        Book book = jdbcTemplate.queryForObject(sql, new Object[]{id}, new BookRowMapper());
        return book;
    }

    @Override
    public Long createBook(Book book) {
        return persist(book);
    }

    @Override
    public Long deleteBook(Long id) {
        String sql = "DELETE FROM " + MAIN_TABLE + " WHERE id = ?";
        int count = jdbcTemplate.update(sql, id);
        if (count == 0){
            return (long) count;
        }
        return id;
    }

    @Override
    public List<Pair<String, Integer>> getCountBookByAuthor() {
            String sql = "Select displayname,count(book_name) as count from " + MAIN_TABLE  +
                    " join person p on book.creator_id = p.id group by displayname;";
            List<Pair<String, Integer>> pairs = jdbcTemplate.query(sql, (rs, i) -> {
                String authorName = rs.getString("displayname");
                Integer count = rs.getInt("count");
                Pair<String, Integer> pair = new Pair<>(authorName, count);
                return pair;
            });
            return pairs;
        }
}
