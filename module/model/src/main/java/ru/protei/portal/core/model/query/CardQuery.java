package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseState;

public class CardQuery extends BaseQuery {

    private Long id;

    private Long typeId;

    private Long stateId;

    private CaseState state;

    private Long managerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public CaseState getState() {
        return state;
    }

    public void setState(CaseState state) {
        this.state = state;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public CardQuery(String searchString, En_SortField sortField, En_SortDir sortDir,
                     Long id, Long typeId, Long stateId, CaseState state, Long managerId) {
        super(searchString, sortField, sortDir);
        this.id = id;
        this.typeId = typeId;
        this.stateId = stateId;
        this.state = state;
        this.managerId = managerId;
    }
}
