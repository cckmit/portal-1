package ru.protei.portal.core.model.query;

/**
 * Фильтр по сотрудникам
 */
public class WorkerEntryQuery extends BaseQuery {
    private Long personId;
    private Integer active;
    private Long companyId;

    public WorkerEntryQuery() {}

    public WorkerEntryQuery(Long personId) {
        this.personId = personId;
    }

    public WorkerEntryQuery(Long companyId, Integer active) {
        this.companyId = companyId;
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
