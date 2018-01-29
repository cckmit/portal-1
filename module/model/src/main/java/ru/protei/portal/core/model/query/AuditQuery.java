package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_AuditType;
import java.util.Date;

/**
 * Запрос для операций по аудиту
 */
public class AuditQuery extends BaseQuery {

    private Long id;
    private En_AuditType type;
    private Long creatorId;
    private Date from;
    private Date to;

    public AuditQuery() {}

    public AuditQuery(Long id) {
        setId(id);
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public En_AuditType getType() {
        return type;
    }

    public void setType( En_AuditType type ) {
        this.type = type;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId( Long creatorId ) {
        this.creatorId = creatorId;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom( Date from ) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo( Date to ) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "AuditQuery{" +
                "id=" + id +
                ", type=" + type +
                ", creatorId=" + creatorId +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
