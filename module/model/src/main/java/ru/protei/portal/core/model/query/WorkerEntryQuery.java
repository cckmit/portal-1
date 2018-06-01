package ru.protei.portal.core.model.query;

/**
 * Фильтр по сотрудникам
 */
public class WorkerEntryQuery extends BaseQuery {
    private Long personId;
    private Integer active;

    public WorkerEntryQuery() {}

    public WorkerEntryQuery(Long personId, Integer active) {
        this.personId = personId;
        this.active = active;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}
