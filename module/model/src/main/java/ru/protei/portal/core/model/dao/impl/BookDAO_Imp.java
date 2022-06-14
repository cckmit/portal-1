package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.protei.portal.core.model.dao.BookDAO;
import ru.protei.portal.core.model.ent.Book;
import ru.protei.portal.core.model.struct.Pair;

import java.util.List;


public class BookDAO_Imp extends PortalBaseJdbcDAO<Book> implements BookDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Pair<String, Integer>> getCountBookByAuthor() {
        String sql = "Select displayname,count(book_name) as count from " + getTableName() +
                " join person p on book.creator_id = p.id group by displayname;";
        List<Pair<String, Integer>> pairs = jdbcTemplate.query(sql, (rs, i) -> {
            String authorName = rs.getString("displayname");
            Integer count = rs.getInt("count");
            return new Pair<>(authorName, count);
        });
        return pairs;
    }
}
