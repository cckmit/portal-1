package ru.protei.portal.core.model.dao.impl;

import org.springframework.jdbc.core.RowMapper;
import ru.protei.portal.core.model.ent.Book;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet resultSet, int i) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getLong("id"));
        book.setBookName(resultSet.getString("book_name"));
        book.setCreatorId(resultSet.getLong("creator_id"));
        book.setCreated(resultSet.getDate("created"));
        book.setNoJsonData(resultSet.getString("no_json_data"));
        book.setNullData(resultSet.getString("null_data"));

        return book;
    }
}
