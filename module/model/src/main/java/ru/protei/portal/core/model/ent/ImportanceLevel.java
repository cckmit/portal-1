package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author michael
 */

@JdbcEntity(table = "importance_level")
public class ImportanceLevel implements Serializable {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Integer id;

    @JdbcColumn(name = "code")
    private String code;

    @JdbcColumn(name = "info")
    private String info;

    @JdbcColumn(name = "reaction_time")
    private Long reactionTime;

    @JdbcColumn(name = "temporary_solution_time")
    private Long temporarySolutionTime;

    @JdbcColumn(name = "full_solution_time")
    private Long fullSolutionTime;

    @JdbcColumn(name = "color")
    private String color;

    public ImportanceLevel() {}

    public ImportanceLevel(Integer id) {
        this.id = id;
    }

    public ImportanceLevel(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    public ImportanceLevel(Integer id, String code, String info) {
        this.id = id;
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return this.code;
    }

    public Integer getId() {
        return this.id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getReactionTime() {
        return reactionTime;
    }

    public Long getTemporarySolutionTime() {
        return temporarySolutionTime;
    }

    public Long getFullSolutionTime() {
        return fullSolutionTime;
    }

    public void setReactionTime(Long reactionTime) {
        this.reactionTime = reactionTime;
    }

    public void setTemporarySolutionTime(Long temporarySolutionTime) {
        this.temporarySolutionTime = temporarySolutionTime;
    }

    public void setFullSolutionTime(Long fullSolutionTime) {
        this.fullSolutionTime = fullSolutionTime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportanceLevel that = (ImportanceLevel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ImportanceLevel{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", info='" + info + '\'' +
                ", reactionTime=" + reactionTime +
                ", temporarySolutionTime=" + temporarySolutionTime +
                ", fullSolutionTime=" + fullSolutionTime +
                '}';
    }
}
