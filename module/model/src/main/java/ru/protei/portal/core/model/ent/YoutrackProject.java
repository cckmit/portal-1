package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;

import static ru.protei.portal.core.model.ent.YoutrackProject.Fields.*;

@JdbcEntity( table = "youtrack_project" )
public class YoutrackProject implements Serializable {

    @JdbcId( name = "id", idInsertMode = IdInsertMode.AUTO )
    private Long id;

    @JdbcColumn( name = YOUTRACK_ID )
    private String youtrackId;

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
    public String toString() {
        return "YoutrackProject{" +
                "id=" + id +
                ", youtrackId='" + youtrackId + '\'' +
                ", shortName='" + shortName + '\'' +
                '}';
    }
}
