package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.*;
import ru.protei.winter.jdbc.annotations.*;

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

 //   private static long count = 0;
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    @JsonProperty("id")
    private Long id;

    @JdbcColumn(name = "book_name")
    @JsonProperty("book_name")
    @JsonAlias({"book_name", "bookName" })
    private String bookName;

    @JdbcColumn(name = Columns.CREATOR_ID)
    @JsonProperty("creator_id")
    private Long creatorId;

    @JdbcJoinedColumn(localColumn =  Columns.CREATOR_ID, remoteColumn = "id",
            mappedColumn = "displayname", table = "person", sqlTableAlias = "p")
    @JsonProperty("creator")
    private String author;

    @JdbcColumn(name = "created")
    @JsonProperty("created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date created;

    @JdbcColumn(name = "no_json_data")
    @JsonIgnore
    private String noJsonData;

    @JdbcColumn(name = "null_data")
    @JsonProperty("null_data")
    @JsonAlias({"null_data", "nullData"})
    private String nullData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String  getAuthor() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public interface Columns {
        String CREATOR_ID = "creator_id";
    }
}
