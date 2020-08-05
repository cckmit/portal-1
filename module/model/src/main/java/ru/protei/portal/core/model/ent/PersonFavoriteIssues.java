package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;

import static ru.protei.winter.jdbc.annotations.IdInsertMode.AUTOINCREMENT;

@JdbcEntity(table = "person_favorite_issues")
public class PersonFavoriteIssues implements Serializable {
    @JdbcId(name = "id", idInsertMode = AUTOINCREMENT)
    private Long id;

    @JdbcColumn(name = "person_id")
    private Long personId;

    @JdbcColumn(name = "case_object_id")
    private Long issueId;

    public PersonFavoriteIssues() {}

    public PersonFavoriteIssues(Long personId, Long issueId) {
        this.personId = personId;
        this.issueId = issueId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    @Override
    public String toString() {
        return "PersonFavoritesIssues{" +
                "id=" + id +
                ", personId=" + personId +
                ", issueId=" + issueId +
                '}';
    }
}
