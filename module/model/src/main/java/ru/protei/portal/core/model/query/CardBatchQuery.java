package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.core.model.struct.DateRange;

import java.util.List;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

public class CardBatchQuery extends BaseQuery {

    private List<Long> contractors;

    private List<Long> typeIds;

    private DateRange deadline;

    private List<Long> stateIds;

    private List<Integer> importanceIds;

    public CardBatchQuery() {
    }

    public List<Long> getContractors() {
        return contractors;
    }

    public void setContractors(List<Long> contractors) {
        this.contractors = contractors;
    }

    public List<Long> getTypeIds() {
        return typeIds;
    }

    public void setTypeIds(List<Long> typeIds) {
        this.typeIds = typeIds;
    }

    public DateRange getDeadline() {
        return deadline;
    }

    public void setDeadline(DateRange deadline) {
        this.deadline = deadline;
    }

    public List<Long> getStateIds() {
        return stateIds;
    }

    public void setStateIds(List<Long> stateIds) {
        this.stateIds = stateIds;
    }

    public List<Integer> getImportanceIds() {
        return importanceIds;
    }

    public void setImportanceIds(Iterable<ImportanceLevel> importanceIds) {
        this.importanceIds = importanceIds == null ? null
                                                   : toList(importanceIds, importanceLevel -> importanceLevel.getId().intValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardBatchQuery cardQuery = (CardBatchQuery) o;
        return Objects.equals(searchString, cardQuery.searchString)
                && Objects.equals(contractors, cardQuery.contractors)
                && Objects.equals(typeIds, cardQuery.typeIds)
                && Objects.equals(deadline, cardQuery.deadline)
                && Objects.equals(stateIds, cardQuery.stateIds)
                && Objects.equals(importanceIds, cardQuery.importanceIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchString, contractors, typeIds, deadline, stateIds, importanceIds);
    }
}
