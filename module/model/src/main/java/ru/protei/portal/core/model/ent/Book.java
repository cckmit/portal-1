package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.*;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Date;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JdbcEntity(table = "book")
public class Book {

    private static long count = 0;
    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
    @JsonProperty("id")
    private long id;

    @JdbcColumn(name = "book_name")
    @JsonProperty("book_name")
    @JsonAlias({"book_name", "bookName" })
    private String bookName;


    @JdbcColumn(name = "creator")
    @JsonProperty("creator")
    private String author;

    @JdbcColumn(name = "created")
    @JsonProperty("created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date created;

    @JdbcColumn(name = "noJsonData")
    @JsonIgnore
    private String noJsonData;

    @JdbcColumn(name = "nullData")
    @JsonProperty("null_data")
    @JsonAlias({"null_data", "nullData"})
    private String nullData;

    public Book() {
        incrementCount();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static long getCount() {
        return count;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getNoJsonData() {
        return noJsonData;
    }

    public void setNoJsonData(String noJsonData) {
        this.noJsonData = noJsonData;
    }

    public String getNullData() {
        return nullData;
    }

    public void setNullData(String nullData) {
        this.nullData = nullData;
    }

    protected static void incrementCount(){
        ++count;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id && Objects.equals(bookName, book.bookName) && Objects.equals(author, book.author) && Objects.equals(created, book.created) && Objects.equals(noJsonData, book.noJsonData) && Objects.equals(nullData, book.nullData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookName, author, created, noJsonData, nullData);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", created=" + created +
                ", noJsonData='" + noJsonData + '\'' +
                ", nullData='" + nullData + '\'' +
                '}';
    }
}
