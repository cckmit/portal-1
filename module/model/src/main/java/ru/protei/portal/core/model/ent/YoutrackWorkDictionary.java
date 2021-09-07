package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.List;

@JdbcEntity( table = "youtrack_work_dictionary" )
public class YoutrackWorkDictionary extends AuditableObject {

    @JdbcId( name = "id", idInsertMode = IdInsertMode.AUTO )
    private Long id;

    @JdbcColumn( name = "name" )
    private String name;

    @JdbcColumn(name = "type")
    @JdbcEnumerated( EnumType.ID )
    private En_YoutrackWorkType type;

    @JdbcManyToMany(linkTable = "youtrack_work_dictionary_to_youtrack_project",
            localLinkColumn = "youtrack_work_dictionary_id",
            remoteLinkColumn = "youtrack_project_id")
    private List<YoutrackProject> youtrackProjects;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public En_YoutrackWorkType getType() {
        return type;
    }

    public void setType(En_YoutrackWorkType type) {
        this.type = type;
    }

    public List<YoutrackProject> getYoutrackProjects() {
        return youtrackProjects;
    }

    public void setYoutrackProjects(List<YoutrackProject> youtrackProjects) {
        this.youtrackProjects = youtrackProjects;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    public interface Fields {
        String YOUTRACK_PROJECTS = "youtrackProjects";
    }

    public interface Columns {
        String DICTIONARY_TYPE = "type";
    }

    public static final String AUDIT_TYPE = "YoutrackWorkDictionary";

    @Override
    public String toString() {
        return "YoutrackWorkDictionary{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dictionaryType=" + type +
                ", youtrackProjects=" + youtrackProjects +
                '}';
    }
}
