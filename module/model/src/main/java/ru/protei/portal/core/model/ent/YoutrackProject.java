package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;
import java.util.Objects;

import static ru.protei.portal.core.model.ent.YoutrackProject.Fields.YOUTRACK_ID;

@JdbcEntity( table = "youtrack_project" )
public class YoutrackProject implements Serializable {

    // equals - Основным идентификатором является youtrackId
    @JdbcColumn( name = YOUTRACK_ID )
    private String youtrackId;

    @JdbcId( name = "id", idInsertMode = IdInsertMode.AUTO )
    private Long id;

    @JdbcColumn( name = "short_name" )
    private String shortName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getYoutrackId() {
        return youtrackId;
    }

    public void setYoutrackId(String youtrackId) {
        this.youtrackId = youtrackId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public interface Fields {
        String YOUTRACK_ID = "youtrack_id";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YoutrackProject)) return false;
        YoutrackProject project = (YoutrackProject) o;
        return youtrackId.equals(project.youtrackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(youtrackId);
    }

    @Override
    public String toString() {
        return "YoutrackProject{" +
                "id=" + id +
                ", youtrackId='" + youtrackId + '\'' +
                ", shortName='" + shortName + '\'' +
                '}';
    }
}
