package ru.protei.portal.core.model.query;

import java.util.List;
import java.util.Objects;

public class PcbOrderQuery extends BaseQuery {

    private List<Long> cardTypeIds;

    private List<Integer> typeIds;

    private List<Integer> stateIds;

    private List<Integer> promptnessIds;

    public PcbOrderQuery() {
    }

    public List<Long> getCardTypeIds() {
        return cardTypeIds;
    }

    public void setCardTypeIds(List<Long> cardTypeIds) {
        this.cardTypeIds = cardTypeIds;
    }

    public List<Integer> getTypeIds() {
        return typeIds;
    }

    public void setTypeIds(List<Integer> typeIds) {
        this.typeIds = typeIds;
    }

    public List<Integer> getStateIds() {
        return stateIds;
    }

    public void setStateIds(List<Integer> stateIds) {
        this.stateIds = stateIds;
    }

    public List<Integer> getPromptnessIds() {
        return promptnessIds;
    }

    public void setPromptnessIds(List<Integer> promptnessIds) {
        this.promptnessIds = promptnessIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PcbOrderQuery cardQuery = (PcbOrderQuery) o;
        return Objects.equals(cardTypeIds, cardQuery.cardTypeIds)
                && Objects.equals(typeIds, cardQuery.typeIds)
                && Objects.equals(stateIds, cardQuery.stateIds)
                && Objects.equals(promptnessIds, cardQuery.promptnessIds)
                && Objects.equals(searchString, cardQuery.searchString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardTypeIds, typeIds, stateIds, promptnessIds, searchString);
    }
}
